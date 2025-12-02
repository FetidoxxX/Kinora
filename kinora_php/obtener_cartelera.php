<?php
header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();

$URL_BASE_POSTERS = "http://192.168.1.4/Kinora/kinora_php/";

$clasCsv = isset($_GET['clasificaciones']) ? trim($_GET['clasificaciones']) : '';
$genCsv  = isset($_GET['generos']) ? trim($_GET['generos']) : '';
$tipoCsv = isset($_GET['tipos']) ? trim($_GET['tipos']) : '';

function csv_to_ints($csv) {
    $arr = array_filter(array_map('trim', explode(',', $csv)));
    $out = array();
    foreach ($arr as $v) {
        $n = intval($v);
        if ($n > 0) $out[] = $n;
    }
    return $out;
}

$whereClauses = array();

// Clasificaciones
$clas_arr = csv_to_ints($clasCsv);
if (count($clas_arr) > 0) {
    $whereClauses[] = "p.clasificacion_id_clasificacion IN (" . implode(',', $clas_arr) . ")";
}

// Tipos
$tipo_arr = csv_to_ints($tipoCsv);
if (count($tipo_arr) > 0) {
    $whereClauses[] = "p.tipo_id_tipo IN (" . implode(',', $tipo_arr) . ")";
}

// Generos
$gen_arr = csv_to_ints($genCsv);
if (count($gen_arr) > 0) {
    $whereClauses[] = "p.genero_id_genero IN (" . implode(',', $gen_arr) . ")";
}

$whereBase = "f.fecha_hora >= NOW() AND p.id_estado_pelicula = 1";

$whereSql = "";
if (count($whereClauses) > 0) {
    $whereSql = " AND " . implode(" AND ", $whereClauses);
}

$sql = "SELECT DISTINCT
            p.id_pelicula, 
            p.nombre AS titulo, 
            p.poster 
        FROM 
            pelicula p
        JOIN 
            funcion f ON p.id_pelicula = f.pelicula_id_pelicula 
        WHERE
            $whereBase
            $whereSql
        ORDER BY
            p.nombre ASC";

$result = mysqli_query($conn, $sql);
$peliculas = array();

if ($result) {
    while ($row = mysqli_fetch_assoc($result)) {
        $pelicula = array(
            'id_pelicula' => $row['id_pelicula'],
            'titulo' => $row['titulo'],
            'urlPoster' => $URL_BASE_POSTERS . $row['poster']
        );
        $peliculas[] = $pelicula;
    }
    echo json_encode($peliculas);
} else {
    http_response_code(500);
    echo json_encode(array("success" => false, "message" => "Error al ejecutar la consulta: " . mysqli_error($conn)));
}

mysqli_close($conn);
?>
