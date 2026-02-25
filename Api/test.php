<?php

header('Content-Type: application/json; charset=utf-8');

$data = json_decode(file_get_contents("php://input", ),true);

if($data['user_message'] == "hi"){

    echo json_encode([

        "status" => "success",
        "ai_message" => "Hello"

    ]);

} elseif($data['user_message'] == "how are you"){

    echo json_encode([

        "status" => "success",
        "ai_message" => "Iam fine, and you ?"

    ]);

} elseif($data['user_message'] == "i am fine"){

    echo json_encode([

        "status" => "success",
        "ai_message" => "I am ready to help you, you can ask me any question"

    ]);

}else{


    echo json_encode([

        "status" => "failed",
        "message" => "empty data"

    ]);

}