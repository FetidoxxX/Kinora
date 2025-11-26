<?php
header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();

$sql = "SELECT id_director, nombre, apellido FROM director";

$result = mysqli_query($conn, $sql);

$directores = array();

if ($result && mysqli_num_rows($result) > 0) {
    while($row = mysqli_fetch_assoc($result)) {
        $directores[] = $row;
    }
}

echo json_encode($directores);

mysqli_close($conn);

?>