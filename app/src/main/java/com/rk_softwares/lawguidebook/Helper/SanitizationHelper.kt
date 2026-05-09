package com.rk_softwares.lawguidebook.Helper

object SanitizationHelper {

    fun sanitizedSearch(text : String) : String{

        val data = text.replace(Regex("""\p{L}\p{M}\p{P}\s\?"""), "")
            
        return data
    }

}