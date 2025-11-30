<?php
include 'conexion.php';
$conexion = Conectar();

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_funcion = $_POST['id_funcion'];
    $id_pelicula = $_POST['id_pelicula'];
    $id_sala = $_POST['id_sala'];
    $precio_base = $_POST['precio_base'];
    $fecha_hora = $_POST['fecha_hora'];
    $id_dia = isset($_POST['id_dia']) && $_POST['id_dia'] != "" ? $_POST['id_dia'] : "NULL";

    $sql = "UPDATE funcion SET 
            pelicula_id_pelicula = '$id_pelicula', 
            sala_id_sala = '$id_sala', 
            precio_base = '$precio_base', 
            dia_id_dia = $id_dia, 
            fecha_hora = '$fecha_hora' 
            WHERE id_funcion = '$id_funcion'";

    if ($conexion->query($sql) === TRUE) {
        $response['status'] = 'success';
        $response['message'] = 'Función actualizada exitosamente';
    } else {
        $response['status'] = 'error';
        $response['message'] = 'Error al actualizar la función: ' . $conexion->error;
    }
} else {
    $response['status'] = 'error';
    $response['message'] = 'Método no permitido';
}

echo json_encode($response);
$conexion->close();
?>
