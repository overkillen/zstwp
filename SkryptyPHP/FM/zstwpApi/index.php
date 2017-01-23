<?php
error_reporting(E_ALL);
require_once 'DetailsByIdAction.php';
require_once 'UpdateByIdAction.php';
require_once 'OtherAlertsListAction.php';

class App{
    private function __construct(){}
    private function __clone(){}
    private static $instance;
    private $url;
    private $action;
    private $apiActions = [
        "otherAlerts" => "OtherAlertsListAction",
        "detailsById" => "DetailsByIdAction",
        "updateById" => "UpdateByIdAction"
    ];
    public static function getInstance(){
        if(!self::$instance){
            self::$instance = new self();
        }
        return self::$instance;
    }
    
    public function run(){
        $this->url = urldecode($_SERVER['REQUEST_URI']);
        $this->action = explode("?",explode("/", $this->url)[2])[0];
        if( !isset( $this->apiActions[ $this->action ] ) ){
            die("Nie rozpoznano akcji!");
        }
        $action = new $this->apiActions[ $this->action ];
        $action->run();
        exit();
    }
}

App::getInstance()->run();