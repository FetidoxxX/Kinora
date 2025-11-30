<?php
include 'conexion.php';
$conexion = Conectar();

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_sala = $_POST['id_sala'];
    $numero_sala = $_POST['numero_sala'];

    $sql = "UPDATE sala SET numero_sala = '$numero_sala' WHERE id_sala = '$id_sala'";

    if ($conexion->query($sql) === TRUE) {
        $response['status'] = 'success';
        $response['message'] = 'Sala actualizada exitosamente';
    } else {
        $response['status'] = 'error';
        $response['message'] = 'Error al actualizar la sala: ' . $conexion->error;
    }
} else {
    $response['status'] = 'error';
    $response['message'] = 'Método no permitido';
}

echo json_encode($response);
$conexion->close();
?>
