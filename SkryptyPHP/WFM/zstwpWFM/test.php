<?php
//header('Content-Type: application/json');
require_once 'db.php';

class Test
{
	public function run(){
		if(isset($_GET[temporaryServicemanLatitude])){
		$data_temporaryServicemanLatitude = mysqli_real_escape_string( Db::getInstance()->getLink() ,$_GET[temporaryServicemanLatitude]);
		}
		if(isset($_GET[temporaryServicemanLongitude])){
		$data_temporaryServicemanLongitude =  mysqli_real_escape_string( Db::getInstance()->getLink() , $_GET[temporaryServicemanLongitude]);
		}
		if(isset($_GET[servicemanID])){
		$data_servicemanID =  mysqli_real_escape_string( Db::getInstance()->getLink() ,$_GET[servicemanID]);
		}
		if(isset($_GET[nowWorking])){
		$data_nowWorking =  mysqli_real_escape_string( Db::getInstance()->getLink() ,$_GET[nowWorking]);
		}

		$sql = $this->prepareSql( $data_temporaryServicemanLatitude, $data_temporaryServicemanLongitude, $data_servicemanID, $data_nowWorking);
		$result = Db::getInstance()->query( $sql );

		}
	private function prepareSql( $Latitude, $Longitude, $servicemanID, $data_nowWorking){
		$ifexistServiemanQuery = "SELECT WorkerId from Workers WHERE WorkerId = '$servicemanID'";
		$resultWorkerId = Db::getInstance()->query($ifexistServiemanQuery)->fetch_assoc()['WorkerId'];
		if($resultWorkerId == null) {
			 if($_GET[nowWorking] == yes){
			 $workerIsReadyForTask = 0;
			 echo $workerIsReadyForTask;
			 }else{
			 $workerIsReadyForTask = 1;
			 echo $workerIsReadyForTask;
			 }
			$sql = "INSERT INTO Workers Values (".$servicemanID.",".$workerIsReadyForTask.",'Franek','Kowalski','4','728728728','franek@gmail.com',".$Latitude.",".$Longitude.",now())";
			return $sql;
		}else{
			if($_GET[nowWorking] == yes){
				$workerIsReadyForTask = 0;
				echo $workerIsReadyForTask;
				}else{
				$workerIsReadyForTask = 1;
				echo $workerIsReadyForTask;
				}
			$sql = "UPDATE Workers SET Latitude='$Latitude', Longitude='$Longitude', IsActive='$workerIsReadyForTask' WHERE WorkerId ='$servicemanID'";
			return $sql;
		
		}
		
		}
	
}
$testObject = new Test();
$testObject->run();
?> 