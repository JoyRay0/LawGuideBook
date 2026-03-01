<?php

header('Content-Type: application/json; charset=utf-8');

$res = [

    [
        "image" => "https://rksoftwares.fun/All_app/cdn/images/ad.png",
        "title" => "আইন"
    ],
    [
        "image" => "https://rksoftwares.fun/All_app/cdn/images/education.png",
        "title" => "পুলিশ স্টেশন"
    ],
    [
        "image" => "https://rksoftwares.fun/All_app/cdn/images/agriculture.png",
        "title" => "জমি"
    ],
    [
        "image" => "https://rksoftwares.fun/All_app/cdn/images/food.png",
        "title" => "লোডিং"
    ],

];

echo json_encode([

    "status" => "Success",
    "items" => $res

]);