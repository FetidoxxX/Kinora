<?php
include('conexion.php');
$link = Conectar();
$user = $_REQUEST['usuario'];
$pass = $_REQUEST['clave'];

if (empty($user) || empty($pass)) {
  echo "ERROR 1";
} else {
  $sql = "SELECT id_u, rol_id, nombre, email, usuario, id_estado_usuario 
          FROM usuario 
          WHERE usuario = '$user' AND clave = '$pass'";

  $res = mysqli_query($link, $sql);
  if ($res && $res->num_rows > 0) {
    $row = $res->fetch_assoc();

    if ($row['id_estado_usuario'] == 1) {
      $data = array();
      $data[] = $row;
      echo json_encode($data);
    } else {
      echo "ERROR 3";
    }
  } else {
    echo "ERROR 2";
  }
}
?>