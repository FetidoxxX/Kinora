<?php
header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();

if (isset($_POST['id_genero']) && isset($_POST['nuevo_nombre'])) {
    
    $id_genero = $_POST['id_genero'];
    $nuevo_nombre = $_POST['nuevo_nombre'];

    $id_genero = mysqli_real_escape_string($conn, $id_genero);
    $nuevo_nombre = mysqli_real_escape_string($conn, $nuevo_nombre);

    $sql = "UPDATE genero SET genero = '$nuevo_nombre' WHERE id_genero = '$id_genero'";

    if (mysqli_query($conn, $sql)) {
        if (mysqli_affected_rows($conn) > 0) {
            echo json_encode(array("success" => true, "message" => "Género actualizado correctamente."));
        } else {
            echo json_encode(array("success" => false, "message" => "El género ya tiene ese nombre o el ID no existe."));
        }
    } else {
        echo json_encode(array("success" => false, "message" => "Error al ejecutar la consulta: " . mysqli_error($conn)));
    }

} else {
    echo json_encode(array("success" => false, "message" => "Faltan parámetros POST (id_genero o nuevo_nombre)."));
}

mysqli_close($conn);
?>