<?php

namespace App\Controller;

use App\Database\DB;
use App\Helper\CacheHelper;
use App\Helper\PaginationHelper;
use Slim\Psr7\Request;
use Slim\Psr7\Response;

class CategoryController{

    public function all_category(Request $request, Response $response){

        $data = [

            [
                "id" => "1",
                "title" => "জমি আইন সহায়িকা",
                "image" => $_ENV['APP_LINK']."/server_image/category_image/land.png",
                "t_name" => $_ENV['T_LAND']
            ],
            [
                "id" => "2",
                "title" => "পুলিশ আইন সহায়িকা",
                "image" => $_ENV['APP_LINK']."/server_image/category_image/police.png",
                "t_name" => $_ENV['T_POLIC']
            ],
            [
                "id" => "3",
                "title" => "সড়ক পরিবহণ আইন সহায়িকা",
                "image" => $_ENV['APP_LINK']."/server_image/category_image/road.png",
                "t_name" => $_ENV['T_ROAD']
            ],
            [
                "id" => "4",
                "title" => "শ্রম আইন সহায়িকা",
                "image" => $_ENV['APP_LINK']."/server_image/category_image/labor.png",
                "t_name" => $_ENV['T_LABOR']
            ],
            [
                "id" => "5",
                "title" => "তথ্য ও যোগাযোগ প্রযুক্তি আইন সহায়িকা",
                "image" => $_ENV['APP_LINK']."/server_image/category_image/ict.png",
                "t_name" => $_ENV['T_ICT']
            ],
            [
                "id" => "6",
                "title" => "শিক্ষা আইন সহায়িকা",
                "image" => $_ENV['APP_LINK']."/server_image/category_image/education.png",
                "t_name" => $_ENV['T_EDUCATION']
            ],
            [
                "id" => "7",
                "title" => "কর আইন সহায়িকা",
                "image" => $_ENV['APP_LINK']."/server_image/category_image/tax.png",
                "t_name" => $_ENV['T_TAX']
            ],
            [
                "id" => "8",
                "title" => "বৈদেশিক কর্মষংস্থান ও অভিবাসী আইন সহায়িকা",
                "image" => $_ENV['APP_LINK']."/server_image/category_image/migration.png",
                "t_name" => $_ENV['T_MIGRATION']
            ],
            [
                "id" => "9",
                "title" => "ভোক্তা অধিকার আইন সহায়িকা",
                "image" => $_ENV['APP_LINK']."/server_image/category_image/customer.png",
                "t_name" => $_ENV['T_CUSTOMER']
            ],
            [
                "id" => "10",
                "title" => "বিবাহ আইন সহায়িকা",
                "image" => $_ENV['APP_LINK']."/server_image/category_image/marriage.png",
                "t_name" => $_ENV['T_MARRIAGE']
            ],

        ];

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "from" => "api",
            "item" => $data

        ]));

        return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

    }


    public function category( Request $request, Response $response, array $args){

        $table_names = [

            $_ENV['T_LAND'], $_ENV['T_POLIC'], $_ENV['T_ROAD'], $_ENV['T_LABOR'],
            $_ENV['T_ICT'], $_ENV['T_EDUCATION'], $_ENV['T_TAX'], $_ENV['T_MIGRATION'],
            $_ENV['T_CUSTOMER'], $_ENV['T_MARRIAGE']

        ];

        $page = (!isset($args['page'])) ? $args['page'] : 1;

        $data = (!empty($request->getParsedBody())) ? $request->getParsedBody() : [];

        $table_name = $data['t_name'] ?: "";    // database table name

        if(empty(trim($table_name))){

            $response->getBody()->write(json_encode([

                "status" => "Error",
                "message" => "This filed can not be empty"

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }


        //checking table name in tables array 
        
        if(!in_array(trim($table_name), $table_names, true)){

            $response->getBody()->write(json_encode([

                "status" => "Error",
                "message" => "Not match"

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        //=============================
        //checking cache
        //=============================

        $cacheData = CacheHelper::getArrayCache($table_name."_cache_".$page);

        if(!empty($cacheData)){

            //$cache_arry = json_decode($cacheData, true);

            $response->getBody()->write(json_encode([

                "status" => "Success",
                "from" => "cache",
                "data" => $cacheData

            ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        //=============================
        //db query
        //=============================

        $paginaton = PaginationHelper::getLimitOffset($page, 30);

        $limit = $paginaton['limit'];
        $offset = $paginaton['offset'];

        $data =  DB::query("SELECT id, question FROM $table_name LIMIT $limit OFFSET $offset");

        if(empty($data)){

            $response->getBody()->write(json_encode([

                "status" => "Failed",
                "message" => "Empty data"

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        CacheHelper::setArrayCache($table_name."_cache_".$page, $data, 120);      //saving data in cache

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "from" => "database",
            "page" => $page,
            "limit" => $limit,
            "data" => $data

        ]));

        return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

    }

}