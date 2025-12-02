<?php
header('Content-Type: application/json');
require_once 'conexion.php';
$conn = Conectar();

$response = array(
    "clasificaciones" => array(),
    "generos" => array(),
    "tipos" => array()
);

// clasificaciones
$sql_clas = "SELECT id_clasificacion, clasificacion FROM clasificacion ORDER BY clasificacion ASC";
$res_clas = mysqli_query($conn, $sql_clas);
if ($res_clas && mysqli_num_rows($res_clas) > 0) {
    while ($row = mysqli_fetch_assoc($res_clas)) {
        $response['clasificaciones'][] = $row;
    }
}

// generos
$sql_gen = "SELECT id_genero, genero FROM genero ORDER BY genero ASC";
$res_gen = mysqli_query($conn, $sql_gen);
if ($res_gen && mysqli_num_rows($res_gen) > 0) {
    while ($row = mysqli_fetch_assoc($res_gen)) {
        $response['generos'][] = $row;
    }
}

// tipos
$sql_tipo = "SELECT id_tipo, tipo FROM tipo ORDER BY tipo ASC";
$res_tipo = mysqli_query($conn, $sql_tipo);
if ($res_tipo && mysqli_num_rows($res_tipo) > 0) {
    while ($row = mysqli_fetch_assoc($res_tipo)) {
        $response['tipos'][] = $row;
    }
}

echo json_encode($response);
mysqli_close($conn);
?>