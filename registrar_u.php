<?php
// Incluir el archivo de conexión que contiene la función Conectar()
require_once 'conexion.php';

// Obtener la instancia de conexión usando la función Conectar()
$conexion = Conectar();

// Verificar si la conexión es válida. Si Conectar() retorna null o false, no se puede continuar.
if (!$conexion) {
  // Nota: Si la conexión falla, se detiene la ejecución inmediatamente.
  echo "ERROR DE CONEXIÓN: No se pudo establecer la conexión a la base de datos.";
  exit;
}

// -----------------------------------------------------------
// -- MANEJO DE PETICIONES (GET para Tipos, POST para Registro)
// -----------------------------------------------------------

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
  // 1. OBTENER TIPOS DE DOCUMENTO (Usado por la solicitud JsonArrayRequest en Android)

  $sql = "SELECT id_tipo_doc, nombre_tipo FROM tipos_documento";
  $result = mysqli_query($conexion, $sql);

  $tipos = array();

  if ($result) {
    while ($row = mysqli_fetch_assoc($result)) {
      $tipos[] = $row;
    }
  }

  // Devolver la lista de tipos en formato JSON
  echo json_encode($tipos);

} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
  // 2. REGISTRAR NUEVO USUARIO (Usado por la solicitud StringRequest en Android)

  // Recibir y sanitizar datos. 
  $documento = mysqli_real_escape_string($conexion, $_POST['documento']);
  $usuario = mysqli_real_escape_string($conexion, $_POST['usuario']);
  $nombre = mysqli_real_escape_string($conexion, $_POST['nombre']);
  $email = mysqli_real_escape_string($conexion, $_POST['email']);
  $contrasena = $_POST['contraseña'];

  // Convertir a entero para seguridad y consistencia con la base de datos (IDs numéricos)
  // Nota: Si el POST no tiene valor, intval() retorna 0.
  $id_tipo_doc = intval($_POST['id_tipo_doc']);
  $rol_id = intval($_POST['rol_id']);

  // Validaciones básicas 
  if (empty($documento) || $id_tipo_doc === 0 || $rol_id === 0 || empty($usuario) || empty($email) || empty($contrasena)) {
    echo "ERROR: Campos requeridos vacíos o ID de documento/rol inválido.";
    mysqli_close($conexion);
    exit;
  }

  // Hashing de la contraseña por seguridad
  $hashed_password = password_hash($contrasena, PASSWORD_DEFAULT);

  // --- 2.1. Verificar duplicados (documento, usuario o email) ---
  // Se añade la verificación por documento, ya que es UNIQUE en la BD.
  $check_sql = "SELECT COUNT(*) AS total FROM usuarios WHERE documento = '$documento' OR usuario = '$usuario' OR email = '$email'";
  $check_result = mysqli_query($conexion, $check_sql);

  if ($check_result) {
    $row = mysqli_fetch_assoc($check_result);

    if ($row['total'] > 0) {
      echo "Duplicate Entry: El documento, usuario o correo electrónico ya está registrado.";
      mysqli_close($conexion);
      exit;
    }
  } else {
    echo "Error al verificar duplicados: " . mysqli_error($conexion);
    mysqli_close($conexion);
    exit;
  }


  // --- 2.2. Insertar el nuevo usuario ---
  // Los IDs numéricos se pasan SIN comillas simples
  $insert_sql = "INSERT INTO usuarios (documento, id_tipo_doc, rol_id, usuario, nombre, email, contraseña, codigo) 
                 VALUES ('$documento', $id_tipo_doc, $rol_id, '$usuario', '$nombre', '$email', '$hashed_password', NULL)";

  if (mysqli_query($conexion, $insert_sql)) {
    // Éxito en el registro
    echo "Usuario Registrado";
  } else {
    // Error en la inserción
    echo "Error al registrar: " . mysqli_error($conexion);
  }
}

// Cerrar la conexión
mysqli_close($conexion);
?>