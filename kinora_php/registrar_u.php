<?php
$conexion = mysqli_connect("localhost", "root", "", "kinora_db");

if (!$conexion) {
  die("Error de conexión: " . mysqli_connect_error());
}

// Detectar si la petición es GET o POST
if ($_SERVER['REQUEST_METHOD'] === 'GET') {

  // Obtener tipos de documento
  $sql = "SELECT id_tipo_doc, nombre_tipo FROM tipos_documento";
  $result = mysqli_query($conexion, $sql);

  $tipos = array();

  while ($row = mysqli_fetch_assoc($result)) {
    $tipos[] = $row;
  }

  echo json_encode($tipos);

} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
  // Aquí más adelante implementaremos el registro del usuario
  // ejemplo:
  // registrarUsuario($conexion, $_POST);

  echo json_encode(array("status" => "pendiente", "mensaje" => "Función de registro aún no implementada"));
}

mysqli_close($conexion);
?>