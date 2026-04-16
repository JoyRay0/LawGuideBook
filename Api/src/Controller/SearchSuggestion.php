<?php

namespace App\Controller;

use App\Database\DB;
use App\Helper\SanitizeHelper;
use Slim\Psr7\Request;
use Slim\Psr7\Response;

class SearchSuggestion{

    public function suggestion(Request $request, Response $response){

        $searchData = (!empty($request->getParsedBody())) ? $request->getParsedBody() : "";

        if(empty($searchData['search'])){

            $response->getBody()->write(json_encode([

                "status" => "Error",
                "message" => "Empty filed"

            ]));

            return $response->withHeader("Content-Type", "application/json");

        }

        $s_search_data = SanitizeHelper::inputString($searchData['search']);

        $data = DB::query(
            "SELECT question FROM search WHERE question LIKE ? ORDER BY id ASC LIMIT 10",
            ["%".$s_search_data."%"]
        );

        if(empty($data)){

            $response->getBody()->write(json_encode([

                "status" => "Failed",
                "message" => "Not found"

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
