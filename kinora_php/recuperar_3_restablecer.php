<?php
header('Content-Type: application/json');
include('conexion.php');
$link = Conectar();

$response = ['success' => false, 'message' => 'Error al cambiar la contraseña.'];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
  $email = $_POST['email'] ?? '';
  $codigo = $_POST['codigo'] ?? '';
  $nueva_clave = $_POST['nueva_clave'] ?? '';

  if (empty($email) || empty($codigo) || empty($nueva_clave)) {
    $response['message'] = 'Todos los campos son requeridos.';
    echo json_encode($response);
    exit();
  }

  $email_escaped = mysqli_real_escape_string($link, $email);
  $codigo_escaped = mysqli_real_escape_string($link, $codigo);
  $clave_escaped = mysqli_real_escape_string($link, $nueva_clave);

  $sql_update = "UPDATE usuario 
                   SET clave = '$clave_escaped', codigo = NULL 
                   WHERE email = '$email_escaped' AND codigo = '$codigo_escaped' AND codigo IS NOT NULL";

  mysqli_query($link, $sql_update);

  if (mysqli_affected_rows($link) > 0) {
    $response['success'] = true;
    $response['message'] = 'Contraseña actualizada exitosamente.';
  } else {
    $response['message'] = 'Error: El código o email no coinciden, o el código ya fue usado.';
  }
}

mysqli_close($link);
echo json_encode($response);
?>