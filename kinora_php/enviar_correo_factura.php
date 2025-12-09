<?php


header('Content-Type: application/json; charset=utf-8');

$emailUsuario = $_POST['email_usuario'] ?? '';
$infoFactura = $_POST['info_factura'] ?? '';
$idFactura = $_POST['id_factura'] ?? '';

if (empty($emailUsuario) || empty($infoFactura) || empty($idFactura)) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'message' => 'Faltan datos para enviar el correo'
    ]);
    exit;
}

$asunto = "Confirmación de factura #$idFactura";

$cuerpo = "Gracias por tu compra en Kinora.\n\n";
$cuerpo .= "Detalles de la factura:\n";
$cuerpo .= $infoFactura . "\n\n";
$cuerpo .= "Número de factura: $idFactura\n";
$cuerpo .= "Fecha de emisión: " . date('d/m/Y H:i') . "\n\n";

$cuerpo .= "¡Disfruta la función!\n";

$fromEmail = 'postmaster@localhost';
$fromName = 'Kinora Cine';

$headers = "From: $fromName <$fromEmail>\r\n";
$headers .= "Reply-To: $fromEmail\r\n";
$headers .= "MIME-Version: 1.0\r\n";
$headers .= "Content-Type: text/plain; charset=UTF-8\r\n";

if (mail($emailUsuario, $asunto, $cuerpo, $headers)) {
    echo json_encode([
        'success' => true,
        'message' => 'Correo enviado correctamente'
    ]);
} else {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Error al enviar el correo'
    ]);
}
