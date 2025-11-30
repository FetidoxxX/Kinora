<?php
include 'conexion.php';
$conexion = Conectar();

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_dia = $_POST['id_dia'];
    $nombre = $_POST['nombre'];
    $descuento = $_POST['descuento'];
    $fecha = $_POST['fecha'];

    $query = "UPDATE dia SET nombre='$nombre', descuento='$descuento', fecha='$fecha' WHERE id_dia='$id_dia'";

    if ($conexion->query($query) === TRUE) {
        $response['status'] = 'success';
        $response['message'] = 'Promoción actualizada exitosamente';
    } else {
        $response['status'] = 'error';
        $response['message'] = 'Error al actualizar la promoción: ' . $conexion->error;
    }
} else {
    $response['status'] = 'error';
    $response['message'] = 'Método no permitido';
}

echo json_encode($response);
$conexion->close();
?>
