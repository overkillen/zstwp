<?php
class Config{
    private static $instance;
    private function __construct(){}
    private function __clone(){}
    private $dbConfiguration = [
        "host" => "db4free.net",
        "user" => "zstwp",
        "password" => "BazaDanychZSTWP",
        "db" => "zstwp_db"
    ];
    public static function getInstance(){
        if(!self::$instance){
            self::$instance = new self();
        }
        return self::$instance;
    }
    
    public function getDbConfiguration(){
        return $this->dbConfiguration;
    }
}