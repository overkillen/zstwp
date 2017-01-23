<?php
require_once 'db.php';
header('Content-Type: application/json');
class Test3
	{
		public function run(){
		
		if(isset($_GET['ServicemanId'])){
		$service_man = $_GET['ServicemanId'];
		}
		$message = "";
		if(isset($_GET['NEWmessage'])){
		$message = $_GET['NEWmessage'];
		}
		$sql = $this->prepareSql($service_man);
		}
		private function prepareSql($servicemanID){
		$actualValue = "SELECT * from Updates WHERE PersonId = '$servicemanID' ORDER BY PersonId DESC LIMIT 1";
		$resultActualValue = Db::getInstance()->query($actualValue)->fetch_array(MYSQLI_NUM);
		
		$pkeyDataValue = "SELECT max(UpdateId) from Updates";
		$resultPkeyValue = Db::getInstance()->query($pkeyDataValue)->fetch_array(MYSQLI_NUM);
		
		$pkeyValue = $resultPkeyValue[0] + 1;
		echo $pkeyValue;
		$id = $resultActualValue[1];
		$personId = $resultActualValue[2];
		$msg = $message;
		
		echo $personId;
		echo $message;
		
		$sql = "INSERT INTO Updates Values (".$pkeyValue.",".$id.",".$personId.",now(),'".$msg."',4)";
		$resultWorkerId = Db::getInstance()->query($sql);
		
		echo "All right, go ahead!!!";
		}

	}    
$testObject3 = new Test3();
$testObject3->run();
?> 
