<?php

header("Content-Type: text/html");

$user_ip = $_SERVER['REMOTE_ADDR'];
$method = $_SERVER['REQUEST_METHOD'];
$allowed_ip = "172.93.100.198";

if($user_ip !== $allowed_ip){

    echo "<h2>Access Denied</h2>";
    exit;

}

if($method !== 'GET'){

    echo "<h2>Not Allow</h2>";
    exit;

}

$db_host = "localhost";
$db_name = "jekkhjpw_lawguidebook";
$db_user = "jekkhjpw_lawguidebook";
$db_password = "VnP1WG#C39";

$connect = new mysqli($db_host, $db_user, $db_password, $db_name);

if($connect->connect_error){

    echo "<h2>Not connected</h2>";
    exit;

}

$tables = ["land", "police", "road", "labor", "ict", "education", "marriage", "tax", "customer", "migration"];

$stmt = $connect->prepare("INSERT INTO search (table_name, record_id, question, answer) VALUES (?, ?, ?, ?)
ON DUPLICATE KEY UPDATE
    question = VALUES(question)
");

foreach($tables as $table){

    $result = $connect->query("SELECT * FROM $table");

    while($row = $result->fetch_assoc()){

        $id = $row['id'];
        $question = $row['question'];
        $answer = $row['answer'];

        $stmt->bind_param("siss", $table, $id, $question, $answer);
        $stmt->execute();

    }

}

echo "<h2>Done</h2>";
