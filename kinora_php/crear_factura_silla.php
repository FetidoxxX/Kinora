<?php
header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();

$idSilla   = $_GET['id_silla'] ?? null;
$idFuncion = $_GET['id_funcion'] ?? null;
$idUsuario = $_GET['id_usuario'] ?? null;

$respuesta = [
    'success' => false,
    'message' => '',
    'id_factura' => null
];

if ($idSilla === null || $idFuncion === null || $idUsuario === null) {
    $respuesta['message'] = 'Faltan parámetros';
    echo json_encode($respuesta);
    exit;
}

$sqlCrearFactura = "INSERT INTO factura (
    usuario_id_usuario,
    funcion_id_funcion,
    precio_final,
    fecha_hora,
    Silla_id_silla
)
SELECT
    '$idUsuario'      AS usuario_id_usuario,
    f.id_funcion      AS funcion_id_funcion,
    CASE
        WHEN d.descuento IS NULL THEN f.precio_base
        ELSE f.precio_base * (1 - d.descuento / 100)
    END               AS precio_final,
    NOW()             AS fecha_hora,
    '$idSilla'        AS Silla_id_silla
FROM funcion f
LEFT JOIN dia d ON f.dia_id_dia = d.id_dia
WHERE f.id_funcion = '$idFuncion';
";

if (mysqli_query($conn, $sqlCrearFactura)) {
    $idFactura = mysqli_insert_id($conn);

    $respuesta['success'] = true;
    $respuesta['message'] = 'Factura creada correctamente';
    $respuesta['id_factura'] = $idFactura;
} else {
    $respuesta['message'] = 'Error al crear factura: ' . mysqli_error($conn);
}

mysqli_close($conn);

echo json_encode($respuesta);
