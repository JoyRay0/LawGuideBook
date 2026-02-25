package com.rk_softwares.lawguidebook.Helper

import android.content.Context
import android.widget.Toast

object ShortMessageHelper {

    fun toast(context: Context, message : String){

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    }

}