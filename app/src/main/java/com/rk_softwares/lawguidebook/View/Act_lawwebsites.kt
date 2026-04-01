package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rk_softwares.lawguidebook.Helper.BanglaFont
import com.rk_softwares.lawguidebook.Helper.ComposeHelper
import com.rk_softwares.lawguidebook.Helper.IntentHelper
import com.rk_softwares.lawguidebook.Helper.InternetChecker
import com.rk_softwares.lawguidebook.Helper.InternetStatus
import com.rk_softwares.lawguidebook.Helper.KeyHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.Model.WebsiteData
import com.rk_softwares.lawguidebook.Presenter.LawWebsitePresenter
import com.rk_softwares.lawguidebook.Presenter.LawWebsites
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.View.theme_main.LightNav
import com.rk_softwares.lawguidebook.View.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBarIcon
import kotlinx.coroutines.delay

class Act_lawwebsites : ComponentActivity(), InternetStatus, LawWebsites {

    private lateinit var presenter: LawWebsitePresenter
    private lateinit var internetChecker: InternetChecker

    //init------
    private var isInternet = mutableStateOf(false)
    private val websiteList = mutableStateListOf<WebsiteData>()
    private var serverStatus = mutableStateOf("")


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

            LaunchedEffect(isInternet.value) {

                if (isInternet.value){

                    presenter.websitesData()

                }

            }


            LawGuideBookTheme {
                LawWebsitesFullScreen(
                    backClick = { finish() },
                    internet = isInternet.value,
                    websiteList = websiteList,
                    websiteTitleClick = {

                        IntentHelper.dataIntent(this, Act_browser::class.java,
                            KeyHelper.lawWebsiteLink_IntentKey(), it)

                    }
                )
            }
        }
    }//on create===============================================

    private fun init(){
        presenter = LawWebsitePresenter(this)
        internetChecker = InternetChecker(this, this)

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
        websiteList.addAll(list)
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
    websiteTitleClick : (String) -> Unit = {}
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

            Column(

                modifier = Modifier
                    .fillMaxSize()

            ) {


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

                }

            }//column

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
    modifier: Modifier = Modifier,
    title : String = "Title",
    titleClick : () -> Unit = {},
) {

    var isBookmarkVisible by remember { mutableStateOf(false) }

    Box(

        modifier = modifier
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
                fontFamily = BanglaFont.font(),
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