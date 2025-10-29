<?php
function Conectar()
{
    $host = "localhost";
    $user = "root";
    $pass = "";
    $dbname = "kinora_db";
    $link = mysqli_connect($host, $user, $pass);
    mysqli_select_db($link, $dbname);
    return $link;
}
?>