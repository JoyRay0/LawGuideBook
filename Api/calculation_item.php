<?php

require_once __DIR__. '/Header.php';
require_once __DIR__. '/JsonMessage.php';

$jsonMessage = new JsonMessage();
$header = new HeadersManager();

$method = $_SERVER['REQUEST_METHOD'];
$res = $_GET['res'] ?? "";


$header->setAllHeaders();

if($method !== 'GET'){

    $jsonMessage->dieMessage("Error", "Method not allowed");

}


switch($res){

    case'limit_item':

        $responce = [

            [
                "title" => "উওরাধিকার",
                "image" => "https://lawguidebook.rksoftwares.fun/cdn/family.png"
            ],
            [
                "title" => "রেজিস্ট্রেশন ফি",
                "image" => "https://lawguidebook.rksoftwares.fun/cdn/regi.png"
            ],
            [
                "title" => "দেনমোহর",
                "image" => "https://lawguidebook.rksoftwares.fun/cdn/denmohor.png"
            ],

        ];

    break;

    case 'all_item':

        $responce = [

            [
                "title" => "উওরাধিকার",
                "image" => "https://lawguidebook.rksoftwares.fun/cdn/family.png"
            ],
            [
                "title" => "রেজিস্ট্রেশন ফি",
                "image" => "https://lawguidebook.rksoftwares.fun/cdn/regi.png"
            ],
            [
                "title" => "দেনমোহর",
                "image" => "https://lawguidebook.rksoftwares.fun/cdn/denmohor.png"
            ],

        ];

    break;

    default :

        $jsonMessage->errorMessage("Failed", "Wrong res method");

}//switch

$jsonMessage->successMessage("Success", "api", "items", $responce);


