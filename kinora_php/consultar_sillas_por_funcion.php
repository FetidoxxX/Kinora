<?php

header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();


if (!isset($_GET['idFuncion'])) {
    echo json_encode([]);
    die();
}
$idFuncion = $_GET['idFuncion'];

$sillas = array();
$sqlSillas = "SELECT 
    s.id_silla AS id_silla,
    s.fila AS fila_silla,
    s.columna AS columna_silla,
    s.sala_id_sala AS sala_silla
    FROM 
    Silla s
    JOIN
    funcion f ON f.sala_id_sala = s.sala_id_sala
    WHERE f.id_funcion = '$idFuncion'";

$resultadoSillas = mysqli_query($conn,$sqlSillas);
if ($resultadoSillas && mysqli_num_rows($resultadoSillas) > 0) {
    while ($row = mysqli_fetch_assoc($resultadoSillas)) {
        $sillas[] = $row;
    }
}

$sillasOcupadas = array();
$sqlSillasOcupadas = "SELECT 
    s.id_silla AS id_silla,
    s.fila AS fila_silla,
    s.columna AS columna_silla,
    s.sala_id_sala AS sala_silla
    FROM 
    Silla s
    JOIN
    factura fa ON fa.Silla_id_silla = s.id_silla
    JOIN
    funcion f ON f.id_funcion = fa.funcion_id_funcion
    WHERE f.id_funcion = '$idFuncion'";

$resultadoSillasOcupadas = mysqli_query($conn,$sqlSillasOcupadas);
if ($resultadoSillasOcupadas && mysqli_num_rows($resultadoSillasOcupadas) > 0) {
    while ($row = mysqli_fetch_assoc($resultadoSillasOcupadas)) {
        $sillasOcupadas[] = $row;
    }
}

$respuesta = array();
$respuesta['sillas'] = $sillas;
$respuesta['sillasOcupadas'] = $sillasOcupadas;
echo json_encode($respuesta);
mysqli_close($conn);
?>