<?php

namespace App\Controller;

use App\Database\DB;
use App\Helper\SanitizeHelper;
use Slim\Psr7\Request;
use Slim\Psr7\Response;

class ChatbotController{

    public function chatbot(Request $request, Response $response){

        $message = (!empty($request->getParsedBody())) ? $request->getParsedBody() : ""; 

        if(empty($message['user_message'])){

            $response->getBody()->write(json_encode([

                "status" => "Error",
                "message" => "message can not be empty"

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        $s_user_message = SanitizeHelper::inputString($message['user_message']);

        $result =  self::check_user_message($s_user_message, $response);

        if($result !== null){

            $response->getBody()->write(json_encode([

                "status" => "Success",
                "user_message" => $s_user_message,
                "ai_message" => $result

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        //======================================
        //searching in ser=arch table
        //=======================================
        $answer = DB::find(
            "SELECT question, answer FROM search WHERE MATCH(question) AGAINST (? IN NATURAL LANGUAGE MODE) LIMIT 3", 
            [$s_user_message]);


        $rules = "### ভূমিকা (System Role):
        তুমি একজন বিশেষজ্ঞ আইনি সহকারী (AI Legal Consultant)। তোমার মূল দায়িত্ব হলো শুধুমাত্র বাংলাদেশের আইনের ওপর ভিত্তি করে ব্যবহারকারীর প্রশ্নের উত্তর দেওয়া।

        ### তোমার জন্য নিয়মাবলী:
        ১. নিচে দেওয়া {তথ্যসূত্র} থেকে উত্তর দেওয়ার চেষ্টা করো।
        ২. যদি {তথ্যসূত্র} অংশে উত্তর না থাকে, তবে তোমার সাধারণ আইনি জ্ঞান ব্যবহার করো।
        ৩. যদি ব্যবহারকারী আইন বহির্ভূত কোনো প্রশ্ন করে, তবে কোনো উত্তর দিবে না। শুধু বলবে— দুঃখিত, আমি শুধুমাত্র আইনি বিষয়ে আপনাকে সাহায্য করতে পারবো।
        ৪. তোমার ভাষা হবে অত্যন্ত মার্জিত এবং পেশাদার।
        ৫. ইউজার তোমাকে যদি ইংরেজি বা বাংলা ভাষায় প্রশ্ন করে তাহলে তুমি সেই ভাষায় তাকে উত্তর দিবে।
        ৬. তুমি কোন ছবি বা ভিডিও ব্যবহার করবে না।
        ৭. তুমি ব্যবহারকারীকে তুমি, তোমাকে বলে সম্মোধন করবে।
        ";

        $context = "### তথ্যসূত্র (Reference Data):".$answer."### ব্যবহারকারীর প্রশ্ন (User Question): ". $s_user_message;

        //=============================
        //sending user message to ai
        //=============================

        $gemini_ai = self::geminiBot($rules, $context);

        //$deepeek_ai = self::deepseek($rules, "### তথ্যসূত্র (Reference Data):".$answer."### ব্যবহারকারীর প্রশ্ন (User Question): ". $s_user_message);

        $ai_message = json_decode($gemini_ai, true);

        if(!$ai_message || isset($ai_message['error'])){

            //$deepeek_ai = self::deepseek($rules, "### তথ্যসূত্র (Reference Data):".$answer."### ব্যবহারকারীর প্রশ্ন (User Question): ". $s_user_message);

            $response->getBody()->write(json_encode([

                "status" => "Failed",
                "message" => "Token finished"

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        //===========================
        //save ai response
        //===========================

        $isInserted = DB::insertOne(
            "INSERT INTO ai_chat (user_message, ai_message) VALUES (?, ?)",
            [$s_user_message, $ai_message]
        );

        if(!$isInserted){

            $response->getBody()->write(json_encode([

                "status" => "Error",
                "message" => "Ai chat not inserted"

            ]));

            return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

        }

        $response->getBody()->write(json_encode([

            "status" => "Success",
            "user_message" => $s_user_message,
            "ai_message" => $gemini_ai

        ]));

        return $response ->withHeader("Content-Type", "application/json; charset=utf-8");

    }

    private function send_data_to_api( string $url, array $header, string $json){

        $ch = curl_init($url);

        curl_setopt_array($ch, [

            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_POST => true,
            CURLOPT_HTTPHEADER => $header,
            CURLOPT_POSTFIELDS => $json,
            CURLOPT_CONNECTTIMEOUT => 20,
            CURLOPT_TIMEOUT => 30

        ]);

        $result = curl_exec($ch);

        if(curl_error($ch)){

            echo curl_errno($ch);

        }

        curl_close($ch);

        return $result;

    }

    private function check_user_message(string $message, $response) : ?string{

        $hash = md5(self::normalize($message));

        //=================================
        //Hash check
        //=================================

        $hash_response = DB::findOne(
            "SELECT ai_message FROM ai_chat WHERE hash = ? LIMIT 1", 
            [$hash]);

        if($hash_response){

           return $hash_response['ai_message'];

        }

        //======================================
        //full text message check
        //======================================

        $full_text_response = DB::findOne(
            "SELECT ai_message, MATCH(user_message) AGAINST(? IN NATURAL LANGUAGE MODE) AS score
                FROM ai_chat
                WHERE MATCH(user_message) AGAINST(? IN NATURAL LANGUAGE MODE)
                ORDER BY score DESC
                LIMIT 10", 
            [$message, $message]);


        if($full_text_response && $full_text_response['score'] >= 0.90){


            return $full_text_response['ai_message'];

        }

        //===================================
        //Like message check
        //===================================

        $like_response = DB::findOne(
            "",
            []
        );

        return null;

    }

    private function geminiBot(string $rules, string $message){

        $json = json_encode([

            "contents" => [
                [
                    "parts" => [
                        [
                            "text" => $rules . $message
                        ]

                    ]
                ]
            ]

        ]);

        $data = self::send_data_to_api(
            $_ENV['GEMINI_API_URL'],
            [
                "Content-Type: application/json",
                "x-goog-api-key: ".$_ENV['GEMINI_API_KEY']
            ],
            $json,
        );

        return $data;

    }

    private function deepseek(string $rules, string $message){

        $json = json_encode([

            "model" => "deepseek-reasoner",
            "messages" => [

                [

                    "role" => "system",
                    "content" => $rules

                ],
                [

                    "role" => "user",
                    "content" => $message

                ],


            ]

        ]);


        $data = self::send_data_to_api(
            $_ENV['DEEPSEEK_API_URL'],
            [
                "Content-Type: application/json",
                "Authorization: Bearer ".$_ENV['DEEPSEEK_API_KEY'] 
            ],
            $json
        );

        return $data;

    }

    private function normalize(string $text){

        $text = strtolower($text);
        $text = preg_replace('/[^\p{L}\p{N}\s]/u', '', $text); //symbol remove
        $text = preg_replace('/\s+/', '', $text);  //multi space remopve


        return trim($text);

    }

}