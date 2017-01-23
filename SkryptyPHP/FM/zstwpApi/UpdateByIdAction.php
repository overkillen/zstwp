<?php
require_once 'db.php';
class UpdateByIdAction
{
    public function run(){
        if( !isset($_GET['TicketId']) ){
            die("Nie podano id ticketu (TicketId)!");
        }
        if( !isset($_GET['PersonId']) ){
            die("Nie podano id ticketu (PersonId)!");
        }
        if( !isset($_GET['message']) ){
            die("Nie podano komentarza (message)!");
        }
        $ticketId = mysqli_real_escape_string( Db::getInstance()->getLink() ,$_GET['TicketId'] );
        $message = mysqli_real_escape_string( Db::getInstance()->getLink() ,$_GET['message'] );
        $personId = mysqli_real_escape_string( Db::getInstance()->getLink() ,$_GET['PersonId'] );
        
        $sql = $this->prepareSql( $ticketId, $personId, $message );
        $result = Db::getInstance()->query( $sql );
        echo json_encode( [ "success" => true ] );
    }
    private function prepareSql( $id, $personId, $msg ){
        $pkeyQuesry = "SELECT max(UpdateId) as 'pkey' from Updates";
        $pkeyValue = Db::getInstance()->query( $pkeyQuesry )->fetch_assoc()['pkey'] + 1;
        
        $sql = "INSERT INTO Updates Values 
            (".$pkeyValue.",".$id.",".$personId.",now(),'".$msg."',4)";
        
        return $sql;
    }
}