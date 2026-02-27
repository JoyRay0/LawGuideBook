<?php

header('Content-Type: application/json; charset=utf-8');

$data = json_decode(file_get_contents("php://input", ),true);

$res = [
        "question" => "",
        "answer" => 
        "শব্দ গুলো দেখে সকলেই অবাক হয়েছে, এটা বলা বাহুল্য।\n".
        "অনেকেই ভাবছেন, এ সব শব্দের বেশিরভাগটাই নিজে থেকে বানানো।\n\n".
        "তবে **[সত্যটা](https://www.google.com)** হলো, এর একটাও বানানো নয়। সব গুলোই বাংলা ভাষার অংশ।\n ".
        "উৎপত্তি অনুসারে বাংলাভাষার যে শ্রেনীবিভাগ রয়েছে, সেই **পাঁচটি** শ্রেনী বিভাগের ফলেই জন্ম নিয়েছে এই সকল শব্দ, যার অনেকগুলোই আপনি আপনার এই পর্যন্ত জীবনে একবারও শোনেননি।\n\n".        
        "[Google](https://www.google.com) \n".
                    
        "| Key | First Name | Last Name |\n".
        "|---|------|---------|\n".
        "| 1 | Joy | Ray |\n".
        "| 2 | Arjun | Ray |"

];



echo json_encode([

        "status" => "Success",
        "answer_data" => $res

]);