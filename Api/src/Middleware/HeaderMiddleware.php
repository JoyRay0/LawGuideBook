<?php

namespace App\Middleware;

use Psr\Http\Message\ServerRequestInterface;
use Psr\Http\Server\RequestHandlerInterface;
use Psr\Http\Message\ResponseInterface;
use Slim\Psr7\Response;

class HeaderMiddleware{

    public function __invoke(ServerRequestInterface $request, RequestHandlerInterface $handler) : ResponseInterface
    {
    
        $response = $handler->handle($request);

        $header = $response
        ->withHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
        ->withHeader("X-Content-Type-Options", "nosniff")
        ->withHeader("X-XSS-Protection", "1; mode=block")
        ->withHeader("X-Frame-Options", "DENY")
        ->withHeader("Referrer-Policy", "no-referrer")
        ->withHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        ->withHeader("Content-Type", "application/json; charset=utf-8")
        ->withHeader("Access-Control-Allow-Origin", "". $_ENV['APP_LINK'])
        ->withHeader("Cache-Control", "public, max-age=3600")
        ->withHeader("X-App-Name", "LawGuideBook");
        
        return $header;

    }

}