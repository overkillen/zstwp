<?php
require_once 'db.php';
header('Content-Type: application/json');
class Test2
{    
	public function run(){
		
		if(isset($_GET['ServicemanId'])){
		$service_man = $_GET['ServicemanId'];
		}
		
		$sql = $this->prepareSql($service_man);
	}
	private function prepareSql($servicemanID){
		$ifexistServiemanQuery = "SELECT PersonId from Updates WHERE PersonId = '$servicemanID'";
		$resultWorkerId = Db::getInstance()->query($ifexistServiemanQuery)->fetch_assoc()['PersonId'];
		
		if($resultWorkerId == null) {
		
		$data_ToME = 'No';
		$data_message = 'Brak zgłoszeń dla Ciebie';
		//$data_isRepair = 'Yes';
		$data_isRepair = 'No';
		$data_latitude = '0';
		$data_longitude = '0';
		
		$result =json_encode(array("message" => $data_message, "toME" => $data_ToME, "isRepair" => $data_isRepair, "whoSend" => $servicemanID, "latitude" => $data_latitude, "longitude" => $data_longitude), JSON_FORCE_OBJECT);
		echo $result;
		return "brak zgloszenia";
		
		}else{
		
		$data_ToME = 'Yes';
		$data_message = 'Damage: ';
		//$data_isRepair = 'Yes';
		$data_isRepair = 'No';
		
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
		
		//$dataIsRepair = "SELECT Message from Updates WHERE PersonId = '$servicemanID' ORDER BY PersonId DESC LIMIT 1";
		$dataIsRepair = "SELECT Message from Updates ORDER BY UpdateId DESC LIMIT 1";
		//$dataIsRepair = "SELECT up.max(UpdateId) , up.Message from Updates AS up WHERE up.PersonId = '$servicemanID'";
		//$resultDataIsRepair = Db::getInstance()->query($dataIsRepair)->fetch_array(MYSQLI_NUM);
		$resultDataIsRepair = Db::getInstance()->query($dataIsRepair)->fetch_assoc()['Message'];
		
		//echo $resultDataIsRepair;
		
		if($resultDataIsRepair == null) {
			$data_isRepair = 'No';
		}else{
			$data_isRepair = 'Yes';
		};
		
		
		
		
	
		$data_message = $data_message . ' '. $resultAlarmMessage . ', '. $row[0] .', '. $row[1] .', '.$row[2] . ', ' .$row[3];
		
		$result =json_encode(array("message" => $data_message, "toME" => $data_ToME, "isRepair" => $data_isRepair, "whoSend" => $servicemanID, "latitude" => $resultCorrdinatesDatacenterIdLatitude, "longitude" => $resultCorrdinatesDatacenterIdLongitude), JSON_FORCE_OBJECT);
		echo $result;
		return "mamy zgloszenie";
		}
	}
}

$testObject2 = new Test2();
$testObject2->run();
?>