<?php
header('Content-Type: application/json');

require_once 'conexion.php'; 
$conn = Conectar();

//$URL_BASE_POSTERS = "http://192.168.80.25/Kinora/kinora_php/";
$URL_BASE_POSTERS = "http://10.0.2.2/kinora_php/";



$sql = "SELECT DISTINCT 
            p.id_pelicula, 
            p.nombre AS titulo, 
            p.poster 
        FROM 
            pelicula p
        JOIN 
            funcion f ON p.id_pelicula = f.pelicula_id_pelicula 
        WHERE
            f.fecha_hora >= NOW()
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