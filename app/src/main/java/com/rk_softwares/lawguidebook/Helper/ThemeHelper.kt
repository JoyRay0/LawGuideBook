package com.rk_softwares.lawguidebook.Helper

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

object ThemeHelper {

    @Composable
    fun SystemUi(statusBarColor : Color, navColor : Color, darkIcons : Boolean ){

        val systemUi = rememberSystemUiController()

        systemUi.setStatusBarColor(
            color = statusBarColor,
            darkIcons = darkIcons
        )

        systemUi.setNavigationBarColor(
            color = navColor,
            darkIcons = darkIcons
        )

    }

}