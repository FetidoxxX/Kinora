<?php
include 'conexion.php';
$conexion = Conectar();

$id_usuario = $_GET['id_usuario'];

// 1. Obtener el id_cine del usuario (Encargado)
$sql_cine = "SELECT id_cine FROM cine WHERE usuario_id_u = '$id_usuario'";
$result_cine = $conexion->query($sql_cine);

if ($result_cine->num_rows > 0) {
    $row_cine = $result_cine->fetch_assoc();
    $id_cine = $row_cine['id_cine'];

    // 2. Obtener las salas de ese cine con su capacidad
    $sql_salas = "SELECT s.id_sala, s.numero_sala, COUNT(si.id_silla) as capacidad
                  FROM sala s
                  LEFT JOIN silla si ON s.id_sala = si.sala_id_sala
                  WHERE s.cine_id_cine = '$id_cine. '
                  GROUP BY s.id_sala";
    
    // Fix: Remove extra dot and space in query above if present, cleaner version below
    $sql_salas = "SELECT s.id_sala, s.numero_sala, COUNT(si.id_silla) as capacidad
                  FROM sala s
                  LEFT JOIN silla si ON s.id_sala = si.sala_id_sala
                  WHERE s.cine_id_cine = '$id_cine'
                  GROUP BY s.id_sala";

    $result_salas = $conexion->query($sql_salas);
    $salas = array();

    while ($row = $result_salas->fetch_assoc()) {
        $salas[] = $row;
    }

    echo json_encode($salas);

} else {
    // Si el usuario no tiene cine asignado o no existe
    echo json_encode(array());
}

$conexion->close();
?>
