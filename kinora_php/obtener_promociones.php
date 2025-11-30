<?php
include 'conexion.php';
$conexion = Conectar();

$query = "SELECT * FROM dia";
$resultado = $conexion->query($query);

$promociones = array();

while($fila = $resultado->fetch_assoc()) {
    $promociones[] = $fila;
}

echo json_encode($promociones);
$conexion->close();
?>
