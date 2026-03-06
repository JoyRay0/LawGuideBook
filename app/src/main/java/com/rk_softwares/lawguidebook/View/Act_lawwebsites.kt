package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rk_softwares.lawguidebook.Helper.BanglaFont
import com.rk_softwares.lawguidebook.Helper.ComposeHelper
import com.rk_softwares.lawguidebook.Helper.InternetStatus
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.Model.WebsiteData
import com.rk_softwares.lawguidebook.Presenter.LawWebsitePresenter
import com.rk_softwares.lawguidebook.Presenter.LawWebsites
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.View.theme_main.LightNav
import com.rk_softwares.lawguidebook.View.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBar

class Act_lawwebsites : ComponentActivity(), InternetStatus, LawWebsites {

    private lateinit var presenter: LawWebsitePresenter

    //init------
    private var isInternet = mutableStateOf(false)
    private val websiteLink = mutableStateListOf<WebsiteData>()
    private var serverStatus = mutableStateOf("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            init()

            LawGuideBookTheme {
                LawWebsitesFullScreen(
                    backClick = { finish() },
                    internet = isInternet.value
                )
            }
        }
    }//on create===============================================

    private fun init(){
        presenter = LawWebsitePresenter(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun isInternet(internet: Boolean) {
        isInternet.value = internet
    }

    override fun websiteList(list: List<WebsiteData>) {
        websiteLink.addAll(list)
    }

    override fun serverStatus(status: String) {
        serverStatus.value = status
    }
}//class=======================================================


@Preview(showBackground = true)
@Composable
private fun LawWebsitesFullScreen(
    backClick: () -> Unit = {},
    internet: Boolean = false
){

    var isInternetDialogVisible by remember { mutableStateOf(false) }

    if (internet) isInternetDialogVisible = false else isInternetDialogVisible = true

    Scaffold(
        topBar = { Toolbar(
            backClick = { backClick() }
        ) },
        modifier = Modifier.fillMaxSize()

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



            }//column

            if (isInternetDialogVisible){

                ComposeHelper.InternetDialog(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    closeClick = { isInternetDialogVisible = false },
                    openClick = { isInternetDialogVisible = false }
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