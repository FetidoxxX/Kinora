<?php
header('Content-Type: application/json');

require_once 'conexion.php';
$conn = Conectar();

$sql = "SELECT id_tipo, tipo FROM tipo";

$result = mysqli_query($conn, $sql);

$tipos = array();

if ($result && mysqli_num_rows($result) > 0) {
    while($row = mysqli_fetch_assoc($result)) {
        $tipos[] = $row;
    }
}

echo json_encode($tipos);

mysqli_close($conn);

?>