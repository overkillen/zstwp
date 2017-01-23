<?php
require_once 'config.php';
class Db{
    private static $instance;
    private $link;
    private function __construct(){
        $dbConfig = Config::getInstance()->getDbConfiguration();
        
        $this->link = new mysqli($dbConfig["host"], $dbConfig["user"], $dbConfig["password"],$dbConfig["db"]);
        if ($this->link->connect_error) {
            die("Connection failed: " . $conn->connect_error);
        }
        
    }
    private function __clone(){}
    
    public static function getInstance(){
        if(!self::$instance){
            self::$instance = new self();
        }
        return self::$instance;
    }
    public function getLink(){
        return $this->link;
    }
    public function query( $sql ){
        $result = $this->link->query( $sql );        
        if( !$result ){
            die( mysqli_error( $this->link ) );
        }
        return $result;
    }
}