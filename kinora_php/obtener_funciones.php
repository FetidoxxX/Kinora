<?php
include 'conexion.php';
$conexion = Conectar();

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    $id_usuario = $_GET['id_usuario'];

    // 1. Obtener el id_cine del usuario
    $sql_cine = "SELECT id_cine FROM cine WHERE usuario_id_u = '$id_usuario'";
    $result_cine = $conexion->query($sql_cine);

    if ($result_cine->num_rows > 0) {
        $row_cine = $result_cine->fetch_assoc();
        $id_cine = $row_cine['id_cine'];

        // 2. Obtener funciones con detalles
        $sql = "SELECT f.id_funcion, f.precio_base, f.fecha_hora, 
                       p.nombre AS nombre_pelicula, p.id_pelicula,
                       s.numero_sala, s.id_sala, 
                       (SELECT COUNT(*) FROM silla WHERE sala_id_sala = s.id_sala) AS capacidad,
                       d.nombre AS nombre_dia, d.descuento, d.id_dia
                FROM funcion f
                JOIN pelicula p ON f.pelicula_id_pelicula = p.id_pelicula
                JOIN sala s ON f.sala_id_sala = s.id_sala
                LEFT JOIN dia d ON f.dia_id_dia = d.id_dia
                WHERE s.cine_id_cine = '$id_cine'
                ORDER BY f.fecha_hora DESC";

        $result = $conexion->query($sql);
        $funciones = array();

        while ($row = $result->fetch_assoc()) {
            // Calcular precio final si hay descuento
            $precio_base = floatval($row['precio_base']);
            $descuento = $row['descuento'] ? floatval($row['descuento']) : 0;
            $precio_final = $precio_base - ($precio_base * ($descuento / 100));

            $row['precio_final'] = $precio_final;
            $funciones[] = $row;
        }
        echo json_encode($funciones);

    } else {
        echo json_encode(array()); // Usuario no tiene cine o no encontrado
    }
} else {
    echo json_encode(array("error" => "Metodo no permitido"));
}
$conexion->close();
?>
