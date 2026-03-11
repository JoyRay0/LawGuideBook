<?php

namespace App\Middleware;

use Psr\Http\Message\ServerRequestInterface;
use Psr\Http\Server\RequestHandlerInterface;
use Psr\Http\Message\ResponseInterface;
use Slim\Psr7\Response;
use App\Helper\CacheHelper;

class RateLimitMiddleware{

    public function __invoke(ServerRequestInterface $request, RequestHandlerInterface $handler) : ResponseInterface{
       
        $device_id = $request->getHeaderLine("Device-ID") ?: "";

        $response = new Response();

        $currentTime = time();

        $cacheData = CacheHelper::getArrayCache($device_id);

        if(empty($cacheData)){

            CacheHelper::setArrayCache($device_id, [
                "count" => 1, 
                "time" => $currentTime
            ], 60); //60 second cache

        }else{

            $count = (int)$cacheData["count"] ?? 0;
            $firstTime = (int)$cacheData["time"] ?? 0;

            if($currentTime - $firstTime <= 60){

                if($count >= 20){

                    $response->getBody()->write(json_encode([

                        "status" => "Failed",
                        "message" => "Please try again later"

                    ]));

                    return $response->withHeader("Content-Type", "application/json");

                }

                $count++;
                CacheHelper::setArrayCache($device_id, [

                    "count" => $count,
                    "time" => $firstTime

                ], 60);

            }else{

                //reset cache

                CacheHelper::setArrayCache($device_id, [
                    "count" => 1, 
                    "time" => $currentTime
                ], 60);


            }

        }

        return $handler->handle($request);
    }

}