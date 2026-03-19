<?php

namespace App\Database;

use Exception;
use PDO;
use Slim\Psr7\Response;

class DB{

    private static $connection;

    private static function connect(){

        if(!self::$connection){

            $db_host = $_ENV['DB_HOST'];
            $db_name = $_ENV['DB_NAME'];
            $db_user = $_ENV['DB_USER'];
            $db_pass = $_ENV['DB_PASSWORD'];

            $mysql_dsn = "mysql:host=$db_host;dbname=$db_name;charset=utf8mb4";

            try{

                self::$connection = new PDO(
                    $mysql_dsn,
                    $db_user,
                    $db_pass
                );

                self::$connection->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

            }catch(Exception $e){

                $response = new Response();

                if($_ENV['DEBUG'] === "false"){

                    $response->getBody()->write(json_encode([

                        "status" => "Error",
                        "message" => "Please try again"

                    ]));

                    return $response->withHeader("Content-Type", "application/json");

                }

                die("Databse not connected");

            }

        }

    }

    public static function query(string $sql, array $params = []){

        self::connect();

        $stmt = self::$connection->prepare($sql);
        $stmt->execute($params);

        return $stmt->fetchAll(PDO::FETCH_ASSOC);       //return array with data

    }

    public static function find(string $sql, array $params = []){

        self::connect();

        $stmt = self::$connection->prepare($sql);
        $stmt->execute($params);

        $data = [];

        while($row = $stmt->fetch(PDO::FETCH_ASSOC)){

            $data[] = $row;

        }

        return $data;       //return boolean with data
    }

    public static function findOne(string $sql , array $params = []){

        self::connect();

        $stmt = self::$connection->prepare($sql);
        $stmt->execute($params);

        return $stmt->fetch(PDO::FETCH_ASSOC);      //return boolean with data

    }

    public static function insertOne(string $sql, array $params){

        self::connect();

        $stmt = self::$connection->prepare($sql);
        $isInserted = $stmt->execute($params);

        return $isInserted;

    }

}