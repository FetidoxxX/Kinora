<?php
header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();

if (isset($_POST['id_actor']) && isset($_POST['nuevo_nombre']) && isset($_POST['nuevo_apellido'])) {
    
    $id_actor = $_POST['id_actor'];
    $nuevo_nombre = $_POST['nuevo_nombre'];
    $nuevo_apellido = $_POST['nuevo_apellido'];

    $id_actor = mysqli_real_escape_string($conn, $id_actor);
    $nuevo_nombre = mysqli_real_escape_string($conn, $nuevo_nombre);
    $nuevo_apellido = mysqli_real_escape_string($conn, $nuevo_apellido);

    $sql = "UPDATE actor SET nombre = '$nuevo_nombre', apellido = '$nuevo_apellido' WHERE id_actor = '$id_actor'";

    if (mysqli_query($conn, $sql)) {
        if (mysqli_affected_rows($conn) > 0) {
            echo json_encode(array("success" => true, "message" => "Actor actualizado correctamente."));
        } else {
            echo json_encode(array("success" => false, "message" => "Los datos del actor no cambiaron o el ID no existe."));
        }
    } else {
        echo json_encode(array("success" => false, "message" => "Error al ejecutar la consulta: " . mysqli_error($conn)));
    }

} else {
    echo json_encode(array("success" => false, "message" => "Faltan parámetros POST (id_actor, nuevo_nombre o nuevo_apellido)."));
}

mysqli_close($conn);
?>