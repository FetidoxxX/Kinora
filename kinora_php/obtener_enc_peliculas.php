<?php
header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();

if (!isset($_GET['id_u'])) {
    http_response_code(400);
    echo json_encode(array("error" => "ID de usuario no proporcionado."));
    mysqli_close($conn);
    exit;
}

$id_usuario_encargado = $_GET['id_u'];

$id_usuario_encargado = mysqli_real_escape_string($conn, $id_usuario_encargado);

$sql = "
    SELECT
        p.id_pelicula,
        p.nombre
    FROM
        pelicula p
    INNER JOIN
        cine c ON p.id_cine = c.id_cine
    WHERE
        c.usuario_id_u = '$id_usuario_encargado' 
        AND p.id_estado_pelicula = 1
";

$result = mysqli_query($conn, $sql);

$peliculas = array();

if ($result && mysqli_num_rows($result) > 0) {
    while($row = mysqli_fetch_assoc($result)) {
        $peliculas[] = $row;
    }
}

echo json_encode($peliculas);

mysqli_close($conn);

?>