<?php

namespace App\Helper;


class SanitizeHelper{

    public static function inputString(string $input) : string{

        $data = trim(preg_replace("/[^p{L}\s\?]/u", "", $input) ?? "");

        return $data;
    }

    public static function chatString(string $input) : string{

        return trim(strip_tags(preg_replace("/\s+/", "", $input)) ?? "");

    }

}