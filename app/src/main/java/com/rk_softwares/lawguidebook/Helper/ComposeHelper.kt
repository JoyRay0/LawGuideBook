package com.rk_softwares.lawguidebook.Helper

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*

@Preview
@Composable
fun FullView(){

    Column(

        modifier = Modifier.fillMaxSize()
            .background(color = Color(0xFFFFFFFF))

    ) {

        ComposeHelper.InternetDialog()

        ComposeHelper.SkeletonLoading()

    }//column

}//fun end

object ComposeHelper {

    //@Preview(showBackground = true)
    @Composable
    fun InternetDialog(
        modifier: Modifier = Modifier,
    ){

        val context = LocalContext.current

        Box(

            modifier = modifier
                .fillMaxWidth()
                .padding(9.dp)

        ) {

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp))
                    .clip(shape = RoundedCornerShape(14.dp))
                    .background(color = Color(0xFFFFFFFF))
                    .padding(3.dp)

            ) {

                Text(text = "ইন্টারনেট সংযোগ চালু করে আবার চেষ্টা করুন।",
                    fontSize = 15.sp,
                    fontFamily = BanglaFont.font(),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF564D4D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(7.dp)
                        .align(Alignment.CenterHorizontally)
                )

            }//column

        }//box

    }//fun end

    @Composable
    fun SkeletonLoading(
        modifier: Modifier = Modifier,
        shape : Dp = 0.dp,
        innerPadding : Dp = 10.dp
    ){

        Box(

            modifier = modifier
                .fillMaxWidth()


        ){

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(shape))
                    .background(color = Color(0xFFFAE2E2))
                    .padding(innerPadding )
            )
            

        }


    }//fun end

}
