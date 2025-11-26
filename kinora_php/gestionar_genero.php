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
    case 'crear_genero':
        $genero = isset($_POST['genero']) ? trim($_POST['genero']) : '';
        if ($genero === '') {
            echo json_encode(['status' => 'error', 'message' => 'El nombre del genero no puede estar vacío.']);
            exit;
        }

        $safe = mysqli_real_escape_string($conn, $genero);
        $q = "SELECT id_genero FROM genero WHERE genero = '$safe' LIMIT 1";
        $res = mysqli_query($conn, $q);
        if ($res === false) {
            echo json_encode(['status' => 'error', 'message' => 'Error en consulta de verificación: ' . mysqli_error($conn)]);
            exit;
        }
        if ($row = mysqli_fetch_assoc($res)) {
            echo json_encode(['status' => 'exists', 'message' => 'El genero ya existe.', 'id' => intval($row['id_genero'])]);
            exit;
        }

        $ins = "INSERT INTO genero (genero) VALUES ('$safe')";
        if (!mysqli_query($conn, $ins)) {
            echo json_encode(['status' => 'error', 'message' => 'Error al crear el genero: ' . mysqli_error($conn)]);
            exit;
        }

        $new_id = mysqli_insert_id($conn);
        echo json_encode(['status' => 'success', 'message' => 'genero creado exitosamente.', 'id' => intval($new_id)]);
        exit;
        break;

    default:
        echo json_encode(['status' => 'error', 'message' => 'Acción no especificada o inválida.']);
        exit;
}
mysqli_close($conn);

?>