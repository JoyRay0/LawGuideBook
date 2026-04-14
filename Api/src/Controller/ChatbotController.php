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

    private function check_user_message(string $message) : ?string{

        $map_message = self::check_text($message);      //word maping in user message

        $hash = md5(self::normalize($map_message));

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
            [$map_message, $map_message]);


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

    private function check_text(string $text) : string{

        $words = [

            "hi" => "hi", "hello" => "hi", "হ্যালো" => "hi", "হাই" => "hi",

            "land" => "জমি", "জায়গা" => "জমি", "ভূমি" => "জমি", "ভুমি" => "জমি", "জমি" => "জমি",
            "খেত" => "জমি", "jomi" => "জমি", "jayga" => "জমি",

            "purchase" => "ক্রয়", "কেনা" => "ক্রয়", "ক্রয়" => "ক্রয়", "কেনা" => "ক্রয়", "kory" => "ক্রয়",
            "kinte" => "ক্রয়", "kinta" => "ক্রয়",

            "sell" => "বিক্রয়", "sold" => "বিক্রয়", "বিক্রি" => "বিক্রয়", "বেচা" => "বিক্রয়",
            "বিক্রয়" => "বিক্রয়", "bikroy" => "বিক্রয়", "bikri" => "বিক্রয়",

            "i" => "আমি", "আমি" => "আমি", "ami" => "আমি", "ame" => "আমি",

            "my" => "আমার", "amer" => "আমার", "amar" => "আমার", "amr" => "আমার", "আমার" => "আমার",

            "me" => "আমাকে", "amk" => "আমাকে", "amak" => "আমাকে", "amke" => "আমাকে", "আমাকে" => "আমাকে",
            "to me" => "আমাকে", "আমায়" => "আমাকে",

            "good" => "ভালো", "excellent" => "ভালো", "balo" => "ভালো", "valo" => "ভালো", "ভালো" => "ভালো",
            "bhalo" => "ভালো", "vhalo" => "ভালো",

            "have" => "আছে", "has" => "আছে", "theres" => "আছে", "there is" => "আছে", "আছে" => "আছে",
            "ace" => "আছে", "ache" => "আছে", "acha" => "আছে",

            "paper" => "কাগজ", "কাগজ" => "কাগজ", "kagoj" => "কাগজ", "kagog" => "কাগজ",

            "document" => "দলিল", "documenting" => "দলিল", "record" => "দলিল", "deed" => "দলিল", "দলিল" => "দলিল",
            "dolil" => "দলিল", "dalil" => "দলিল", "dalill" => "দলিল",

            "law" => "আইন", "act" => "আইন", "laws" => "আইন", "regulation" => "আইন", 
            "regulations" => "আইন", "আইন" => "আইন", "ain" => "আইন", "aien" => "আইন", "আইনে" => "আইন",

            "rule" => "নিয়ম", "rules" => "নিয়ম", "নিয়ম" => "নিয়ম", "niym" => "নিয়ম", "niom" => "নিয়ম",
            "niam" => "নিয়ম", "neam" => "নিয়ম",

            "clause" => "ধারা", "genre" => "ধারা", "ধারা" => "ধারা", "dhara" => "ধারা", "dara" => "ধারা",
            "দারা" => "ধারা",

            "article" => "দফা", "dofa" => "দফা", "দফা" => "দফা", "dafa" => "দফা",

            "right" => "ঠিক", "correct" => "ঠিক", "exactly" => "ঠিক", "sure" => "ঠিক", "ঠিক" => "ঠিক",
            "thik" => "ঠিক", "tik" => "ঠিক", "thick" => "ঠিক", "tick" => "ঠিক", 
            "are" => "হয়", "হয়" => "হয়", "hoy" => "হয়", "hoye" => "হয়", "hay" => "হয়",

            "no" => "না", "না" => "না", "none" => "না", "na" => "না", "noo" => "না", "nope" => "না",
            "never" => "না", "nay" => "না", "নাহ" => "না",

            "ok" => "ঠিক আছে", "ঠিক আছে" => "ঠিক আছে", "thik ache" => "ঠিক আছে", "thik ace" => "ঠিক আছে",
            "tik ache" => "ঠিক আছে", "tik ace" => "ঠিক আছে",
            
            "what" => "কি", "কি" => "কি", "ki" => "কি", "ke" => "কি", "কী" => "কি",

            "where" => "কোথায়", "কোথায়" => "কোথায়", "কই" => "কোথায়", "kothy" => "কোথায়", "kotay" => "কোথায়",
            "koi" => "কোথায়", "kai" => "কোথায়",

            "there" => "সেখানে", "at that" => "সেখানে", "সেখানে" => "সেখানে", "sakane" => "সেখানে",
            "sekane" => "সেখানে", "skane" => "সেখানে", "skne" => "সেখানে", "হেথায়" => "সেখানে",

            "why" => "কেন", "কেন" => "কেন", "kano" => "কেন", "keno" => "কেন", "কেনো" => "কেন",
            "kino" => "কেন",

            "whereto" => "যেখানে", "whereas" => "যেখানে", "যেখানে" => "যেখানে", "jekane" => "যেখানে",
            "jakane" => "যেখানে", "jkane" => "যেখানে", "jakne" => "যেখানে", "jekne" => "যেখানে",

            "gov" => "সরকারি", "government" => "সরকারি", "governmental" => "সরকারি", "goverment" => "সরকারি",
            "সরকারি" => "সরকারি", "govt" => "সরকারি", "govment" => "সরকারি", "sarkari" => "সরকারি",
            "sorkari" => "সরকারি", "srkari" => "সরকারি", "sakri" => "সরকারি", "sorkri" => "সরকারি", "offical" => "সরকারি",

            "nongov" => "বেসরকারি", "non-government" => "বেসরকারি", "nongovt" => "বেসরকারি", "nongovernmental" => "বেসরকারি",
            "unoffical" => "বেসরকারি", "besorkari" => "বেসরকারি", "basorkari" => "বেসরকারি", "bsorkari" => "বেসরকারি",
            "bsarkari" => "বেসরকারি",

            "address" => "ঠিকানা", "addressses" => "ঠিকানা", "location" => "ঠিকানা", "trace" => "ঠিকানা", "ঠিকানা" => "ঠিকানা",
            "thkana" => "ঠিকানা", "thikana" => "ঠিকানা", "tikana" => "ঠিকানা", "tkana" => "ঠিকানা",

            "id card" => "পরিচয় পএ", "identity card" => "পরিচয় পএ", "পরিচয় পএ" => "পরিচয় পএ",
            "porichay patra" => "পরিচয় পএ", "parichay patra" => "পরিচয় পএ", "parichoy patra" => "পরিচয় পএ",
            "porichoy patra" => "পরিচয় পএ", "parichay potra" => "পরিচয় পএ", "parichay potro" => "পরিচয় পএ",

            "charter" => "সনদ", "diploma" => "সনদ", "সনদ" => "সনদ", "sanad" => "সনদ", "sonad" => "সনদ", 
            "sonod" => "সনদ", "certificate" => "সনদ",

            "mother" => "মা", "ma" => "মা",  "mom" => "মা", "আম্মু" => "মা", "মাতা" => "মা", "মা" => "মা",

            "father" => "বাবা", "baba" => "বাবা", "বাবা" => "বাবা", "আব্বু" => "বাবা", "আব্বা" => "বাবা",

            "rights" => "অধিকার", "claim" => "অধিকার", "অধিকার" => "অধিকার", "odikar" => "অধিকার",
            "odikr" => "অধিকার", "odiker" => "অধিকার",

            "help" => "সহায়তা", "support" => "সহায়তা", "kindness" => "সহায়তা", "সহায়তা" => "সহায়তা",
            "sohayta" => "সহায়তা", "sohyta" => "সহায়তা", "sohayota" => "সহায়তা",

            "help in" => "সাহায্য", "সাহায্য" => "সাহায্য", "favor" => "সাহায্য", "sahajo" => "সাহায্য",
            "sahajho" => "সাহায্য", "sahaja" => "সাহায্য",

            "tax" => "কর", "taxs" => "কর", "কর" => "কর", "ট্যাক্স" => "কর", "taxes" => "কর",

            "vat" => "ভ্যাট", "bat" => "ভ্যাট", "ভ্যাট" => "ভ্যাট",

            "income tax" => "আয়কর", "income-tax" => "আয়কর", "incometax" => "আয়কর",

            "court" => "আদালত", "accourt" => "আদালত", "courts" => "আদালত", "judicature" => "আদালত",
            "law court" => "আদালত", "court of justice" => "আদালত", "আদালত" => "আদালত", "adalot" => "আদালত",
            "adlot" => "আদালত", "adalat" => "আদালত",

            "justice" => "বিচার", "judged" => "বিচার", "বিচার" => "বিচার", "bichr" => "বিচার", "bicahr" => "বিচার",
            "bicar" => "বিচার", "bichar" => "বিচার",

            "equity" => "ন্যায়", "honesty" => "ন্যায়", "ন্যায়" => "ন্যায়", "naye" => "ন্যায়", "nye" => "ন্যায়",

            "original" => "আসল", "real" => "আসল", "actual" => "আসল", "genuine" => "আসল", "আসল" => "আসল",
            "authentic" => "আসল", "asol" => "আসল", "ashol" => "আসল",

            "fake" => "নকল", "নকল" => "নকল", "nocal" => "নকল", "nocol" => "নকল", "nacal" => "নকল", "জাল" => "নকল",

            "trap" => "জাল", "জাল" => "জাল", "jal" => "জাল", "jaal" => "জাল", "gal" => "জাল", "gaal" => "জাল",

            "fraud" => "জুয়াচুরি", "জুয়াচুরি" => "জুয়াচুরি", "joachori" => "জুয়াচুরি", "joacri" => "জুয়াচুরি",

            "wrong" => "অন্যায়", "অন্যায়" => "অন্যায়", "onaye" => "অন্যায়", "onay" => "অন্যায়", "onnay" => "অন্যায়",

            "bad" => "খারাপ", "evil" => "খারাপ", "খারাপ" => "খারাপ", "karap" => "খারাপ", "kharap" => "খারাপ",
            "khrap" => "খারাপ", "kharp" => "খারাপ",

            "bangladesh" => "বাংলাদেশ", "bangladeshi" => "বাংলাদেশ", "বাংলাদেশ" => "বাংলাদেশ", "দেশ" => "বাংলাদেশ",
            "দেশে" => "বাংলাদেশ",

            "police" => "পুলিশ", "polic" => "পুলিশ", "পুলিশ" => "পুলিশ", "আইনের রক্ষক" => "পুলিশ", "পুলিশি" => "পুলিশ",

            "our" => "আমাদের", "of us" => "আমাদের", "us" => "আমাদের", "আমাদের" => "আমাদের", "amader" => "আমাদের",
            "amder" => "আমাদের", "amdr" => "আমাদের", "ours" => "আমাদের",

            "theirs" => "তাদের", "their" => "তাদের", "তাদের" => "তাদের", "tader" => "তাদের", "tder" => "তাদের",
            "tadar" => "তাদের", "tadr" => "তাদের",

            "they" => "তারা", "tara" => "তারা", "theyre" => "তারা", "তারা" => "তারা", "tra" => "তারা",

            "of those" => "যারা", "যারা" => "যারা", "jara" => "যারা", "jra" => "যারা",

            "time" => "সময়", "period" => "সময়", "age" => "সময়", "during" => "সময়", "সময়" => "সময়",
            "somoy" => "সময়", "samoy" => "সময়", "shomoy" => "সময়",

            "passages" => "অনুচ্ছেদ", "paragraph" => "অনুচ্ছেদ", "paragraphs" => "অনুচ্ছেদ", "অনুচ্ছেদ" => "অনুচ্ছেদ",
            "section" => "অনুচ্ছেদ", "sections" => "অনুচ্ছেদ", "onoched" => "অনুচ্ছেদ", "ohnoched" => "অনুচ্ছেদ",
            "onochad" => "অনুচ্ছেদ",

            "0" => "০", "০" => "০", "zero" => "০", "শুন্য" => "০",

            "1" => "১", "১" => "১", "one" => "১", "এক" => "১",

            "2" => "২", "২" => "২", "two" => "২", "দুই" => "২",

            "3" => "৩", "৩" => "৩", "three" => "৩", "তিন" => "৩",

            "4" => "৪", "৪" => "৪", "four" => "৪", "চার" => "৪",

            "5" => "৫", "৫" => "৫", "five" => "৫", "পাঁচ" => "৫",

            "6" => "৬", "৬" => "৬", "six" => "৬", "ছয়" => "৬",

            "7" => "৭", "৭" => "৭", "seven" => "৭", "সাত" => "৭",

            "8" => "৮", "৮" => "৮", "eight" => "৮", "আট" => "৮",

            "9" => "৯", "৯" => "৯", "nine" => "৯", "নয়" => "৯",


        ];

        $word_list = array_filter(explode("\n", strtolower(trim($text))));

        $result = [];

        foreach($word_list as $w){

            $result[] = $words[$w] ?? $w;

        }

        $full_text = implode(" ", $result);

        return $full_text;

    }

}