<?php

class JsonMessage{


    function dieMessage($status, $message){


        die(json_encode([
            "status" => $status,
            "message" => $message
        ]));

    }

    function errorMessage($status, $message){

        echo json_encode([

            "status" => $status,
            "message" => $message

        ]);

    }

    function successMessage($status, $from, $data){

        echo json_encode([

            "status" => $status,
            "from" => $from,
            "data" => $data

        ]);

    }

}