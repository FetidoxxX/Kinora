<?php
include 'conexion.php';
$conexion = Conectar();

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $nombre = $_POST['nombre'];
    $descuento = $_POST['descuento'];
    $fecha = $_POST['fecha'];

    $query = "INSERT INTO dia (nombre, descuento, fecha) VALUES ('$nombre', '$descuento', '$fecha')";

    if ($conexion->query($query) === TRUE) {
        $response['status'] = 'success';
        $response['message'] = 'Promoción creada exitosamente';
    } else {
        $response['status'] = 'error';
        $response['message'] = 'Error al crear la promoción: ' . $conexion->error;
    }
} else {
    $response['status'] = 'error';
    $response['message'] = 'Método no permitido';
}

echo json_encode($response);
$conexion->close();
?>
