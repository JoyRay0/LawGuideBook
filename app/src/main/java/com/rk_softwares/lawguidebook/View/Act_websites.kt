package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.rk_softwares.lawguidebook.Helper.*
import com.rk_softwares.lawguidebook.Model.WebsiteData
import com.rk_softwares.lawguidebook.Presenter.WebsitePresenter
import com.rk_softwares.lawguidebook.Presenter.Websites
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class Act_websites : ComponentActivity(), InternetStatus, Websites {

    private lateinit var presenter: WebsitePresenter
    private lateinit var internetChecker: InternetChecker

    //init------
    private var isInternet = mutableStateOf(false)
    private val websiteList = mutableStateListOf<WebsiteData>()
    private var serverStatus = mutableStateOf("")

    private var websiteIntent = mutableStateOf("")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        init()

        websiteIntent.value = intent.getStringExtra(KeyHelper.website_IntentKey()) ?: ""

        setContent {

            var isApiDataLoaded = remember { mutableStateOf(false) }

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = true
            )

            LaunchedEffect(isInternet.value) {

                if (!isInternet.value) return@LaunchedEffect
                if (isApiDataLoaded.value) return@LaunchedEffect

                isApiDataLoaded.value = true

                if (websiteIntent.value == "law_websites"){

                    presenter.lawWebsites()

                }else {

                    presenter.govWebsites()

                }

            }

            LawGuideBookTheme {
                LawWebsitesFullScreen(
                    backClick = {
                        finish()
                        websiteIntent.value = ""
                        websiteList.clear()
                                },
                    internet = isInternet.value,
                    websiteList = websiteList,
                    websiteTitleClick = {

                        IntentHelper.dataIntent(this, Act_browser::class.java,
                            KeyHelper.websiteLink_IntentKey(), it)

                    },
                    serverStatus = serverStatus.value
                )
            }

            BackHandler {

                finish()
                websiteIntent.value = ""
                websiteList.clear()

            }

        }

    }//on create===============================================

    private fun init(){
        presenter = WebsitePresenter(this)
        internetChecker = InternetChecker(this, this)

    }

    override fun onStart() {
        super.onStart()
        internetChecker.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
        internetChecker.onStop()
    }

    override fun isInternet(internet: Boolean) {
        isInternet.value = internet
    }

    override fun websiteList(list: List<WebsiteData>) {
        websiteList.clear()
        websiteList.addAll(list)

        ShortMessageHelper.toast(this, list.size.toString())
    }

    override fun serverStatus(status: String) {
        serverStatus.value = status
    }
}//class=======================================================


@Preview(showBackground = true)
@Composable
private fun LawWebsitesFullScreen(
    backClick: () -> Unit = {},
    internet: Boolean = false,
    websiteList : List<WebsiteData> = emptyList(),
    websiteTitleClick : (String) -> Unit = {},
    serverStatus : String = ""
){

    var isInternetDialogVisible by remember { mutableStateOf(false) }
    val lazyState = rememberLazyListState()

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

            Box(

                modifier = Modifier
                    .fillMaxSize()

            ) {


                when(serverStatus){

                    "websites_pending"->{

                        ComposeHelper.CircularProgressBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                        )

                    }
                    "websites_success"->{

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            state = lazyState
                        ) {

                            items(

                                items = websiteList,
                                key = { it.id }

                            ){ it ->

                                Item(
                                    title = it.title,
                                    titleClick = { websiteTitleClick(it.websiteLink) }
                                )

                            }

                        }//lazy column

                    }
                    else -> null

                }

            }//box

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

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)

        ) {

            IconButton(
                onClick = { backClick() },
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(CircleShape)
                    .size(35.dp)
                    .align(Alignment.CenterVertically)
            ) {

                Icon( painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = LightToolBarIcon,
                    modifier = Modifier
                        .size(22.dp)
                        .wrapContentWidth()

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