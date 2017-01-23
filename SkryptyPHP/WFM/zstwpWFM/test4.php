<?php
//header('Content-Type: application/json');
if(isset($_GET[temporaryServicemanLatitude])){
$data_temporaryServicemanLatitude = $_GET[temporaryServicemanLatitude];
}
if(isset($_POST[temporaryServicemanLongitude])){
$data_temporaryServicemanLongitude = $_GET[temporaryServicemanLongitude];
}
if(isset($_GET[servicemanID])){
$data_servicemanID = $_POST[servicemanID];
}
$filename = "zapisuGPS.txt";
$info = "Tutaj pojawi¹ siê koordynaty GPS";
$fh = fopen($filename, "w+");
fwrite($fh, $info);
fwrite($fh, $data_temporaryServicemanLatitude);
fwrite($fh, $data_temporaryServicemanLongitude);
fwrite($fh, $data_servicemanID);
fclose($fh);
?> 