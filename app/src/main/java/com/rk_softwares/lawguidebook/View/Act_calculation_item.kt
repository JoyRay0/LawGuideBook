package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.unit.*
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
import coil3.compose.AsyncImage
import com.rk_softwares.lawguidebook.Helper.BanglaFont
import com.rk_softwares.lawguidebook.Helper.ComposeHelper
import com.rk_softwares.lawguidebook.Helper.InternetChecker
import com.rk_softwares.lawguidebook.Helper.InternetStatus
import com.rk_softwares.lawguidebook.Helper.ShortMessageHelper
import com.rk_softwares.lawguidebook.View.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.View.theme_main.LightNav
import com.rk_softwares.lawguidebook.View.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.Model.Calculation
import com.rk_softwares.lawguidebook.Presenter.CalculationItemPresenter
import com.rk_softwares.lawguidebook.Presenter.CalculationView
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBar

class Act_calculation : ComponentActivity(), InternetStatus, CalculationView {

    private lateinit var internetChecker: InternetChecker
    private lateinit var presenter: CalculationItemPresenter

    //init--------
    private var isInternet = mutableStateOf(false)
    private var serverStatus = mutableStateOf("")
    private var calculationList = mutableStateListOf<Calculation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            init()
            internetChecker.onStart()

            
            LaunchedEffect(isInternet.value) {

                if (isInternet.value){

                    presenter.calculationDataFromServer()

                }

            }

            LawGuideBookTheme {

                CalculationFullScreen(
                    backClick = { finish() },
                    internet = isInternet.value,
                    calculationList = calculationList,
                    serverStatus = serverStatus.value,
                    itemClick = {

                    }
                )

            }

        }
    }//on create==========================================

    private fun init(){

        internetChecker = InternetChecker(this, this)
        presenter = CalculationItemPresenter(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        internetChecker.onStop()
        presenter.onDestroy()
    }

    override fun isInternet(internet: Boolean) {
        isInternet.value = internet
    }

    override fun onCalculationList(list: List<Calculation>) {
        calculationList.clear()
        calculationList.addAll(list)
    }

    override fun serverStatus(status: String) {
        serverStatus.value = status
        ShortMessageHelper.toast(this, status)
        Log.d("toast", status)
    }

}//class===================================================


@Preview(showBackground = true)
@Composable
private fun CalculationFullScreen(
    backClick: () -> Unit = {},
    internet : Boolean = false,
    calculationList : List<Calculation> = emptyList(),
    serverStatus : String = "",
    itemClick: (String) -> Unit = {}
) {

    val lazyState = rememberLazyGridState()
    var isInternetDialogVisible by remember { mutableStateOf(false) }

    if (internet) isInternetDialogVisible = false else isInternetDialogVisible = true

    Scaffold(
        topBar = { Toolbar(backClick = {backClick()}) },
        modifier = Modifier.fillMaxSize())
    { innerPadding ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {

            Column(

                modifier = Modifier
                    .fillMaxSize()

            ) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(5.dp),
                    state = lazyState,
                    userScrollEnabled = if (serverStatus == "Pending") false else true,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    if (serverStatus == "Pending"){

                        items(25){

                            ComposeHelper.SkeletonLoading(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(7.dp),
                                shape = 14.dp,
                                innerPadding = 48.dp
                            )

                        }

                    }else{

                        items(
                            items = calculationList,
                            key = { it.title }
                        ){ it ->

                            Item(
                                image = it.image,
                                title = it.title,
                                itemClick = {itemClick(it.title)}
                            )

                        }

                    }

                }

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

        Box(

            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)

        ) {

            IconButton(
                onClick = { backClick() },
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterStart)
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


@Preview(showBackground = true)
@Composable
private fun Item(
    image : String = "",
    title : String = "Hello",
    itemClick : () -> Unit = {}
){

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = Color(0xFFFCD0D0), shape = RoundedCornerShape(14.dp))
                .clip(shape = RoundedCornerShape(14.dp))
                .clickable{ itemClick() }
                .background(color = Color(0xFFFFFFFF))
                .padding(7.dp)

        ) {

            AsyncImage( model = image,
                contentDescription = "",
                placeholder = painterResource(R.drawable.img_loading),
                error = painterResource(R.drawable.img_loading),
                modifier = Modifier
                    .width(50.dp)
                    .height(55.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = title,
                fontSize = 14.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF605252),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                )

        }//column

    }//box

}//fun end