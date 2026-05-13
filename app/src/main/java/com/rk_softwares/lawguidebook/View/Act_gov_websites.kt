package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.rk_softwares.lawguidebook.Helper.*
import com.rk_softwares.lawguidebook.Model.Website
import com.rk_softwares.lawguidebook.Presenter.GovWebsitePresenter
import com.rk_softwares.lawguidebook.Presenter.GovWebsites
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.*
import kotlinx.coroutines.delay

class Act_gov_websites : ComponentActivity(), InternetStatus, GovWebsites {

    //init
    private lateinit var internetChecker : InternetChecker

    private lateinit var presenter: GovWebsitePresenter


    private var isInternet = mutableStateOf(false)

    private var govWebsiteList = mutableStateListOf<Website>()

    private var serverStatus = mutableStateOf("")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        init()

        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = true
            )

            LawGuideBookTheme {

                GovWebsitesFullScreen(
                    backClick = { finish() },
                    internet = isInternet.value
                )

            }
        }
    }//on create==============================

    private fun init() {

        internetChecker = InternetChecker(this, this)
        presenter = GovWebsitePresenter(this)

    }

    override fun onStart() {
        super.onStart()
        internetChecker.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        internetChecker.onStop()
    }

    override fun isInternet(internet: Boolean) {
        isInternet.value = internet
    }

    override fun websiteList(list: List<Website>) {
        govWebsiteList.clear()
        govWebsiteList.addAll(list)
    }

    override fun serverStatus(status: String) {
        serverStatus.value = status
    }

}//class=======================================

@Preview(showBackground = true)
@Composable
private fun GovWebsitesFullScreen(
    backClick: () -> Unit = {},
    internet: Boolean = false,
    serverStatus : String = ""
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
            backClick = { backClick() }
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

            //internet dialog

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
private fun Toolbar(
    backClick : () -> Unit = {},
) {

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


@Preview(showBackground = true)
@Composable
private fun Item(
    title : String = "Title",
    titleClick : () -> Unit = {},
) {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)

    ) {

        Box(

            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(12.dp))
                .combinedClickable(
                    onClick = {
                        titleClick()

                    }
                )
                //.background(color = Color(0xFFFFFFFF))
                .border(width = 1.dp, color = Color(0xFFFAC8C8), shape = RoundedCornerShape(12.dp))
                .padding(7.dp)
                .align(Alignment.Center)

        ) {

            Text(text = title,
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
                modifier = Modifier
                    .fillMaxWidth(0.93f)
                    .padding(3.dp)
                    .align(Alignment.CenterStart)
            )

            Icon( painter = painterResource(R.drawable.ic_right),
                contentDescription = "Right",
                tint = Color.Gray,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterEnd)

            )

        }//box

    }//box

}//fun end

