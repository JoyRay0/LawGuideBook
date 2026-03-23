<?php

namespace App\Controller;

use App\Database\DB;
use App\Helper\SanitizeHelper;
use Slim\Psr7\Request;
use Slim\Psr7\Response;

class AnswerController{

    public function answer(Request $request, Response $response){

        $questionData = $request->getParsedBody() ?: "";

        if(empty($questionData['question'])){

            $response->getBody()->write(json_encode([

                "status" => "Error",
                "message" => "question not found"

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        $s_question = SanitizeHelper::inputString($questionData['question']);

        //==================================
        //db one search
        //==================================

        $data = DB::findOne(
            "SELECT answer FROM search WHERE MATCH(question) AGAINST (? IN NATURAL LANGUAGE MODE)",
            [$s_question]);

        if(!$data){

            $response->getBody()->write(json_encode([

                "status" => "Failed",
                "message" => "খুজে পাওয়া যায়নি"

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }


        $response->getBody()->write(json_encode([

            "status" => "Success",
            "data" => $data

        ]));

        return $response ->withHeader("Content-Type", "application/json; charset=utf-8");


    }

}