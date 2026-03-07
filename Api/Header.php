<?php

class HeadersManager{

    private function cacheHeader(){

        header("Cache-Control: public, max-age=3600");

    }

    private function securityHeader(){

        // Strict-Transport-Security হেডার
        header('Strict-Transport-Security: max-age=31536000; includeSubDomains');

        // X-Content-Type-Options হেডার
        header('X-Content-Type-Options: nosniff');

        // XSS সুরক্ষা হেডার
        header('X-XSS-Protection: 1; mode=block');

        // X-Frame-Options হেডার
        header('X-Frame-Options: DENY');

        // Referrer-Policy হেডার
        header('Referrer-Policy: no-referrer');

    }

    private function httpHeader(){

        header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");

        header('Content-Type: application/json; charset=utf-8');

        header("Access-Control-Allow-Origin: https://rksoftwares.fun");

    }

    public function setAllHeaders(){

        $this->cacheHeader();
        $this->securityHeader();
        $this->httpHeader();

    }

}