<?php
header('Content-Type: application/json; charset=utf-8');
require_once 'conexion.php';
$conn = Conectar();

// helpers
function send_json($arr) {
    echo json_encode($arr);
    exit;
}

function get_id_by($conn, $table, $field, $value) {
    $value = trim($value);
    if ($value === '') return null;
    $v = mysqli_real_escape_string($conn, $value);
    $res = mysqli_query($conn, "SELECT * FROM `$table` WHERE `$field` = '$v' LIMIT 1");
    if ($res && $row = mysqli_fetch_assoc($res)) {
        foreach ($row as $k => $val) {
            if (stripos($k, 'id_') === 0) return $val;
        }
        return array_values($row)[0];
    }
    return null;
}

function get_or_create($conn, $table, $field, $value) {
    $value = trim($value);
    if ($value === '') return null;
    $safe = mysqli_real_escape_string($conn, $value);
    $res = mysqli_query($conn, "SELECT * FROM `$table` WHERE `$field` = '$safe' LIMIT 1");
    if ($res && $row = mysqli_fetch_assoc($res)) {
        foreach ($row as $k => $v) if (stripos($k, 'id_') === 0) return intval($v);
        return intval(array_values($row)[0]);
    } else {
        $ins = "INSERT INTO `$table` (`$field`) VALUES ('$safe')";
        if (!mysqli_query($conn, $ins)) {
            return null;
        }
        return intval(mysqli_insert_id($conn));
    }
}

$action = isset($_GET['action']) ? $_GET['action'] : '';

