<?php
header('Content-Type: application/json; charset=utf-8');
require_once 'conexion.php';
$conn = Conectar();

$action = isset($_GET['action']) ? $_GET['action'] : '';

switch ($action) {

    case 'directores':
        $res = mysqli_query($conn, "SELECT nombre FROM director ORDER BY nombre ASC");
        $arr = [];
        while ($row = mysqli_fetch_assoc($res)) $arr[] = $row;
        echo json_encode($arr);
        break;

    case 'generos':
        $res = mysqli_query($conn, "SELECT genero AS nombre FROM genero ORDER BY genero ASC");
        $arr = [];
        while ($row = mysqli_fetch_assoc($res)) $arr[] = $row;
        echo json_encode($arr);
        break;

    case 'tipos':
        $res = mysqli_query($conn, "SELECT tipo AS nombre FROM tipo ORDER BY tipo ASC");
        $arr = [];
        while ($row = mysqli_fetch_assoc($res)) $arr[] = $row;
        echo json_encode($arr);
        break;

    case 'clasificaciones':
        $res = mysqli_query($conn, "SELECT clasificacion AS nombre FROM clasificacion ORDER BY clasificacion ASC");
        $arr = [];
        while ($row = mysqli_fetch_assoc($res)) $arr[] = $row;
        echo json_encode($arr);
        break;

    case 'actores':
        $res = mysqli_query($conn, "SELECT nombre FROM actor ORDER BY nombre ASC");
        $arr = [];
        while ($row = mysqli_fetch_assoc($res)) $arr[] = $row;
        echo json_encode($arr);
        break;

    case 'actualizar':
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            echo json_encode(['status'=>'error','error'=>'Use POST']);
            break;
        }
        $id = isset($_POST['id']) ? trim($_POST['id']) : '';
        $titulo = isset($_POST['titulo']) ? trim($_POST['titulo']) : '';
        $director = isset($_POST['director']) ? trim($_POST['director']) : '';
        $genero = isset($_POST['genero']) ? trim($_POST['genero']) : '';
        $tipo = isset($_POST['tipo']) ? trim($_POST['tipo']) : '';
        $clasificacion = isset($_POST['clasificacion']) ? trim($_POST['clasificacion']) : '';
        $actores = isset($_POST['actores']) ? trim($_POST['actores']) : '';

        if ($id === '' || $titulo === '') {
            echo json_encode(['status'=>'error','error'=>'Faltan parametros id o titulo']);
            break;
        }

        $actorList = array_filter(array_map('trim', explode(',', $actores)), function($v){ return $v !== ''; });

        function get_id_by($conn, $table, $field, $value) {
            if ($value === '') return null;
            $v = mysqli_real_escape_string($conn, $value);
            $res = mysqli_query($conn, "SELECT * FROM $table WHERE $field = '$v' LIMIT 1");
            if ($res && $row = mysqli_fetch_assoc($res)) {
                foreach ($row as $k => $val) {
                    if (stripos($k, 'id_') === 0) return $val;
                }
                return array_values($row)[0];
            }
            return null;
        }

        $idDirector = get_id_by($conn, 'director', 'nombre', $director);
        $idGenero = get_id_by($conn, 'genero', 'genero', $genero);
        $idTipo = get_id_by($conn, 'tipo', 'tipo', $tipo);
        $idClasif = get_id_by($conn, 'clasificacion', 'clasificacion', $clasificacion);

        $dirVal = is_null($idDirector) ? "NULL" : intval($idDirector);
        $genVal = is_null($idGenero) ? "NULL" : intval($idGenero);
        $tipoVal = is_null($idTipo) ? "NULL" : intval($idTipo);
        $clasVal = is_null($idClasif) ? "NULL" : intval($idClasif);
        $tituloSafe = mysqli_real_escape_string($conn, $titulo);

        $sqlUpdate = "UPDATE pelicula SET 
                        nombre = '$tituloSafe',
                        director_id_director = $dirVal,
                        genero_id_genero = $genVal,
                        tipo_id_tipo = $tipoVal,
                        clasificacion_id_clasificacion = $clasVal
                    WHERE id_pelicula = '".mysqli_real_escape_string($conn, $id)."'";

        if (!mysqli_query($conn, $sqlUpdate)) {
            echo json_encode(['status'=>'error','error'=>mysqli_error($conn)]);
            break;
        }

        $idSafe = mysqli_real_escape_string($conn, $id);
        if (!mysqli_query($conn, "DELETE FROM pelicula_has_actor WHERE pelicula_id_pelicula = '$idSafe'")) {
            echo json_encode(['status'=>'error','error'=>mysqli_error($conn)]);
            break;
        }

        foreach ($actorList as $actorName) {
            $actorSafe = mysqli_real_escape_string($conn, $actorName);
            $resA = mysqli_query($conn, "SELECT id_actor FROM actor WHERE nombre = '$actorSafe' LIMIT 1");
            if ($resA && $rowA = mysqli_fetch_assoc($resA)) {
                $idActor = intval($rowA['id_actor']);
                if (!mysqli_query($conn, "INSERT INTO pelicula_has_actor (pelicula_id_pelicula, actor_id_actor) VALUES ('$idSafe', $idActor)")) {
                    echo json_encode(['status'=>'error','error'=>mysqli_error($conn)]);
                    break 2;
                }
            } else {
                continue;
            }
        }

        echo json_encode(['status'=>'success']);
        break;

    default:
        echo json_encode(['error'=>'Acción no válida']);
        break;
}

mysqli_close($conn);
?>
