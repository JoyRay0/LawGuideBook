<?php

namespace App\Controller;

use Slim\Psr7\Request;
use Slim\Psr7\Response;

class AdController{

    public function ads(Request $request, Response $response){

        $enable = false;

        $response->getBody()->write(json_encode([

            "enable" => $enable

        ]));

        return $response->withHeader("Content-Type", "application/json");

    }

}