switch ($action) {

    case 'directores':
        $res = mysqli_query($conn, "SELECT nombre FROM director ORDER BY nombre ASC");
        $arr = [];
        while ($row = mysqli_fetch_assoc($res)) $arr[] = $row;
        send_json($arr);
        break;

    case 'generos':
        $res = mysqli_query($conn, "SELECT genero AS nombre FROM genero ORDER BY genero ASC");
        $arr = [];
        while ($row = mysqli_fetch_assoc($res)) $arr[] = $row;
        send_json($arr);
        break;

    case 'tipos':
        $res = mysqli_query($conn, "SELECT tipo AS nombre FROM tipo ORDER BY tipo ASC");
        $arr = [];
        while ($row = mysqli_fetch_assoc($res)) $arr[] = $row;
        send_json($arr);
        break;

    case 'clasificaciones':
        $res = mysqli_query($conn, "SELECT clasificacion AS nombre FROM clasificacion ORDER BY clasificacion ASC");
        $arr = [];
        while ($row = mysqli_fetch_assoc($res)) $arr[] = $row;
        send_json($arr);
        break;

    case 'actores':
        $res = mysqli_query($conn, "SELECT nombre FROM actor ORDER BY nombre ASC");
        $arr = [];
        while ($row = mysqli_fetch_assoc($res)) $arr[] = $row;
        send_json($arr);
        break;

    case 'crear_pelicula':
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            send_json(['status'=>'error','error'=>'Use POST']);
        }

        $titulo = isset($_POST['titulo']) ? trim($_POST['titulo']) : '';
        $sinopsis = isset($_POST['sinopsis']) ? trim($_POST['sinopsis']) : '';
        $director = isset($_POST['director']) ? trim($_POST['director']) : '';
        $genero = isset($_POST['genero']) ? trim($_POST['genero']) : '';
        $tipo = isset($_POST['tipo']) ? trim($_POST['tipo']) : '';
        $clasificacion = isset($_POST['clasificacion']) ? trim($_POST['clasificacion']) : '';
        $actores = isset($_POST['actores']) ? trim($_POST['actores']) : '';

        if ($titulo === '') {
            send_json(['status'=>'error','error'=>'Titulo requerido']);
        }

        // procesar poster base64 (opcional)
        $poster_db_value = '1'; // compatibilidad con tu proyecto
        if (!empty($_POST['poster']) && !empty($_POST['poster_name'])) {
            $posterBase64 = $_POST['poster'];
            $posterNameRaw = $_POST['poster_name'];
            // sanitizar y forzar extension .jpg si no hay
            $posterName = preg_replace('/[^A-Za-z0-9_\-\.]/', '_', $posterNameRaw);
            if (!preg_match('/\.(jpg|jpeg|png)$/i', $posterName)) {
                $posterName .= '.jpg';
            }
            $posterData = base64_decode($posterBase64);
            if ($posterData !== false) {
                $uploadDir = __DIR__ . '/uploads/';
                if (!is_dir($uploadDir)) mkdir($uploadDir, 0755, true);
                $savePath = $uploadDir . $posterName;
                // si ya existe, añadir sufijo timestamp
                if (file_exists($savePath)) {
                    $posterName = pathinfo($posterName, PATHINFO_FILENAME) . '_' . time() . '.' . pathinfo($posterName, PATHINFO_EXTENSION);
                    $savePath = $uploadDir . $posterName;
                }
                // validar que sea imagen básica (opcional): usar getimagesizefromstring
                $imgInfo = @getimagesizefromstring($posterData);
                if ($imgInfo !== false) {
                    if (file_put_contents($savePath, $posterData) !== false) {
                        // ruta relativa para guardar en DB (ajusta si tu acceso web necesita otra ruta)
                        $poster_db_value = 'uploads/' . $posterName;
                    }
                }
            }
        }



        $idDirector = ($director !== '') ? get_or_create($conn, 'director', 'nombre', $director) : null;
        $idGenero = ($genero !== '') ? get_or_create($conn, 'genero', 'genero', $genero) : null;
        $idTipo = ($tipo !== '') ? get_or_create($conn, 'tipo', 'tipo', $tipo) : null;
        $idClasif = ($clasificacion !== '') ? get_or_create($conn, 'clasificacion', 'clasificacion', $clasificacion) : null;

        $dirVal = is_null($idDirector) ? "NULL" : intval($idDirector);
        $genVal = is_null($idGenero) ? "NULL" : intval($idGenero);
        $tipoVal = is_null($idTipo) ? "NULL" : intval($idTipo);
        $clasVal = is_null($idClasif) ? "NULL" : intval($idClasif);
        $sinopsisSafe = mysqli_real_escape_string($conn, $sinopsis);
        $tituloSafe = mysqli_real_escape_string($conn, $titulo);

        // preparar valor para SQL (si poster_db_value == '1' mantenemos '1' para compatibilidad)
        $posterSqlValue = ($poster_db_value === '1') ? "'1'" : "'" . mysqli_real_escape_string($conn, $poster_db_value) . "'";


        $sql = "INSERT INTO pelicula (nombre, sinopsis, director_id_director, genero_id_genero, clasificacion_id_clasificacion, tipo_id_tipo, poster, id_estado_pelicula, id_cine)
                VALUES ('$tituloSafe', '$sinopsisSafe', $dirVal, $genVal, $clasVal, $tipoVal, $posterSqlValue, '1', '1')";

        if (!mysqli_query($conn, $sql)) {
            send_json(['status'=>'error','error'=>mysqli_error($conn),'query'=>$sql]);
        }

        $newId = mysqli_insert_id($conn);

        $actorList = array_filter(array_map('trim', explode(',', $actores)), function($v){ return $v !== ''; });
        foreach ($actorList as $actorName) {
            $actorSafe = mysqli_real_escape_string($conn, $actorName);
            $resA = mysqli_query($conn, "SELECT id_actor FROM actor WHERE nombre = '$actorSafe' LIMIT 1");
            if ($resA && $rowA = mysqli_fetch_assoc($resA)) {
                $idActor = intval($rowA['id_actor']);
            } else {
                if (!mysqli_query($conn, "INSERT INTO actor (nombre) VALUES ('$actorSafe')")) {
                    continue;
                }
                $idActor = intval(mysqli_insert_id($conn));
            }
            mysqli_query($conn, "INSERT INTO pelicula_has_actor (pelicula_id_pelicula, actor_id_actor) VALUES ('$newId', $idActor)");
        }

        send_json(['status'=>'success','id_pelicula'=>$newId]);
        break;

    case 'actualizar':
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        send_json(['status'=>'error','error'=>'Use POST']);
    }
    $id = isset($_POST['id']) ? trim($_POST['id']) : '';
    $titulo = isset($_POST['titulo']) ? trim($_POST['titulo']) : '';
    $sinopsis = isset($_POST['sinopsis']) ? trim($_POST['sinopsis']) : '';
    $director = isset($_POST['director']) ? trim($_POST['director']) : '';
    $genero = isset($_POST['genero']) ? trim($_POST['genero']) : '';
    $tipo = isset($_POST['tipo']) ? trim($_POST['tipo']) : '';
    $clasificacion = isset($_POST['clasificacion']) ? trim($_POST['clasificacion']) : '';
    $actores = isset($_POST['actores']) ? trim($_POST['actores']) : '';

    if ($id === '' || $titulo === '') {
        send_json(['status'=>'error','error'=>'Faltan parametros id o titulo']);
    }

    // procesar poster base64 (opcional)
    $poster_db_value = null;
    if (!empty($_POST['poster']) && !empty($_POST['poster_name'])) {
        $posterBase64 = $_POST['poster'];
        $posterNameRaw = $_POST['poster_name'];
        $posterName = preg_replace('/[^A-Za-z0-9_\-\.]/', '_', $posterNameRaw);
        if (!preg_match('/\.(jpg|jpeg|png)$/i', $posterName)) {
            $posterName .= '.jpg';
        }
        $posterData = base64_decode($posterBase64);
        if ($posterData !== false) {
            $uploadDir = __DIR__ . '/uploads/';
            if (!is_dir($uploadDir)) mkdir($uploadDir, 0755, true);
            $savePath = $uploadDir . $posterName;
            if (file_exists($savePath)) {
                $posterName = pathinfo($posterName, PATHINFO_FILENAME) . '_' . time() . '.' . pathinfo($posterName, PATHINFO_EXTENSION);
                $savePath = $uploadDir . $posterName;
            }
            $imgInfo = @getimagesizefromstring($posterData);
            if ($imgInfo !== false) {
                if (file_put_contents($savePath, $posterData) !== false) {
                    $poster_db_value = 'uploads/' . $posterName;
                }
            }
        }
    }

    $actorList = array_filter(array_map('trim', explode(',', $actores)), function($v){ return $v !== ''; });

    $idDirector = get_id_by($conn, 'director', 'nombre', $director);
    $idGenero = get_id_by($conn, 'genero', 'genero', $genero);
    $idTipo = get_id_by($conn, 'tipo', 'tipo', $tipo);
    $idClasif = get_id_by($conn, 'clasificacion', 'clasificacion', $clasificacion);

    $dirVal = is_null($idDirector) ? "NULL" : intval($idDirector);
    $genVal = is_null($idGenero) ? "NULL" : intval($idGenero);
    $tipoVal = is_null($idTipo) ? "NULL" : intval($idTipo);
    $clasVal = is_null($idClasif) ? "NULL" : intval($idClasif);
    $tituloSafe = mysqli_real_escape_string($conn, $titulo);
    $sinopsisSafe = mysqli_real_escape_string($conn, $sinopsis);
    $idSafe = mysqli_real_escape_string($conn, $id);

    $sqlUpdate = "UPDATE pelicula SET 
                    nombre = '$tituloSafe',
                    sinopsis = '$sinopsisSafe',
                    director_id_director = $dirVal,
                    genero_id_genero = $genVal,
                    tipo_id_tipo = $tipoVal,
                    clasificacion_id_clasificacion = $clasVal";

    if (!is_null($poster_db_value)) {
        $posterEsc = mysqli_real_escape_string($conn, $poster_db_value);
        $sqlUpdate .= ", poster = '$posterEsc'";
    }

    $sqlUpdate .= " WHERE id_pelicula = '$idSafe'";

    if (!mysqli_query($conn, $sqlUpdate)) {
        send_json(['status'=>'error','error'=>mysqli_error($conn)]);
    }

    if (!mysqli_query($conn, "DELETE FROM pelicula_has_actor WHERE pelicula_id_pelicula = '$idSafe'")) {
        send_json(['status'=>'error','error'=>mysqli_error($conn)]);
    }

    foreach ($actorList as $actorName) {
        $actorSafe = mysqli_real_escape_string($conn, $actorName);
        $resA = mysqli_query($conn, "SELECT id_actor FROM actor WHERE nombre = '$actorSafe' LIMIT 1");
        if ($resA && $rowA = mysqli_fetch_assoc($resA)) {
            $idActor = intval($rowA['id_actor']);
            if (!mysqli_query($conn, "INSERT INTO pelicula_has_actor (pelicula_id_pelicula, actor_id_actor) VALUES ('$idSafe', $idActor)")) {
                send_json(['status'=>'error','error'=>mysqli_error($conn)]);
            }
        } else {
            continue;
        }
    }

    send_json(['status'=>'success']);
    break;


    default:
        send_json(['error'=>'Acción no válida']);
        break;
}

mysqli_close($conn);
?>
