<?php

namespace App\Controller;

use App\Database\DB;
use App\Helper\SanitizeHelper;
use Slim\Psr7\Request;
use Slim\Psr7\Response;

class SearchController{

    public function search(Request $request, Response $response){

        $searchInput = $request->getParsedBody() ?: [];

        if(empty($searchInput["search"])){

            $response->getBody()->write(json_encode([

                "status" => "Error",
                "message" => "Search filed can not be empty"

            ]));

            return $response->withHeader("Content-Type", "application/json");

        }

        $input = SanitizeHelper::inputString($searchInput['search']);

        $data = DB::find(
        "SELECT * FROM search WHERE MATCH(question) AGAINST (? IN NATURAL LANGUAGE MODE LIMIT 30)", 
        [$input]);

        if(!$data){

            $response->getBody()->write(json_encode([

                "status" => "Failed",
                "message" => "খুজে পাওয়া যায়নি"

            ]));

            return $response->withHeader("Content-Type", "application/json");

        }

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "data" => $data

        ]));

        return $response->withHeader("Content-Type", "application/json");

    }

}