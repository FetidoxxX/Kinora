<?php 
header('Content-Type: application/json; charset=utf-8');
require_once 'conexion.php';
$conn = Conectar();


if (!$conn) {
    echo json_encode(['status' => 'error', 'message' => 'Error de conexión a la base de datos']);
    exit;
} 

$action = isset($_POST['action']) ? $_POST['action'] : (isset($_GET['action']) ? $_GET['action'] : '');

switch ($action) {
    case 'crear_tipo':
        $nombre = isset($_POST['tipo']) ? trim($_POST['tipo']) : '';
        if ($nombre === '') {
            echo json_encode(['status' => 'error', 'message' => 'El nombre del tipo no puede estar vacío.']);
            exit;
        }

        $safe = mysqli_real_escape_string($conn, $nombre);
        $q = "SELECT id_tipo FROM tipo WHERE tipo = '$safe' LIMIT 1";
        $res = mysqli_query($conn, $q);
        if ($res === false) {
            echo json_encode(['status' => 'error', 'message' => 'Error en consulta de verificación: ' . mysqli_error($conn)]);
            exit;
        }
        if ($row = mysqli_fetch_assoc($res)) {
            echo json_encode(['status' => 'exists', 'message' => 'El tipo ya existe.', 'id' => intval($row['id_tipo'])]);
            exit;
        }

        $ins = "INSERT INTO tipo (tipo) VALUES ('$safe')";
        if (!mysqli_query($conn, $ins)) {
            echo json_encode(['status' => 'error', 'message' => 'Error al crear el tipo: ' . mysqli_error($conn)]);
            exit;
        }

        $new_id = mysqli_insert_id($conn);
        echo json_encode(['status' => 'success', 'message' => 'Tipo creado exitosamente.', 'id' => intval($new_id)]);
        exit;
        break;

    default:
        echo json_encode(['status' => 'error', 'message' => 'Acción no especificada o inválida.']);
        exit;
}
mysqli_close($conn);

?>