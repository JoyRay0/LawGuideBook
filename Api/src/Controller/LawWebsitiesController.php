<?php

namespace App\Controller;

use Slim\Psr7\Request;
use Slim\Psr7\Response;

class LawWebsitiesController{

    public function websites(Request $request, Response $response){

        $data = [

            [
                "id" => "1",
                "title" => "বাংলাদেশ সরকারি আইনি ওয়েবসাইট",
                "website_link" => "http://bdlaws.minlaw.gov.bd/laws-of-bangladesh.html"
            ],
            [
                "id" => "2",
                "title" => "সাঈদ আইনি ওয়েবসাইট",
                "website_link" => "https://laws.sayed.app/laws"
            ],
            [
                "id" => "3",
                "title" => "বাংলাদেশ সুপ্রিম কোর্ট",
                "website_link" => "https://www.supremecourt.gov.bd/web/index.php"
            ],
            [
                "id" => "4",
                "title" => "বারকাউন্সিল বাংলাদেশ",
                "website_link" => "https://www.barcouncil.gov.bd"
            ],
            

        ];

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "from" => "api",
            "data" => $data

        ]));

        return $response;

    }

}