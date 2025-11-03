<?php
include('conexion.php');
$link = Conectar();

$rol_id_cliente = 3;

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
  $sql = "SELECT id_tipo_doc, nombre_tipo FROM tipo_documento";
  $result = mysqli_query($link, $sql);

  $tipos = array();

  while ($row = mysqli_fetch_assoc($result)) {
    $tipos[] = $row;
  }

  header('Content-Type: application/json');
  echo json_encode($tipos);

} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {

  $nombre = $_POST['nombre'] ?? '';
  $email = $_POST['email'] ?? '';
  $clave = $_POST['clave'] ?? '';
  $documento = $_POST['documento'] ?? '';
  $id_tipo_doc = $_POST['id_tipo_doc'] ?? '';
  $usuario = $_POST['usuario'] ?? '';

  if (empty($nombre) || empty($email) || empty($clave) || empty($documento) || empty($id_tipo_doc) || empty($usuario)) {
    header('Content-Type: application/json');
    echo json_encode(array("status" => "error", "mensaje" => "Faltan campos esenciales."));
    exit;
  }


  $sql_check = "SELECT usuario, email, documento FROM usuario 
                  WHERE usuario = '$usuario' OR email = '$email' OR (documento = '$documento' AND id_tipo_doc = $id_tipo_doc)";
  $result_check = mysqli_query($link, $sql_check);

  if (mysqli_num_rows($result_check) > 0) {
    $row = mysqli_fetch_assoc($result_check);
    $mensaje_error = "";

    if ($row['usuario'] === $usuario) {
      $mensaje_error .= "El nombre de usuario '$usuario' ya está en uso.";
    } elseif ($row['email'] === $email) {
      $mensaje_error .= "El email '$email' ya está registrado.";
    } elseif ($row['documento'] === $documento) {
      $mensaje_error .= "El documento '$documento' ya está registrado.";
    } else {
      $mensaje_error .= "Datos de usuario duplicados.";
    }

    header('Content-Type: application/json');
    echo json_encode(array("status" => "error", "mensaje" => $mensaje_error));
    exit;
  }


  $sql_insert = "INSERT INTO usuario (rol_id, nombre, email, clave, documento, id_tipo_doc, usuario) 
            VALUES ($rol_id_cliente, '$nombre', '$email', '$clave', '$documento', $id_tipo_doc, '$usuario')";

  if (mysqli_query($link, $sql_insert)) {
    header('Content-Type: application/json');
    echo json_encode(array("status" => "success", "mensaje" => "Usuario registrado exitosamente."));
  } else {
    header('Content-Type: application/json');
    echo json_encode(array("status" => "error", "mensaje" => "Error al insertar en la base de datos."));
  }
}

mysqli_close($link);
?>