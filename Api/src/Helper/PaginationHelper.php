<?php

namespace App\Helper;


class PaginationHelper{

    public static function getLimitOffset(int $page, int $limit = 20) : array{

        $pages = $page > 0 ? $page : 1;

        $offset = ($pages - 1) * $limit;         // Page = 1 , 1-1 = 0 * 20  = 0

        return [
            "limit" => $limit,
            "offset" => $offset
        ];

    }

}