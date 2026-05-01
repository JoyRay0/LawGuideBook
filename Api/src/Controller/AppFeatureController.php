<?php

namespace App\Controller;

use Slim\Psr7\Request;
use Slim\Psr7\Response;
use App\Helper\ErrorHelper;

class AppFeatureController{

    public function app_update(Request $request, Response $response){

        ErrorHelper::register();

        $new_version = "";

        if(empty($new_version)){

            $response->getBody()->write(json_encode([

                "status" => "Failed",
                "message" => "No new version found"

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "version" => $new_version

        ]));

        return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

    }

}