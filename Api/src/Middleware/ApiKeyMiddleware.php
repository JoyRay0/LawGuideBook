<?php

namespace App\Middleware;

use Psr\Http\Message\RequestInterface;
use Psr\Http\Message\ServerRequestInterface;
use Psr\Http\Server\RequestHandlerInterface;
use Psr\Http\Message\ResponseInterface;
use Slim\Psr7\Response;

class ApiKeyMiddleware{

    public function __invoke(ServerRequestInterface $request, RequestHandlerInterface $handler):ResponseInterface{

        $api_key = $request->getHeaderLine("API-KEY") ?? "";

        $key = "eda621400e227458803483690f71af64f9b54f8fc9ab593cd92e94e623678539";

        $response = new Response();

        if(empty($api_key) || $api_key !== 64){

            $response->getBody()->write("<h2>Access Denied</h2>");

            return $response->withHeader("Content-Type", "text/html");

        }

        if($api_key !== $key){

            $response->getBody()->write("<h2>Not Match</h2>");

            return $response->withHeader("Content-Type", "text/html");

        }

        return $handler->handle($request);

    }

}