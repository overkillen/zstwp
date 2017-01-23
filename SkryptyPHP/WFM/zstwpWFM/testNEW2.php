<?php
require_once 'db.php';
header('Content-Type: application/json');
class TestNEW2
{    
	public function run(){
	
	if(isset($_GET['ServicemanId'])){
		$service_man = $_GET['ServicemanId'];
		}
		$sql = $this->prepareSql($service_man);
	}
	
	private function prepareSql($servicemanID){
	
	$newTicet = "SELECT TicketId from Tickets ORDER BY TicketId DESC LIMIT 1";
	$resultNewTicetTicketId = Db::getInstance()->query($newTicet)->fetch_assoc()['TicketId'];
	
	$resultNewTicetTicketId = "101";
	
	//echo $resultNewTicetTicketId;
	
	$lastInfoAboutTicet = "SELECT * from Updates WHERE TicketId = '$resultNewTicetTicketId' ORDER BY UpdateId DESC LIMIT 1";
	$dataLastInfoAboutTicet = Db::getInstance()->query($lastInfoAboutTicet)->fetch_array(MYSQLI_NUM);
	//echo "dane z 2 zapytania";
	//echo $dataLastInfoAboutTicet[0];
	
	if($dataLastInfoAboutTicet[4] == null) {
			//wybieramy serwisanta najbli¿szego
			$corrdinatesDeviceId = "SELECT al.DeviceId from Alarms AS al WHERE al.TicketId = (SELECT up.TicketId from Updates AS up WHERE up.PersonId = '$servicemanID' ORDER BY up.PersonId DESC LIMIT 1)";
			$resultCorrdinatesDeviceId = Db::getInstance()->query($corrdinatesDeviceId)->fetch_assoc()['DeviceId'];
			
			$corrdinatesDatacenterIdLatitude = "SELECT da.Latitude, da.Longitude from Datacenters AS da WHERE da.DatacenterId = (SELECT de.DatacenterId from Devices AS de WHERE de.DeviceId = '$resultCorrdinatesDeviceId')";
			$resultCorrdinatesDatacenterId = Db::getInstance()->query($corrdinatesDatacenterIdLatitude)->fetch_array(MYSQLI_NUM);
		
			$GPSinfoServisants = "SELECT WorkerId, Latitude, Longitude from Workers WHERE IsActive = 1";
			//$GPSinfoServisants = "SELECT WorkerId, Latitude, Longitude from Workers";
			$dataGPSinfoServisants = Db::getInstance()->query($GPSinfoServisants);
			
			if($dataGPSinfoServisants === FALSE) { 
				die(mysql_error()); // TODO: better error handling
				};
			$minDistance = INF;
		
			while ($row = $dataGPSinfoServisants->fetch_array()) {
	
				$latitude_float = (float) $row["latitude"];
				$longitude_float = (float) $row["longitude"];

				$distance = $this->vincentyGreatCircleDistance($resultCorrdinatesDatacenterId[0], $resultCorrdinatesDatacenterId[1], $latitude_float, $longitude_float);

				if ($distance < $minDistance) {
				$minDistance = $distance;
				$driverIdWithMinDistanse = $row["WorkerId"];
				//echo $driverIdWithMinDistanse;
				}
			}
			
			//echo $driverIdWithMinDistanse;
			//echo "wybieram serwisanta";
			
			if($driverIdWithMinDistanse == $servicemanID){
			///TO DO MNIE ZRÓB AKTUALIZACJÊ W UPDADE
				$pkeyDataValue = "SELECT max(UpdateId) from Updates";
				$resultPkeyValue = Db::getInstance()->query($pkeyDataValue)->fetch_array(MYSQLI_NUM);
		
				$pkeyValue2 = $resultPkeyValue[0];
				
				$actualValue = "SELECT * from Updates WHERE UpdateId = '$pkeyValue2' ORDER BY PersonId DESC LIMIT 1";
				$resultActualValue = Db::getInstance()->query($actualValue)->fetch_array(MYSQLI_NUM);
				
				$pkeyValue2 = $resultPkeyValue[0] + 1;
				$id2 = $resultActualValue[1];
				$personId2 = $servicemanID;
				$msg2 = "IN_PROGRESS";
				//$msg = "test";
				//echo "dane";
				//echo $id2;
				//echo $pkeyValue2;
				
				//$sql = "INSERT INTO Updates Values (".$pkeyValue.",".$id.",".$personId.",now(),'".$msg."',4)";
				$sql2 = "INSERT INTO Updates Values (".$pkeyValue2.",".$id2.",".$personId2.",now(),'".$msg2."',4)";
				$resultWorkerId = Db::getInstance()->query($sql2);
				
				////////////////////////////////////////////////////////
				
				$data_ToME = utf8_encode('Yes');
				$data_message = utf8_encode('Damage: ');
				$data_isRepair = utf8_encode('No');
				
				$corrdinatesDeviceId = "SELECT al.DeviceId from Alarms AS al WHERE al.TicketId = (SELECT up.TicketId from Updates AS up WHERE up.PersonId = '$servicemanID' ORDER BY up.PersonId DESC LIMIT 1)";
				$resultCorrdinatesDeviceId = Db::getInstance()->query($corrdinatesDeviceId)->fetch_assoc()['DeviceId'];
		
				$corrdinatesDatacenterIdLatitude = "SELECT da.Latitude from Datacenters AS da WHERE da.DatacenterId = (SELECT de.DatacenterId from Devices AS de WHERE de.DeviceId = '$resultCorrdinatesDeviceId')";
				$resultCorrdinatesDatacenterIdLatitude = Db::getInstance()->query($corrdinatesDatacenterIdLatitude)->fetch_assoc()['Latitude'];
		
				$corrdinatesDatacenterIdLongitude = "SELECT da.Longitude from Datacenters AS da WHERE da.DatacenterId = (SELECT de.DatacenterId from Devices AS de WHERE de.DeviceId = '$resultCorrdinatesDeviceId')";
				$resultCorrdinatesDatacenterIdLongitude = Db::getInstance()->query($corrdinatesDatacenterIdLongitude)->fetch_assoc()['Longitude'];
		
				$alarmMessage = "SELECT al.Name from Alarms AS al WHERE al.DeviceId = $resultCorrdinatesDeviceId";
				$resultAlarmMessage = Db::getInstance()->query($alarmMessage)->fetch_assoc()['Name'];
		
				$infoDatacenter = "SELECT da.Name, da.Address, da.PostalCode, da.City  from Datacenters AS da WHERE da.DatacenterId = (SELECT de.DatacenterId from Devices AS de WHERE de.DeviceId = '$resultCorrdinatesDeviceId')";
				$resultInfoDatacenter = Db::getInstance()->query($infoDatacenter);
		
				$row = $resultInfoDatacenter->fetch_array(MYSQLI_NUM);
				
				$data_message = utf8_encode($data_message . ' '. $resultAlarmMessage . ', '. $row[0] .', '. $row[1] .', '.$row[2] . ', ' .$row[3]);
		
				$result =json_encode(array("message" => $data_message, "toME" => $data_ToME, "isRepair" => $data_isRepair, "whoSend" => $servicemanID, "latitude" => $resultCorrdinatesDatacenterIdLatitude, "longitude" => $resultCorrdinatesDatacenterIdLongitude), JSON_FORCE_OBJECT);
				//echo $error = json_last_error();
				echo $result;
		
		///////////////////////////////////////////////////////////////
				
			}else{
			
			//TO NIE DO MNIE POWIEDZ ¯E NIEMAM NIC DO ZROBIENIA
			$data_ToME = utf8_encode('No');
			$data_message = utf8_encode('Brak zg³oszeñ dla Ciebie');
			$data_isRepair = utf8_encode('No');
			$data_latitude = utf8_encode('0');
			$data_longitude = utf8_encode('0');
		
			$result =json_encode(array("message" => $data_message, "toME" => $data_ToME, "isRepair" => $data_isRepair, "whoSend" => $servicemanID, "latitude" => $data_latitude, "longitude" => $data_longitude), JSON_FORCE_OBJECT);
			echo $result;
			

			
			}
			
		}elseif($dataLastInfoAboutTicet[4] == "IN_PROGRESS"){
			//CZY DO MNIE BO MAMY ZADANIE W TRAKCIE REALIZACJI
			//echo $dataLastInfoAboutTicet[4];
			if($dataLastInfoAboutTicet[2] == $servicemanID){
			//DO MNIE WYŒWIETL W APK ¯E PRACUJÊ NAD NIM 
			//echo $dataLastInfoAboutTicet[3];
			//echo "tu jestem";
			$data_ToME = utf8_encode('Yes');
				$data_message = utf8_encode('Damage: ');
				$data_isRepair = utf8_encode('No');
				
				$corrdinatesDeviceId = "SELECT al.DeviceId from Alarms AS al WHERE al.TicketId = (SELECT up.TicketId from Updates AS up WHERE up.PersonId = '$servicemanID' ORDER BY up.PersonId DESC LIMIT 1)";
				$resultCorrdinatesDeviceId = Db::getInstance()->query($corrdinatesDeviceId)->fetch_assoc()['DeviceId'];
		
				$corrdinatesDatacenterIdLatitude = "SELECT da.Latitude from Datacenters AS da WHERE da.DatacenterId = (SELECT de.DatacenterId from Devices AS de WHERE de.DeviceId = '$resultCorrdinatesDeviceId')";
				$resultCorrdinatesDatacenterIdLatitude = Db::getInstance()->query($corrdinatesDatacenterIdLatitude)->fetch_assoc()['Latitude'];
		
				$corrdinatesDatacenterIdLongitude = "SELECT da.Longitude from Datacenters AS da WHERE da.DatacenterId = (SELECT de.DatacenterId from Devices AS de WHERE de.DeviceId = '$resultCorrdinatesDeviceId')";
				$resultCorrdinatesDatacenterIdLongitude = Db::getInstance()->query($corrdinatesDatacenterIdLongitude)->fetch_assoc()['Longitude'];
		
				$alarmMessage = "SELECT al.Name from Alarms AS al WHERE al.DeviceId = $resultCorrdinatesDeviceId";
				$resultAlarmMessage = Db::getInstance()->query($alarmMessage)->fetch_assoc()['Name'];
		
				$infoDatacenter = "SELECT da.Name, da.Address, da.PostalCode, da.City  from Datacenters AS da WHERE da.DatacenterId = (SELECT de.DatacenterId from Devices AS de WHERE de.DeviceId = '$resultCorrdinatesDeviceId')";
				$resultInfoDatacenter = Db::getInstance()->query($infoDatacenter);
		
				$row = $resultInfoDatacenter->fetch_array(MYSQLI_NUM);
				
				$data_message = utf8_encode($data_message . ' '. $resultAlarmMessage . ', '. $row[0] .', '. $row[1] .', '.$row[2] . ', ' .$row[3]);
		
				$result =json_encode(array("message" => $data_message, "toME" => $data_ToME, "isRepair" => $data_isRepair, "whoSend" => $servicemanID, "latitude" => $resultCorrdinatesDatacenterIdLatitude, "longitude" => $resultCorrdinatesDatacenterIdLongitude), JSON_FORCE_OBJECT);
				//echo $error = json_last_error();
				echo $result;
			}else{
			//POKAZ W APK ZE NIE MAM NIC DO ROBOTY 
				$data_ToME = utf8_encode('No');
				$data_message = utf8_encode('Brak zg³oszeñ dla Ciebie');
				$data_isRepair = utf8_encode('No');
				$data_latitude = utf8_encode('0');
				$data_longitude = utf8_encode('0');
				$result =json_encode(array("message" => $data_message, "toME" => $data_ToME, "isRepair" => $data_isRepair, "whoSend" => $servicemanID, "latitude" => $data_latitude, "longitude" => $data_longitude), JSON_FORCE_OBJECT);
				echo $result;
				
				//echo "POKAZ W APK ZE NIE MAM NIC DO ROBOTY!!!";
			
			}
		}else{
			if($dataLastInfoAboutTicet[2] == $servicemanID){
			
			////////////////////////////////////////////////////////
				
				$data_ToME = utf8_encode('Yes');
				$data_message = utf8_encode('Damage: ');
				$data_isRepair = utf8_encode('Yes');
				
				$corrdinatesDeviceId = "SELECT al.DeviceId from Alarms AS al WHERE al.TicketId = (SELECT up.TicketId from Updates AS up WHERE up.PersonId = '$servicemanID' ORDER BY up.PersonId DESC LIMIT 1)";
				$resultCorrdinatesDeviceId = Db::getInstance()->query($corrdinatesDeviceId)->fetch_assoc()['DeviceId'];
		
				$corrdinatesDatacenterIdLatitude = "SELECT da.Latitude from Datacenters AS da WHERE da.DatacenterId = (SELECT de.DatacenterId from Devices AS de WHERE de.DeviceId = '$resultCorrdinatesDeviceId')";
				$resultCorrdinatesDatacenterIdLatitude = Db::getInstance()->query($corrdinatesDatacenterIdLatitude)->fetch_assoc()['Latitude'];
		
				$corrdinatesDatacenterIdLongitude = "SELECT da.Longitude from Datacenters AS da WHERE da.DatacenterId = (SELECT de.DatacenterId from Devices AS de WHERE de.DeviceId = '$resultCorrdinatesDeviceId')";
				$resultCorrdinatesDatacenterIdLongitude = Db::getInstance()->query($corrdinatesDatacenterIdLongitude)->fetch_assoc()['Longitude'];
		
				$alarmMessage = "SELECT al.Name from Alarms AS al WHERE al.DeviceId = $resultCorrdinatesDeviceId";
				$resultAlarmMessage = Db::getInstance()->query($alarmMessage)->fetch_assoc()['Name'];
		
				$infoDatacenter = "SELECT da.Name, da.Address, da.PostalCode, da.City  from Datacenters AS da WHERE da.DatacenterId = (SELECT de.DatacenterId from Devices AS de WHERE de.DeviceId = '$resultCorrdinatesDeviceId')";
				$resultInfoDatacenter = Db::getInstance()->query($infoDatacenter);
		
				$row = $resultInfoDatacenter->fetch_array(MYSQLI_NUM);
				
				$data_message = utf8_encode($data_message . ' '. $resultAlarmMessage . ', '. $row[0] .', '. $row[1] .', '.$row[2] . ', ' .$row[3]);
		
				$result =json_encode(array("message" => $data_message, "toME" => $data_ToME, "isRepair" => $data_isRepair, "whoSend" => $servicemanID, "latitude" => $resultCorrdinatesDatacenterIdLatitude, "longitude" => $resultCorrdinatesDatacenterIdLongitude), JSON_FORCE_OBJECT);
				//echo $error = json_last_error();
				echo $result;
		
		///////////////////////////////////////////////////////////////
			
			}else{
			//ZADANIE ZREALIZOWANE ALE CZY NALEZA£O DO MNIE (NIEWA¯NE) POWIEDZ ¯E NIE MAM NIC DO ROBOTY 
				$data_ToME = utf8_encode('No');
				$data_message = utf8_encode('Brak zg³oszeñ dla Ciebie');
				$data_isRepair = utf8_encode('No');
				$data_latitude = utf8_encode('0');
				$data_longitude = utf8_encode('0');
		
				$result =json_encode(array("message" => $data_message, "toME" => $data_ToME, "isRepair" => $data_isRepair, "whoSend" => $servicemanID, "latitude" => $data_latitude, "longitude" => $data_longitude), JSON_FORCE_OBJECT);
				echo $result;
				//echo "ZADANIE ZREALIZOWANE ALE CZY NALEZA£O DO MNIE (NIEWA¯NE) POWIEDZ ¯E NIE MAM NIC DO ROBOTY ";
				}
		
		}
		
	}
		public function vincentyGreatCircleDistance($latitudeFrom, $longitudeFrom, $latitudeTo, $longitudeTo, $earthRadius = 6371000){
		// convert from degrees to radians
		$latFrom = deg2rad($latitudeFrom);
		$lonFrom = deg2rad($longitudeFrom);
		$latTo = deg2rad($latitudeTo);
		$lonTo = deg2rad($longitudeTo);

		$lonDelta = $lonTo - $lonFrom;
		$a = pow(cos($latTo) * sin($lonDelta), 2) +
		pow(cos($latFrom) * sin($latTo) - sin($latFrom) * cos($latTo) * cos($lonDelta), 2);
		$b = sin($latFrom) * sin($latTo) + cos($latFrom) * cos($latTo) * cos($lonDelta);

		$angle = atan2(sqrt($a), $b);
		return $angle * $earthRadius;
	
	}
}

$testObject2 = new TestNEW2();
$testObject2->run();
?>