<?php
include 'conexion.php';
$conexion = Conectar();

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    $query = $_GET['query'];
    // Buscar peliculas que coincidan con el nombre, limite 5
    $sql = "SELECT id_pelicula, nombre FROM pelicula WHERE nombre LIKE '%$query%' LIMIT 5";
    $result = $conexion->query($sql);
    
    $peliculas = array();
    while ($row = $result->fetch_assoc()) {
        $peliculas[] = $row;
    }
    echo json_encode($peliculas);
} else {
    echo json_encode(array("error" => "Metodo no permitido"));
}
$conexion->close();
?>
