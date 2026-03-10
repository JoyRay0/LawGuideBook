<?php

namespace App\Middleware;

use Psr\Http\Message\ServerRequestInterface;
use Psr\Http\Server\RequestHandlerInterface;
use Psr\Http\Message\ResponseInterface;
use Slim\Psr7\Response;

class DeviceIDMiddleware{

    public function __invoke(ServerRequestInterface $request, RequestHandlerInterface $handler) : ResponseInterface{
    
        $device_id = $request->getHeaderLine("Device-ID");

        $response = new Response();

        if(empty($device_id) || strlen($device_id) !== 16){

            $response->getBody()->write("<h2>Access Denied</h2>");

            return $response->withHeader("Content-Type", "text/html");

        }

        if(!preg_match("/^(?=.*[a-z])(?=.*[0-9])[a-z0-9]{16}$/i", $device_id)){

            $response->getBody()->write("<h3>Invalid ID</h3>");

            return $response->withHeader("Content-Type", "text/html");

        }

        return $handler->handle($request);

    }


}