package com.rk_softwares.lawguidebook.Helper

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.*

class ScreenSize {

    @Composable
    fun height() : Int {

        val configuration = LocalConfiguration.current

        return configuration.screenHeightDp

    }

    @Composable
    fun width() : Int{

        val configuration = LocalConfiguration.current

        return configuration.screenWidthDp

    }

    @Composable
    fun responsiveTextSize(screenSize700 : Int, screenSize1000 : Int, screenSizeElse : Int): TextUnit{

        val screenHeight = this.height()

        val tSize = when{

            screenHeight < 700 -> screenSize700.sp

            screenHeight < 1000 -> screenSize1000.sp

            else -> screenSizeElse.sp

        }

        return tSize

    }

    @Composable
    fun responsivePadding(screenSize700 : Int, screenSize1000 : Int, screenSizeElse : Int) : Dp{

        val screenHeight = this.height()

        val padding = when{

            screenHeight < 700 -> screenSize700.dp

            screenHeight < 1000 -> screenSize1000.dp

            else -> screenSizeElse.dp

        }

        return padding

    }

    @Composable
    fun responsiveImageSize(screenSize700 : Int, screenSize1000 : Int, screenSizeElse : Int) : Dp{

        val screenHeight = this.height()

        val imageSize = when{

            screenHeight < 700 -> screenSize700.dp

            screenHeight < 1000 -> screenSize1000.dp

            else -> screenSizeElse.dp

        }

        return imageSize

    }

    @Composable
    fun responsiveHeightWidth(screenSize700 : Int, screenSize1000 : Int, screenSizeElse : Int) : Dp{

        val screenHeight = this.height()

        val height_width = when{

            screenHeight < 700 -> screenSize700.dp

            screenHeight < 1000 -> screenSize1000.dp

            else -> screenSizeElse.dp

        }

        return height_width

    }

}