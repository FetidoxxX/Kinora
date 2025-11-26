<?php
header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();

if (isset($_POST['id_tipo']) && isset($_POST['nuevo_nombre'])) {
    
    $id_tipo = $_POST['id_tipo'];
    $nuevo_nombre = $_POST['nuevo_nombre'];

    $id_tipo = mysqli_real_escape_string($conn, $id_tipo);
    $nuevo_nombre = mysqli_real_escape_string($conn, $nuevo_nombre);

    $sql = "UPDATE tipo SET tipo = '$nuevo_nombre' WHERE id_tipo = '$id_tipo'";

    if (mysqli_query($conn, $sql)) {
        if (mysqli_affected_rows($conn) > 0) {
            echo json_encode(array("success" => true, "message" => "Tipo actualizado correctamente."));
        } else {
            echo json_encode(array("success" => false, "message" => "El tipo ya tiene ese nombre o el ID no existe."));
        }
    } else {
        echo json_encode(array("success" => false, "message" => "Error al ejecutar la consulta: " . mysqli_error($conn)));
    }

} else {
    echo json_encode(array("success" => false, "message" => "Faltan parámetros POST (id_tipo o nuevo_nombre)."));
}

mysqli_close($conn);
?>