package com.rk_softwares.lawguidebook.Helper

object WebviewHelper {

    private var cacheUA : String? = null

    fun userAgent() : String{

        val mobileAgent = arrayOf(

            //Android (Chrome)
            "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36",

            //Android (Firefox)
            "Mozilla/5.0 (Android 10; Mobile; rv:120.0) Gecko/120.0 Firefox/120.0",

            //Android (Edge)
            "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 EdgA/120.0.0.0",

            //Android (Brave)
            "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 Brave/120.0.0.0",

            //Android (Opera)
            "Mozilla/5.0 (Linux; Android 10; SM-G973F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 OPR/80.0.0.0",

            //Iphone (Safari)
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1",

        )

        val pcAgent = arrayOf(

            //Windows (Chrome)
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",

            //Windows(Firefox)
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0",
        )

        if (cacheUA == null){

            cacheUA = mobileAgent.random()

        }

        return cacheUA!!
    }

    fun header() : Map<String, String>{

        val map = mutableMapOf<String, String>()

        map["Accept"] = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
        map["Accept-Language"] = "en-US,en;q=0.9"
        map["Connection"] = "keep-alive"
        map["Upgrade-Insecure-Requests"] = "1"
        map["Cache-Control"] = "max-age=0"
        map["DNT"] = "1"
        map["Sec-Fetch-Dest"] = "document"
        map["Sec-Fetch-Mode"] = "navigate"
        map["Sec-Fetch-Site"] = "none"
        map["Sec-Fetch-User"] = "?1"


        return map
    }

}