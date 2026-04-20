package com.rk_softwares.lawguidebook.Helper

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.rk_softwares.lawguidebook.R

object Bangla {

    fun banglaFont() : FontFamily{

        return FontFamily(Font(R.font.noto_serif_bengali))

    }

    fun banglaNumber(number : String) : String{

        val enNumber = arrayOf(".", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

        val bnNumber = arrayOf(".", "০", "১", "২", "৩", "৪", "৫", "৬", "৭", "৮", "৯")

        val numberList = number.map { it.toString() }.toMutableList()

        val list = mutableListOf<String>()

        for (ch in numberList){

            val index = enNumber.indexOf(ch)

            if (index != -1){

                list.add(bnNumber[index])

            }else{

                list.add(ch)

            }

        }

        val finalNumber = list.joinToString("")

        return finalNumber
    }

}