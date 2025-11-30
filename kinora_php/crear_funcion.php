<?php
include 'conexion.php';
$conexion = Conectar();

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_pelicula = $_POST['id_pelicula'];
    $id_sala = $_POST['id_sala'];
    $precio_base = $_POST['precio_base'];
    $fecha_hora = $_POST['fecha_hora']; // Formato YYYY-MM-DD HH:MM:SS
    $id_dia = isset($_POST['id_dia']) && $_POST['id_dia'] != "" ? $_POST['id_dia'] : "NULL";

    $sql = "INSERT INTO funcion (pelicula_id_pelicula, sala_id_sala, precio_base, dia_id_dia, fecha_hora) 
            VALUES ('$id_pelicula', '$id_sala', '$precio_base', $id_dia, '$fecha_hora')";

    if ($conexion->query($sql) === TRUE) {
        $response['status'] = 'success';
        $response['message'] = 'Función creada exitosamente';
    } else {
        $response['status'] = 'error';
        $response['message'] = 'Error al crear la función: ' . $conexion->error;
    }
} else {
    $response['status'] = 'error';
    $response['message'] = 'Método no permitido';
}

echo json_encode($response);
$conexion->close();
?>
