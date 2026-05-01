<?php

namespace App\Helper;

class ErrorHelper{

    public static function log(string $type, string $message) : void{

        date_default_timezone_set("Asia/Dhaka");

        $date = date("Y-m-d h:i:s A");

        file_put_contents(
            "php_error_log.txt",
            "[$date] >> [$type] >> $message" . PHP_EOL, FILE_APPEND
        );

    }

    public static function register() : void{

        set_error_handler(function($errno, $errstr, $errfile, $errline){

            $type = match($errno){

                E_WARNING => "WARNING",
                E_NOTICE => "NOTICE",
                default => "ERROR"

            };

            self::log($type,
            "$errstr in $errfile on line $errline");


        });

        set_exception_handler(function($e){

            self::log(
                "EXCEPTION",
                "{$e->getMessage()} in {$e->getFile()} on Line {$e->getLine()}"
            );

        });

    }




}