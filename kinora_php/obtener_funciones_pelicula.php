<?php
header('Content-Type: application/json');

require_once 'conexion.php'; 
$conn = Conectar();

if (!isset($_GET['id_pelicula'])) {
    http_response_code(400);
    echo json_encode(array("success" => false, "message" => "ID de película no proporcionado."));
    exit;
}

$id_pelicula = mysqli_real_escape_string($conn, $_GET['id_pelicula']);
$fecha_filtro = isset($_GET['fecha']) ? mysqli_real_escape_string($conn, $_GET['fecha']) : date('Y-m-d');
$sql = "SELECT
            f.id_funcion,
            f.precio_base,
            f.fecha_hora,
            c.id_cine,
            c.nombre AS nombreCine
        FROM
            funcion f
        JOIN
            sala s ON f.sala_id_sala = s.id_sala
        JOIN
            cine c ON s.cine_id_cine = c.id_cine
        WHERE
            f.pelicula_id_pelicula = '$id_pelicula'
            AND DATE(f.fecha_hora) = '$fecha_filtro'
        ORDER BY
            c.nombre ASC, f.fecha_hora ASC"; 

$result = mysqli_query($conn, $sql);
$funcionesPorCine = array();

if ($result) {
    while ($row = mysqli_fetch_assoc($result)) {
        $id_cine = $row['id_cine'];
        $nombre_cine = $row['nombreCine'];
        
        $timestamp = strtotime($row['fecha_hora']);
        $hora_funcion = date('H:i', $timestamp); 
        
        $funcion = array(
            'id_funcion' => $row['id_funcion'],
            'hora' => $hora_funcion,
            'precio' => (double)$row['precio_base']
        );
        
        if (!isset($funcionesPorCine[$id_cine])) {
            $funcionesPorCine[$id_cine] = array(
                'id_cine' => $id_cine,
                'nombreCine' => $nombre_cine,
                'horas' => array()
            );
        }
        
        $funcionesPorCine[$id_cine]['horas'][] = $funcion;
    }
    
    echo json_encode(array_values($funcionesPorCine));
    
} else {
    http_response_code(500); 
    echo json_encode(array("success" => false, "message" => "Error al ejecutar la consulta: " . mysqli_error($conn)));
}

mysqli_close($conn);
?>