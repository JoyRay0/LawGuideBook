package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.rk_softwares.lawguidebook.Helper.*
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.*


class Act_calculation : ComponentActivity(), InternetStatus {


    //init----
    private var toolbarText = mutableStateOf("")
    private var isInternet = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            
            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            if (savedInstanceState == null){

                toolbarText.value = intent.getStringExtra(KeyHelper.calculationTitle_IntentKey()) ?: ""

            }


            LawGuideBookTheme {

                CalculationFullScreen(
                    backClick = {
                        finish()
                        toolbarText.value = ""
                    },
                    toolbarTitle = toolbarText.value,
                    internet = isInternet.value
                )

            }

            BackHandler {

                toolbarText.value = ""
                finish()

            }
        }
    }//on create===============================================

    override fun isInternet(internet: Boolean) {
       isInternet.value = internet
    }
}//class=======================================================


@Preview(showBackground = true)
@Composable
private fun CalculationFullScreen(
    backClick: () -> Unit = {},
    toolbarTitle: String = "",
    internet: Boolean = false
){

    var isInternetDialogVisible by remember { mutableStateOf(false) }

    if (internet) isInternetDialogVisible = false else isInternetDialogVisible = true


    Scaffold(
        topBar = { Toolbar(
            backClick = { backClick() },
            toolbarTitle = toolbarTitle
        ) },

        modifier = Modifier.fillMaxSize())

    { innerPadding ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {

            TextToFunction(toolbarTitle)

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
    toolbarTitle : String = "",
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

            Spacer(modifier = Modifier.width(5.dp))

            Text(toolbarTitle,
                fontSize = 16.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically)
            )

        }//row

    }//box

}//fun end


@Composable
private fun TextToFunction(title : String){

    when(title){

        "জমি" -> {}

    }

}//fun end