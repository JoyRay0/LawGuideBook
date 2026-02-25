package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.rk_softwares.lawguidebook.View.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.View.theme_main.LightNav
import com.rk_softwares.lawguidebook.View.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.Helper.InternetChecker
import com.rk_softwares.lawguidebook.Helper.KeyHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.R

class Act_webview : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var intentText by remember { mutableStateOf("") }
            var websiteLink by remember { mutableStateOf("") }

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            intentText = intent.getStringExtra(KeyHelper.otherApp_privacy_IntentKey()) ?: ""

            if (intentText == "other_apps"){

                websiteLink = "https://sites.google.com/view/rk-softwares-official-site"

            }else{

                websiteLink = "https://sites.google.com/view/lawguidebookapp/home"

            }

            val internet = InternetChecker.liveInternetStatus(this)

            Toast.makeText(this,if (internet) "On" else "Off", Toast.LENGTH_SHORT).show()

            LawGuideBookTheme {

                WebViewFullScreen(
                    backClick = {
                        finish()
                        intentText = ""
                        websiteLink = ""
                    },
                    websiteLink = websiteLink
                )

            }

            BackHandler{
                finish()
                intentText = ""
                websiteLink = ""
            }
        }
    }//on create=================================
}//class========================================

@Preview(showBackground = true)
@Composable
private fun WebViewFullScreen(
    backClick: () -> Unit = {},
    websiteLink : String = "",

) {

    Scaffold(
        topBar = { ToolBar( backClick = {backClick()} ) },
        modifier = Modifier.fillMaxSize())
    { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {

            AndroidView(
                factory = { context ->

                    val webView = WebView(context)

                    //webView.settings.javaScriptEnabled = true
                    webView.webViewClient = WebViewClient()
                    webView.loadUrl(websiteLink)

                    webView
                },
                modifier = Modifier.fillMaxSize()
            )


        }//column

    }//scaffold

}//fun end


@Preview(showBackground = true)
@Composable
private fun ToolBar(backClick : () -> Unit = {}) {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .background(color = LightToolBar)

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)

        ) {

            IconButton(
                onClick = { backClick() },
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically)
            ) {

                Icon( painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color(0xFFFFFFFF),
                    modifier = Modifier
                        .wrapContentWidth()

                )

            }

        }//row

    }//box

}//fun end