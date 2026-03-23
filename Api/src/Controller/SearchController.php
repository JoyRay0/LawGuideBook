<?php

namespace App\Controller;

use App\Database\DB;
use App\Helper\PaginationHelper;
use App\Helper\SanitizeHelper;
use Slim\Psr7\Request;
use Slim\Psr7\Response;

class SearchController{

    public function search(Request $request, Response $response, array $args){

        $searchInput = (!empty($request->getParsedBody())) ? $request->getParsedBody() : [];
        $page = (!isset($args['page'])) ? $args['page'] : 1;

        if(empty($searchInput["search"])){

            $response->getBody()->write(json_encode([

                "status" => "Error",
                "message" => "Search filed can not be empty"

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        $input = SanitizeHelper::inputString($searchInput['search']);

        //===================================
        //db search
        //===================================

        $pagination = PaginationHelper::getLimitOffset($page, 30);
        $offset = $pagination['offset'];
        $limit = $pagination['limit'];

        $data = DB::find(
        "SELECT question FROM search WHERE MATCH(question) AGAINST (? IN NATURAL LANGUAGE MODE) ORDER BY id ASC LIMIT $limit OFFSET $offset", 
        [$input]);

        if(!$data){

            $response->getBody()->write(json_encode([

                "status" => "Failed",
                "message" => "খুজে পাওয়া যায়নি"

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "page" => $page,
            "limit" => $limit,
            "data" => $data

        ]));

        return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

    }

}