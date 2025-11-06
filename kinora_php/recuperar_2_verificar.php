<?php
header('Content-Type: application/json');
include('conexion.php');
$link = Conectar();

$response = ['success' => false, 'message' => 'Código incorrecto o expirado.'];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
  $email = $_POST['email'] ?? '';
  $codigo = $_POST['codigo'] ?? '';

  if (empty($email) || empty($codigo)) {
    $response['message'] = 'Email o código no proporcionados.';
    echo json_encode($response);
    exit();
  }

  $email_escaped = mysqli_real_escape_string($link, $email);
  $codigo_escaped = mysqli_real_escape_string($link, $codigo);

  // Verificar el código (y que no sea nulo)
  // NOTA: No podemos verificar los 15 min sin un campo de timestamp en la BD.
  $sql = "SELECT id_u FROM usuario WHERE email = '$email_escaped' AND codigo = '$codigo_escaped' AND codigo IS NOT NULL";

  $result = mysqli_query($link, $sql);

  if (mysqli_num_rows($result) > 0) {
    $response['success'] = true;
    $response['message'] = 'Código verificado correctamente.';
  }
}

mysqli_close($link);
echo json_encode($response);
?>