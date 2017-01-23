<?php
require_once 'db.php';
class DetailsByIdAction
{
    public function run(){
        if( !isset($_GET['TicketId']) ){
            die("Nie podano id ticketu (TicketId)!");
        }
        $ticketId = mysqli_real_escape_string( Db::getInstance()->getLink() ,$_GET['TicketId'] );
        
        $sql = $this->prepareSql( $ticketId );
        $result = Db::getInstance()->query( $sql );
        $result = $result->fetch_assoc();
        switch ( $result['type'] ){
            case 1:
                $result['type'] = 'Serwer';
                break;
            case 2:
                $result['type'] = 'Switch';
                break;
            case 3:
                $result['type'] = 'Router';
            break;    
        }
        echo json_encode( $result );
    }
    private function prepareSql( $id ){
        $sql = "SELECT t.number, d.name, d.type, d.ip, h.rackslot, h.shelf, u.message
            FROM Devices d, Hardware h, Tickets t, Updates u, Alarms a
            WHERE t.ticketid = ".$id." AND t.ticketid = u.ticketid AND t.ticketid = a.ticketid AND a.deviceid = d.deviceid AND d.hardwareid = h.hardwareid AND u.type = 3
            ORDER BY u.date desc";
        return $sql;
    }
}