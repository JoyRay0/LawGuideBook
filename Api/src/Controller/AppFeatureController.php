<?php

namespace App\Controller;

use Slim\Psr7\Request;
use Slim\Psr7\Response;

class AppFeatureController{

    public function app_update(Request $request, Response $response){

        $new_version = "";

        if(empty($new_version)){

            $response->getBody()->write(json_encode([

                "status" => "Failed",
                "message" => "No new version found"

            ]));

            return $response;

        }

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "version" => $new_version

        ]));

        return $response;

    }

}