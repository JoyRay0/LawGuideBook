<?php

namespace App\Controller;

use App\Database\DB;
use App\Helper\CacheHelper;
use Slim\Psr7\Request;
use Slim\Psr7\Response;

class GovtWebsitesController{

    public function govt_website(Request $request, Response $response){


        //checking cache data
        $cache = CacheHelper::getArrayCache("gov_cache");

        if(!empty($cache)){

            $response->getBody()->write(json_encode([

                "status" => "Success",
                "from" => "cache",
                "data" => $cache

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        // db query
        $data = DB::query("SELECT * FROM govt");

        if(empty($data)){

            $response->getBody()->write(json_encode([

                "status" => "Failed",
                "message" => "Data not found"

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        //save data in cache
        CacheHelper::setArrayCache("gov_cache", $data, 120);

        
        $response->getBody()->write(json_encode([

            "status" => "Success",
            "from" => "db",
            "data" => $data

        ]));

        return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

    }

}