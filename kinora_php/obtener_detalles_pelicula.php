<?php
header('Content-Type: application/json; charset=utf-8');

require_once 'conexion.php';
$conn = Conectar();

if (!isset($_GET['id_pelicula'])) {
    echo json_encode(['error' => 'No se proporcionó un ID de película.']);
    http_response_code(400);
    die();
}

$id_pelicula = mysqli_real_escape_string($conn, $_GET['id_pelicula']);

$sql_pelicula = "SELECT
                    p.nombre AS nombre,
                    d.nombre AS director,
                    g.genero AS genero,
                    t.tipo AS tipo,
                    cl.clasificacion AS clasificacion
                FROM
                    pelicula p
                LEFT JOIN
                    director d ON p.director_id_director = d.id_director
                LEFT JOIN
                    genero g ON p.genero_id_genero = g.id_genero
                LEFT JOIN
                    tipo t ON p.tipo_id_tipo = t.id_tipo
                LEFT JOIN
                    clasificacion cl ON p.clasificacion_id_clasificacion = cl.id_clasificacion
                WHERE
                    p.id_pelicula = '$id_pelicula'";

$result_pelicula = mysqli_query($conn, $sql_pelicula);
$pelicula_detalles = mysqli_fetch_assoc($result_pelicula);

if (!$pelicula_detalles) {
    echo json_encode(['error' => 'Película no encontrada.']);
    http_response_code(404);
    die();
}

$sql_actores = "SELECT
                    a.nombre
                FROM
                    actor a
                JOIN
                    pelicula_has_actor pha ON a.id_actor = pha.actor_id_actor
                WHERE
                    pha.pelicula_id_pelicula = '$id_pelicula'";

$result_actores = mysqli_query($conn, $sql_actores);
$actores = array();
while($row = mysqli_fetch_assoc($result_actores)) {
    $actores[] = $row['nombre'];
}

$respuesta_final = $pelicula_detalles;
$respuesta_final['actores'] = implode(', ', $actores);

echo json_encode($respuesta_final);

mysqli_close($conn);
?>
