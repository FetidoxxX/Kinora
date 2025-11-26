<?php
header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();

$sql = "SELECT id_actor, nombre, apellido FROM actor";

$result = mysqli_query($conn, $sql);

$actores = array();

if ($result && mysqli_num_rows($result) > 0) {
    while($row = mysqli_fetch_assoc($result)) {
        $actores[] = $row;
    }
}

echo json_encode($actores);

mysqli_close($conn);

?>