package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import android.webkit.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.rk_softwares.lawguidebook.Helper.ComposeHelper
import com.rk_softwares.lawguidebook.View.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.View.theme_main.LightNav
import com.rk_softwares.lawguidebook.View.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.Helper.InternetChecker
import com.rk_softwares.lawguidebook.Helper.InternetStatus
import com.rk_softwares.lawguidebook.Helper.KeyHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBarIcon
import kotlinx.coroutines.delay

class Act_webview : ComponentActivity(), InternetStatus {//class========================================

    private lateinit var internetChecker: InternetChecker

    //init-----

    private var isInternet = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            init()

            var intentText by remember { mutableStateOf("") }
            var websiteLink by remember { mutableStateOf("") }

            internetChecker.onStart()

            intentText = intent.getStringExtra(KeyHelper.otherApp_privacy_IntentKey()) ?: ""

            if (intentText == "other_apps"){

                websiteLink = "https://sites.google.com/view/rk-softwares-official-site"

            }else{

                websiteLink = "https://sites.google.com/view/lawguidebookapp/home"

            }


            LawGuideBookTheme {

                WebViewFullScreen(
                    backClick = {
                        finish()
                        intentText = ""
                        websiteLink = ""
                    },
                    websiteLink = websiteLink,
                    internet = isInternet.value
                )

            }

            BackHandler{
                finish()
                intentText = ""
                websiteLink = ""
            }
        }
    }//on create=================================

    private fun init(){

        internetChecker = InternetChecker(this, this)

    }

    override fun onDestroy() {
        super.onDestroy()
        internetChecker.onStop()
    }

    override fun isInternet(internet: Boolean) {
        isInternet.value = internet
    }
}

@Preview(showBackground = true)
@Composable
private fun WebViewFullScreen(
    backClick: () -> Unit = {},
    websiteLink : String = "",
    internet : Boolean = false
) {
    val context = LocalContext.current
    var isInternetDialogVisible by remember { mutableStateOf(false) }
    val webview = remember { WebView(context) }

    LaunchedEffect(internet) {

        if (!internet){

            delay(2000L)

            isInternetDialogVisible = true

        }else{

            isInternetDialogVisible = false

        }

    }
    Scaffold(
        topBar = { ToolBar( backClick = {backClick()} ) },
        modifier = Modifier
            .fillMaxSize()
            .background(color = LightStatusBar)
            .systemBarsPadding()
    )
    { innerPadding ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {

            LaunchedEffect(internet) {

                if (internet){

                    webview.loadUrl(websiteLink)

                }

            }

            AndroidView(

                factory = { webview},

                /*
                    val webView = WebView(context)

                    //webView.settings.javaScriptEnabled = true
                    webView.webViewClient = WebViewClient()
                    webView.loadUrl(websiteLink)

                    webView
                },

                 */
                modifier = Modifier.fillMaxSize()
            )

            if (isInternetDialogVisible){

                ComposeHelper.InternetDialog(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                )

            }

        }//box

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
                    .clip(shape = CircleShape)
                    .size(35.dp)
                    .align(Alignment.CenterVertically)
            ) {

                Icon( painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = LightToolBarIcon,
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(22.dp)

                )

            }

        }//row

    }//box

}//fun end