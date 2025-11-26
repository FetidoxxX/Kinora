<?php
header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();

if (isset($_POST['id_clasificacion']) && isset($_POST['nuevo_nombre'])) {
    
    $id_clasificacion = $_POST['id_clasificacion'];
    $nuevo_nombre = $_POST['nuevo_nombre'];

    $id_clasificacion = mysqli_real_escape_string($conn, $id_clasificacion);
    $nuevo_nombre = mysqli_real_escape_string($conn, $nuevo_nombre);

    $sql = "UPDATE clasificacion SET clasificacion = '$nuevo_nombre' WHERE id_clasificacion = '$id_clasificacion'";

    if (mysqli_query($conn, $sql)) {
        if (mysqli_affected_rows($conn) > 0) {
            echo json_encode(array("success" => true, "message" => "Clasificación actualizada correctamente."));
        } else {
            echo json_encode(array("success" => false, "message" => "La clasificación ya tiene ese nombre o el ID no existe."));
        }
    } else {
        echo json_encode(array("success" => false, "message" => "Error al ejecutar la consulta: " . mysqli_error($conn)));
    }

} else {
    echo json_encode(array("success" => false, "message" => "Faltan parámetros POST (id_clasificacion o nuevo_nombre)."));
}

mysqli_close($conn);
?>