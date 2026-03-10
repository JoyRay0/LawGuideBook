<?php

namespace App\Controller;

use Slim\Psr7\Request;
use Slim\Psr7\Response;

class CategoryController{


    public function all_category(Request $request, Response $response){

        $data = [

            [
                "id" => "1",
                "title" => "জমি সহায়িকা",
                "image" => $_ENV['API_LINK']."/category_image/land.png",
                "t_name" => ""
            ],
            [
                "id" => "2",
                "title" => "পুলিশ সহায়িকা",
                "image" => $_ENV['API_LINK']."/category_image/police.png",
                "t_name" => ""
            ],
            [
                "id" => "3",
                "title" => "সড়ক পরিবহণ সহায়িকা",
                "image" => $_ENV['API_LINK']."/category_image/road.png",
                "t_name" => ""
            ],
            [
                "id" => "4",
                "title" => "শ্রম সহায়িকা",
                "image" => $_ENV['API_LINK']."/category_image/labor.png",
                "t_name" => ""
            ],
            [
                "id" => "5",
                "title" => "তথ্য ও যোগাযোগ প্রযুক্তি সহায়িকা",
                "image" => $_ENV['API_LINK']."/category_image/ict.png",
                "t_name" => ""
            ],
            [
                "id" => "6",
                "title" => "শিক্ষা সহায়িকা",
                "image" => $_ENV['API_LINK']."/category_image/education.png",
                "t_name" => ""
            ],

        ];

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "from" => "api",
            "item" => $data

        ]));

        return $response->withHeader("Content-Type", "application/json");

    }


    public function category( Request $request, Response $response){

        $table_names = [

            "", ""

        ];

        $data = $request->getParsedBody();

        $table_name = $data['t_name'] ?? "";    // database table name

        if(empty(trim($table_name))){

            $response->getBody()->write(json_encode([

                "status" => "Failed",
                "message" => "This filed can not be empty"

            ]));

            return $response->withHeader("Content-Type", "application/json");

        }


        //checking table name in tables array 
        
        if(!in_array(trim($table_name), $table_names, true)){

            $response->getBody()->write(json_encode([

                "status" => "Failed",
                "message" => "Not Found"

            ]));

            return $response->withHeader("Content-Type", "application/json");

        }


        return $response->withHeader("Content-Type", "application/json");

    }

}