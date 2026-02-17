package com.rk_softwares.lawguidebook.Activity

import android.os.Bundle
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rk_softwares.lawguidebook.Activity.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.Activity.theme_main.LightNav
import com.rk_softwares.lawguidebook.Activity.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.R
import kotlinx.coroutines.delay

class Act_splash : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            LawGuideBookTheme {

                if (Build.VERSION.SDK_INT  < Build.VERSION_CODES.S){

                    SplashFullScreen()

                }


                LaunchedEffect(Unit) {

                    if (Build.VERSION.SDK_INT  < Build.VERSION_CODES.S){

                        delay(1500)

                    }

                    startActivity(Intent(this@Act_splash, Act_home::class.java))
                    finishAffinity()

                }

            }

        }
    }//on create======================================
}//class===============================================


@Preview(showBackground = true)
@Composable
private fun SplashFullScreen() {

    Scaffold(modifier = Modifier.fillMaxSize()

    ) { innerPadding ->


        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {

            Box(

                modifier = Modifier.fillMaxSize()

            ) {

                Image( painter = painterResource(R.drawable.img_law),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(140.dp)
                        .align(Alignment.Center)

                )

            }//box

        }//box

    }//scaffold

}//fun end
