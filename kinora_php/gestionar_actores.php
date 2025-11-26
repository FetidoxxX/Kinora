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
    case 'crear_actor':
        $nombre = isset($_POST['nombre']) ? trim($_POST['nombre']) : '';
        $apellido = isset($_POST['apellido']) ? trim($_POST['apellido']) : '';

        if ($nombre === '' || $apellido === '') {
            echo json_encode(['status' => 'error', 'message' => 'El nombre y el apellido del actor son requeridos.']);
            exit;
        }

        $safe_nombre = mysqli_real_escape_string($conn, $nombre);
        $safe_apellido = mysqli_real_escape_string($conn, $apellido);

        $q = "SELECT id_actor FROM actor WHERE nombre = '$safe_nombre' AND apellido = '$safe_apellido' LIMIT 1";
        $res = mysqli_query($conn, $q);
        
        if ($res === false) {
            echo json_encode(['status' => 'error', 'message' => 'Error en consulta de verificación: ' . mysqli_error($conn)]);
            exit;
        }
        
        if ($row = mysqli_fetch_assoc($res)) {
            echo json_encode(['status' => 'exists', 'message' => 'El actor ya existe.', 'id' => intval($row['id_actor'])]);
            exit;
        }

        $ins = "INSERT INTO actor (nombre, apellido) VALUES ('$safe_nombre', '$safe_apellido')";
        
        if (!mysqli_query($conn, $ins)) {
            echo json_encode(['status' => 'error', 'message' => 'Error al crear el actor: ' . mysqli_error($conn)]);
            exit;
        }

        $new_id = mysqli_insert_id($conn);
        echo json_encode(['status' => 'success', 'message' => 'Actor creado exitosamente.', 'id' => intval($new_id)]);
        exit;
        break;

    default:
        echo json_encode(['status' => 'error', 'message' => 'Acción no especificada o inválida.']);
        exit;
}
mysqli_close($conn);

?>