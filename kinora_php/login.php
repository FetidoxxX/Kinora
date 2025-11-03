<?php
include('conexion.php');
$link = Conectar();
$user = $_REQUEST['usuario'];
$pass = $_REQUEST['contraseña'];

if (empty($user) || empty($pass)) {
  echo "ERROR 1";
} else {
  $sql = "select * from usuarios where usuario= '$user' and clave='$pass'";
  $res = mysqli_query($link, $sql);
  $data = array();
  $num = $res->num_rows;
  if ($num > 0) {
    while ($row = $res->fetch_assoc()) {
      $data[] = $row;
      //crear el json 
      echo json_encode($data);
    }
  } else {
    echo "ERROR 2";
  }
}


?>