<?php
header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();

if (!isset($_GET['nombre'])) {
    echo json_encode([]);
    die();
}
$nombre_buscado = $_GET['nombre'];

$sql = "SELECT 
            c.id_cine,
            c.direccion,
            c.nombre AS nombre_cine,
			u.id_u AS id_usuario,
            u.nombre AS nombre_usuario,
            u.email AS email_usuario,
            u.documento AS documento_usuario,
            c.telefono
        FROM 
            cine c
        JOIN 
            usuario u ON c.usuario_id_u = u.id_u
        WHERE 
            c.nombre LIKE '%$nombre_buscado%'";

$result = mysqli_query($conn, $sql);

$cines = array();
if ($result && mysqli_num_rows($result) > 0) {
    while($row = mysqli_fetch_assoc($result)) {
        $cines[] = $row;
    }
}

echo json_encode($cines);

mysqli_close($conn);

?>