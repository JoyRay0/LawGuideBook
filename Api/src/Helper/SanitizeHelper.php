<?php

namespace App\Helper;


class SanitizeHelper{

    public static function inputString(string $input) : string{

        $data = trim(strip_tags($input));

        return $data;
    }

}