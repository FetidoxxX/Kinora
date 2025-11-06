<?php
// --- INICIO DE LA MODIFICACIÓN ---
// Ocultar errores y advertencias de PHP para asegurar una respuesta JSON limpia
error_reporting(0);
ini_set('display_errors', 0);
// --- FIN DE LA MODIFICACIÓN ---

header('Content-Type: application/json');
include('conexion.php');
$link = Conectar();

$response = ['success' => false, 'message' => 'Error desconocido.'];

if (!$link) {
  $response['message'] = 'Error de conexión a la Base de Datos.';
  echo json_encode($response);
  exit();
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
  $email = $_POST['email'] ?? '';

  if (empty($email)) {
    $response['message'] = 'Por favor, ingrese su correo electrónico.';
    echo json_encode($response);
    exit();
  }

  // 1. Verificar si el email existe
  $email_escaped = mysqli_real_escape_string($link, $email);
  $sql_check = "SELECT id_u FROM usuario WHERE email = '$email_escaped'";
  $result_check = mysqli_query($link, $sql_check);

  if (mysqli_num_rows($result_check) > 0) {
    // 2. Generar y guardar código
    $codigo = rand(100000, 999999); // Código de 6 dígitos
    $sql_update = "UPDATE usuario SET codigo = '$codigo' WHERE email = '$email_escaped'";

    if (mysqli_query($link, $sql_update)) {
      // 3. Enviar correo (Usando mail() de XAMPP/Mercury)
      $para = $email;
      $asunto = '🔐 Código de Recuperación - Kinora';
      $mensaje = "
            <html>
            <head><title>Recuperación de Contraseña</title></head>
            <body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>
                <div style='max-width: 600px; margin: auto; background: #100F1E; color: white; padding: 30px; border-radius: 10px;'>
                    <h1 style='text-align: center; color: #FFFFFF;'>Recuperación de Contraseña</h1>
                    <p style='font-size: 16px; color: #FFFFFF;'>Hola,</p>
                    <p style='font-size: 16px; color: #FFFFFF;'>Has solicitado restablecer tu contraseña para Kinora. Usa el siguiente código:</p>
                    <div style='background: #383180; border-radius: 8px; text-align: center; padding: 20px; margin: 25px 0;'>
                        <div style='font-size: 14px; color: #AFAFAF; margin-bottom: 8px;'>Tu código de verificación</div>
                        <div style='font-size: 36px; font-weight: bold; color: #FFFFFF; letter-spacing: 5px;'>" . $codigo . "</div>
                    </div>
                    <p style='font-size: 14px; color: #AFAFAF; text-align: center;'>Este código es válido por 15 minutos.</p>
                </div>
            </body>
            </html>";

      // Encabezados
      $headers = "MIME-Version: 1.0" . "\r\n";
      $headers .= "Content-type: text/html; charset=UTF-8" . "\r\n";
      $headers .= "From: Soporte Kinora <soporte@kinora.com>" . "\r\n";

      if (mail($para, $asunto, $mensaje, $headers)) {
        $response['success'] = true;
        $response['message'] = 'Código enviado a su correo. Revise su bandeja de entrada (y spam).';
      } else {
        $response['message'] = 'Usuario encontrado, pero falló el envío del correo. (Revise Mercury/XAMPP).';
      }
    } else {
      $response['message'] = 'Error al guardar el código en la BD.';
    }
  } else {
    $response['message'] = 'El correo electrónico no se encuentra registrado.';
  }
} else {
  $response['message'] = 'Método no permitido.';
}

mysqli_close($link);
echo json_encode($response);
?>