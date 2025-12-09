<?php
header('Content-Type: application/json');

require_once 'conexion.php'; 
$conn = Conectar();

if (!isset($_GET['id_pelicula'])) {
    http_response_code(400);
    echo json_encode(array("success" => false, "message" => "ID de película no proporcionado."));
    exit;
}

$id_pelicula = mysqli_real_escape_string($conn, $_GET['id_pelicula']);
//$URL_BASE_POSTERS = "http://192.168.80.25/Kinora/kinora_php/";
$URL_BASE_POSTERS = "http://10.0.2.2/kinora_php/";


$sql = "SELECT
            p.id_pelicula,
            p.nombre AS titulo,
            p.poster,
            p.sinopsis,
            d.nombre AS director,
            GROUP_CONCAT(DISTINCT CONCAT(a.nombre, ' ', a.apellido) SEPARATOR ', ') AS reparto,
            -- ⚠️ Corrección: Usar g.genero en lugar de g.nombre
            g.genero AS genero, 
            -- ⚠️ Corrección: Usar c.clasificacion en lugar de c.nombre
            c.clasificacion AS clasificacion,
            -- ⚠️ Corrección: Usar t.tipo en lugar de t.nombre
            t.tipo AS tipo
        FROM
            pelicula p
        JOIN
            director d ON p.director_id_director = d.id_director
        JOIN
            genero g ON p.genero_id_genero = g.id_genero
        JOIN
            clasificacion c ON p.clasificacion_id_clasificacion = c.id_clasificacion
        JOIN
            tipo t ON p.tipo_id_tipo = t.id_tipo
        LEFT JOIN 
            pelicula_has_actor pha ON p.id_pelicula = pha.pelicula_id_pelicula
        LEFT JOIN
            actor a ON pha.actor_id_actor = a.id_actor
        WHERE
            p.id_pelicula = '$id_pelicula'
        GROUP BY
            p.id_pelicula"; 

$result = mysqli_query($conn, $sql);
$detalle = array();

if ($result && mysqli_num_rows($result) > 0) {
    $row = mysqli_fetch_assoc($result);
    
    $detalle = array(
        'id_pelicula' => $row['id_pelicula'],
        'titulo' => $row['titulo'],
        'urlPoster' => $URL_BASE_POSTERS . $row['poster'],
        'sinopsis' => $row['sinopsis'] ?: 'Sinopsis no disponible', 
        'director' => $row['director'],
        'reparto' => $row['reparto'] ?: 'No especificado',
        'genero' => $row['genero'],
        'clasificacion' => $row['clasificacion'],
        'tipo' => $row['tipo']
    );
    
    echo json_encode($detalle);
    
} else {
    http_response_code(404); 
    echo json_encode(array("success" => false, "message" => "Película no encontrada o error en la consulta: " . mysqli_error($conn)));
}

mysqli_close($conn);
?>