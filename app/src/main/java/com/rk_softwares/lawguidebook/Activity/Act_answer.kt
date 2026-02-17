package com.rk_softwares.lawguidebook.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.colintheshots.twain.MarkdownText
import com.rk_softwares.lawguidebook.Activity.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.Activity.theme_main.LightNav
import com.rk_softwares.lawguidebook.Activity.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.Activity.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.Helper.BanglaFont
import com.rk_softwares.lawguidebook.Helper.InternetChecker
import com.rk_softwares.lawguidebook.Helper.KeyHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.R

class Act_answer : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            
            var toolBarText by remember { mutableStateOf("") }

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            toolBarText = intent.getStringExtra(KeyHelper.sendQuestion_IntentKey()) ?: ""

            val internet = InternetChecker.liveInternetStatus(this)

            Toast.makeText(this,if (internet) "On" else "Off", Toast.LENGTH_SHORT).show()


            LawGuideBookTheme {
                AnswerFullScreen(
                    backClick = {
                        finish()
                        toolBarText = ""
                    },
                    toolbarTitle = toolBarText
                )

            }

            BackHandler {

                finish()
                toolBarText = ""

            }

        }
    }//on create==============================================
}//class======================================================


@Preview(showBackground = true)
@Composable
private fun AnswerFullScreen(
    backClick: () -> Unit = {},
    toolbarTitle: String = ""

) {

    val text = """
        
         শব্দ গুলো দেখে সকলেই অবাক হয়েছে, এটা বলা বাহুল্য। 
         অনেকেই ভাবছেন, এ সব শব্দের বেশিরভাগটাই নিজে থেকে বানানো। 
         তবে **[সত্যটা](https://www.google.com)** হলো, এর একটাও বানানো নয়। সব গুলোই বাংলা ভাষার অংশ। 
         উৎপত্তি অনুসারে বাংলাভাষার যে শ্রেনীবিভাগ রয়েছে, সেই **পাঁচটি** শ্রেনী বিভাগের ফলেই জন্ম নিয়েছে এই সকল শব্দ, যার অনেকগুলোই আপনি আপনার এই পর্যন্ত জীবনে একবারও শোনেননি।
                    
         [Google](https://www.google.com)
                    
         | Key | First Name | Last Name |
         |---|------|---------|
         | 1 | Joy | Ray |
         | 2 | Arjun | Ray |
        
    """.trimIndent()

    var textZoom by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {Toolbar(
            backClick = { backClick()},
            toolbarTitle = toolbarTitle
        )},
        modifier = Modifier.fillMaxSize()) { innerPadding ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.TopStart)

            ) {

                MarkdownText(
                    text = text,
                    zoomCount = textZoom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(80.dp))

            }//column

            ZoomInOut(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.BottomEnd),
                textZoom = textZoom,
                zoomInClick = {

                    if (textZoom > -1 && textZoom < 22){

                        textZoom++

                    }

                },
                zoomOutClick = {

                    if (textZoom > -1 && textZoom <= 22){

                        textZoom--

                    }

                }
            )


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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
                )

        }//row

    }//box
    
}//fun end


@Preview(showBackground = true)
@Composable
private fun ZoomInOut(
    modifier: Modifier = Modifier,
    textZoom : Int = 0,
    zoomInClick : () -> Unit = {},
    zoomOutClick : () -> Unit = {},
) {

    Box(

        modifier = modifier
            .wrapContentWidth()
            .padding(15.dp)

    ) {

        Row(

            modifier = Modifier
                .wrapContentWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(50.dp))
                .clip(shape = RoundedCornerShape(50.dp))
                .background(color = Color(0xFF4CAF50))
                .padding(2.dp)
                .align(Alignment.Center)

        ) {

            IconButton(
                onClick = { zoomOutClick() },
                enabled = textZoom > 0,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically)
                    .alpha(if (textZoom > 0) 1f else 0.5f)

            ) {

                Icon( painter = painterResource(R.drawable.ic_zoom_out),
                    contentDescription = "zoom out",
                    tint = Color(0xFFFFFFFF),
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(22.dp)
                        .align(Alignment.CenterVertically)

                )

            }

            VerticalDivider(
                thickness = 1.dp,
                color = Color(0xFFFFFFFF),
                modifier = Modifier
                    .height(30.dp)
                    .align(Alignment.CenterVertically)

            )

            IconButton(
                onClick = { zoomInClick() },
                enabled = textZoom < 22,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically)
                    .alpha(if (textZoom == 22) 0.5f else 1f)

            ) {

                Icon( painter = painterResource(R.drawable.ic_zoom_in),
                    contentDescription = "zoom out",
                    tint = Color(0xFFFFFFFF),
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(22.dp)

                )

            }

        }//row

    }//box

}//fun end


@Composable
private fun MarkdownText(
    text : String = "",
    zoomCount : Int = 0,
    modifier: Modifier = Modifier
) {

    when(zoomCount){

        0 -> MarkdownText(
            markdown = text,
            fontSize = 18.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        1 -> MarkdownText(
            markdown = text,
            fontSize = 19.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        2 -> MarkdownText(
            markdown = text,
            fontSize = 20.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        3 -> MarkdownText(
            markdown = text,
            fontSize = 21.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        4 -> MarkdownText(
            markdown = text,
            fontSize = 22.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        5 -> MarkdownText(
            markdown = text,
            fontSize = 23.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        6 -> MarkdownText(
            markdown = text,
            fontSize = 24.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        7 -> MarkdownText(
            markdown = text,
            fontSize = 25.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        8 -> MarkdownText(
            markdown = text,
            fontSize = 26.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        9 -> MarkdownText(
            markdown = text,
            fontSize = 27.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        10 -> MarkdownText(
            markdown = text,
            fontSize = 28.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        11 -> MarkdownText(
            markdown = text,
            fontSize = 29.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        12 -> MarkdownText(
            markdown = text,
            fontSize = 30.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        13 -> MarkdownText(
            markdown = text,
            fontSize = 31.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        14 -> MarkdownText(
            markdown = text,
            fontSize = 32.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        15 -> MarkdownText(
            markdown = text,
            fontSize = 33.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        16 -> MarkdownText(
            markdown = text,
            fontSize = 34.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        17 -> MarkdownText(
            markdown = text,
            fontSize = 35.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        18 -> MarkdownText(
            markdown = text,
            fontSize = 36.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        19 -> MarkdownText(
            markdown = text,
            fontSize = 37.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        20 -> MarkdownText(
            markdown = text,
            fontSize = 38.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        21 -> MarkdownText(
            markdown = text,
            fontSize = 39.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

        22 -> MarkdownText(
            markdown = text,
            fontSize = 40.sp,
            fontResource = R.font.noto_serif_bengali,
            modifier = modifier

        )

    }

}//fun end
