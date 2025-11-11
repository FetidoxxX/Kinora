<?php
header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();

$sql = "SELECT id_pelicula, nombre FROM pelicula";

$result = mysqli_query($conn, $sql);

$peliculas = array();

if ($result && mysqli_num_rows($result) > 0) {
    while($row = mysqli_fetch_assoc($result)) {
        $peliculas[] = $row;
    }
}

echo json_encode($peliculas);

mysqli_close($conn);

?>
