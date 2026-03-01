<?php

header('Content-Type: application/json; charset=utf-8');

$data = json_decode(file_get_contents("php://input"), true);

$questions = [

    [

        "id" => "1",
        "question" => "সরণ কাকে বলে? কত প্রকার ও কি কি?"

    ],
    [

        "id" => "2",
        "question" => "কেন্দ্রীয় প্রবণতা কাকে বলে? কেন এটি পরিসংখ্যানের প্রাণকেন্দ্র?"

    ],
    [

        "id" => "3",
        "question" => "দর্শন কাকে বলে? কত প্রকার ও কি কি?"

    ],
    [

        "id" => "4",
        "question" => "রাষ্ট্রবিজ্ঞান কাকে বলে?"

    ],
    [

        "id" => "5",
        "question" => "বিশ্বের সর্বকালের সেরা ফুটবলার কে? ২০২৫"

    ],
    [

        "id" => "6",
        "question" => "বাক্য কাকে বলে? বাক্যের প্রকারভেদ?"

    ],
    [

        "id" => "7",
        "question" => "কারক ও বিভক্তি কাকে বলে?"

    ]

];

if($data['title'] == "a"){

    echo json_encode([

        "status" => "Success",
        "items" => $questions

    ]);

}else{

    echo json_encode([

        "status" => "failed",
        "message" => "empty search"

    ]);

}