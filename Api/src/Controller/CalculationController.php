<?php

namespace App\Controller;

use Slim\Psr7\Request;
use Slim\Psr7\Response;

class CalculationController{

    public function calculation_limt_item(Request $request, Response $response){

        $data = [

            [
                "title" => "উওরাধিকার",
                "image" => $_ENV['APP_LINK']."/cdn/family.png"
            ],
            [
                "title" => "রেজিস্ট্রেশন ফি",
                "image" => $_ENV['APP_LINK']."/cdn/regi.png"
            ],
            [
                "title" => "দেনমোহর",
                "image" => $_ENV['APP_LINK']."/cdn/denmohor.png"
            ],

        ];

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "from" => "api",
            "item" => $data

        ]));

        return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

    }

    public function calculation_all_item(Request $request, Response $response){

        $data = [

            [
                "title" => "উওরাধিকার",
                "image" => $_ENV['APP_LINK']."/cdn/family.png"
            ],
            [
                "title" => "রেজিস্ট্রেশন ফি",
                "image" => $_ENV['APP_LINK']."/cdn/regi.png"
            ],
            [
                "title" => "দেনমোহর",
                "image" => $_ENV['APP_LINK']."/cdn/denmohor.png"
            ],

        ];

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "from" => "api",
            "item" => $data

        ]));

        return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

    }

}