<?php
require_once 'db.php';
class OtherAlertsListAction
{
    public function run(){
        if( !isset($_GET['TicketNumber']) ){
            die("Nie podano numeru ticketu (TicketNumber)!");
        }
        $ticketNumber = mysqli_real_escape_string( Db::getInstance()->getLink() ,$_GET['TicketNumber'] );
        
        $sql = $this->prepareSql( $ticketNumber );
        $result = Db::getInstance()->query( $sql );
        if( $result->num_rows > 0 ){
            echo json_encode( [ "success" => false ] );
            exit();
        }
        echo json_encode( [ "success" => true ] );
    }
    private function prepareSql( $number ){
        $date = new DateTime();
        $date->modify('-1 hour');
        $dateString = $date->format('Y-m-d H:i:s');
        $sql = "select a.AlarmID 
            from Alarms a, Datacenters data, Devices d 
            where d.DatacenterID=(
                select data.DatacenterID 
                from Alarms a, Tickets t, Datacenters data, Devices d 
                where t.Number='".$number."' and t.TicketID=a.TicketID and a.DeviceID=d.DeviceID and d.DatacenterID=data.DatacenterID
            ) and data.DatacenterID=d.DatacenterID and d.DeviceID=a.DeviceID and a.StartDate>'".$dateString."';";
        
        return $sql;
    }
}