<?php
include 'conexion.php';
$conexion = Conectar();

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_usuario = $_POST['id_usuario'];
    $numero_sala = $_POST['numero_sala'];
    $filas = intval($_POST['filas']);
    $columnas = intval($_POST['columnas']);

    // 1. Obtener el id_cine del usuario
    $sql_cine = "SELECT id_cine FROM cine WHERE usuario_id_u = '$id_usuario'";
    $result_cine = $conexion->query($sql_cine);

    if ($result_cine->num_rows > 0) {
        $row_cine = $result_cine->fetch_assoc();
        $id_cine = $row_cine['id_cine'];

        // 2. Verificar si el numero de sala ya existe para este cine
        $check_sql = "SELECT * FROM sala WHERE numero_sala = '$numero_sala' AND cine_id_cine = '$id_cine'";
        $check_result = $conexion->query($check_sql);

        if ($check_result->num_rows > 0) {
            $response['status'] = 'error';
            $response['message'] = 'El número de sala ya existe en este cine.';
        } else {
            // 3. Insertar la Sala
            $insert_sala = "INSERT INTO sala (numero_sala, cine_id_cine) VALUES ('$numero_sala', '$id_cine')";
            
            if ($conexion->query($insert_sala) === TRUE) {
                $id_sala = $conexion->insert_id;

                // 4. Generar Sillas
                $letras = range('A', 'Z');
                $error_sillas = false;
                for ($i = 0; $i < $filas; $i++) {
                    if ($i >= count($letras)) break; 
                    $fila = $letras[$i];
                    
                    for ($j = 1; $j <= $columnas; $j++) {
                        $columna = $j;
                        $insert_silla = "INSERT INTO silla (fila, columna, sala_id_sala) VALUES ('$fila', '$columna', '$id_sala')";
                        if (!$conexion->query($insert_silla)) {
                            $error_sillas = true;
                        }
                    }
                }

                if (!$error_sillas) {
                    $response['status'] = 'success';
                    $response['message'] = 'Sala creada exitosamente';
                } else {
                    $response['status'] = 'warning';
                    $response['message'] = 'Sala creada, pero hubo errores al generar algunas sillas.';
                }

            } else {
                $response['status'] = 'error';
                $response['message'] = 'Error al crear la sala: ' . $conexion->error;
            }
        }

    } else {
        $response['status'] = 'error';
        $response['message'] = 'Usuario no asociado a un cine.';
    }
} else {
    $response['status'] = 'error';
    $response['message'] = 'Método no permitido';
}

echo json_encode($response);
$conexion->close();
?>
