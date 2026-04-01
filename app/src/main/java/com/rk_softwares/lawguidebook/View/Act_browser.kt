package com.rk_softwares.lawguidebook.View

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.provider.Browser
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.rk_softwares.lawguidebook.Helper.BanglaFont
import com.rk_softwares.lawguidebook.Helper.ComposeHelper
import com.rk_softwares.lawguidebook.Helper.InternetChecker
import com.rk_softwares.lawguidebook.Helper.InternetStatus
import com.rk_softwares.lawguidebook.Helper.KeyHelper
import com.rk_softwares.lawguidebook.Helper.ShortMessageHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.View.theme_main.LightNav
import com.rk_softwares.lawguidebook.View.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBarIcon
import kotlinx.coroutines.delay

class Act_browser : ComponentActivity(), InternetStatus {

    private lateinit var internetChecker: InternetChecker

    //init-------------
    private var isInternet = mutableStateOf(false)
    private var website = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            init()

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = true
            )

            website.value = intent.getStringExtra(KeyHelper.lawWebsiteLink_IntentKey()) ?: ""

            LawGuideBookTheme {

                BrowserFullScreen(
                    backClick = {
                        finish()
                        website.value = ""
                    },
                    website = website.value,
                    internet = isInternet.value,
                )

            }

            BackHandler {

                finish()
                website.value = ""

            }
        }
    }//on create=================================

    private fun init(){

        internetChecker = InternetChecker(this, this)

        internetChecker.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        //internetChecker.onStop()
    }

    override fun onStop() {
        super.onStop()
        internetChecker.onStop()
    }

    override fun isInternet(internet: Boolean) {
        isInternet.value = internet
    }

}//class==========================================



@Preview(showBackground = true)
@Composable
private fun BrowserFullScreen(
    backClick: () -> Unit = {},
    website : String = "",
    internet: Boolean = false
) {

    var isInternetDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(internet) {

        if (!internet){

            delay(2000L)

            isInternetDialogVisible = true

        }else{

            isInternetDialogVisible = false

        }

    }

    Scaffold(
        topBar = { Toolbar(
            backClick = { backClick() },
        ) },
        modifier = Modifier
            .fillMaxSize()
            .background(color = LightStatusBar)
            .systemBarsPadding()
    ) { innerPadding ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {

            if (website.isNotEmpty()){

                WebView(
                    websiteLink = website
                )

            }

            if (isInternetDialogVisible){

                ComposeHelper.InternetDialog(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )

            }

        }//box

    }//scaffold

}//fun end

@Preview(showBackground = true)
@Composable
private fun Toolbar(
    backClick : () -> Unit = {},
) {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .background(color = LightToolBar)

    ) {

        Box(

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
                    .align(Alignment.CenterStart)
            ) {

                Icon( painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = LightToolBarIcon,
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(22.dp)

                )

            }

        }//box

    }//box

}//fun end


@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebView(
    websiteLink : String = ""
) {

    var isLoading = remember { mutableStateOf(true) }


    Box(

        modifier = Modifier
            .fillMaxSize()

    ) {


        AndroidView(
            factory = { context ->
                WebView(context).apply {

                    webViewClient = object : WebViewClient(){

                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                        ) {
                            super.onReceivedSslError(view, handler, error)
                            handler?.proceed()
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading.value = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading.value = false
                        }

                    }

                    settings.apply {

                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false


                        allowFileAccess = false
                        allowContentAccess = false

                        allowFileAccessFromFileURLs = false
                        allowFileAccessFromFileURLs = false

                    }
                    loadUrl(websiteLink)
                }
            },
            modifier = Modifier
                .fillMaxSize()
        )

        if (isLoading.value){

            CircularProgressIndicator(

                modifier = Modifier
                    .wrapContentWidth()
                    .size(50.dp)
                    .align(Alignment.Center),
                strokeWidth = 5.dp,
                color = Color(0xFF9C27B0),
                trackColor = Color.LightGray

            )

        }

    }//box

}//fun end