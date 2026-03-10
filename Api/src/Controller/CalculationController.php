<?php

namespace App\Controller;

use Slim\Psr7\Request;
use Slim\Psr7\Response;

class CalculationController{

    public function limt_item(Request $request, Response $response){

        $data = [

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

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "from" => "api",
            "item" => $data

        ]));

        return $response->withHeader("Content-Type", "application/json");

    }

    public function all_item(Request $request, Response $response){

        $data = [

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

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "from" => "api",
            "item" => $data

        ]));

        return $response->withHeader("Content-Type", "application/json");

    }

}