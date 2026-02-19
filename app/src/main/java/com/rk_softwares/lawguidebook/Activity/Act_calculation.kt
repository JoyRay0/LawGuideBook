package com.rk_softwares.lawguidebook.Activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rk_softwares.lawguidebook.Activity.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.Activity.theme_main.LightNav
import com.rk_softwares.lawguidebook.Activity.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.Helper.ThemeHelper

class Act_calculation : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            LawGuideBookTheme {

                CalculationFullScreen()

            }

        }
    }//on create==========================================
}//class===================================================


@Preview(showBackground = true)
@Composable
private fun CalculationFullScreen() {

    Scaffold(
        modifier = Modifier.fillMaxSize())
    { innerPadding ->



    }//scaffold

}//fun end