package com.rk_softwares.lawguidebook.Helper

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

    }//column

}//fun end

object ComposeHelper {

    //@Preview(showBackground = true)
    @Composable
    fun InternetDialog(
        modifier: Modifier = Modifier,
        closeClick : () -> Unit = {},
        openClick : () -> Unit = {}
    ){

        val context = LocalContext.current

        Box(

            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)

        ) {

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 7.dp, shape = RoundedCornerShape(14.dp))
                    .clip(shape = RoundedCornerShape(14.dp))
                    .background(color = Color(0xFFFFFFFF))
                    .padding(7.dp)

            ) {

                Text(text = "ইন্টারনেট সংযোগ নেই!",
                    fontSize = 20.sp,
                    fontFamily = BanglaFont.font(),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF332D2D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally)
                    )

                Text(text = "ইন্টারনেট সংযোগ চালু করে আবার চেষ্টা করুন।",
                    fontSize = 15.sp,
                    fontFamily = BanglaFont.font(),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7A6F6F),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(7.dp)

                ) {

                    Text(text = "বন্ধ করুন",
                        fontSize = 14.sp,
                        fontFamily = BanglaFont.font(),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(shape = RoundedCornerShape(12.dp))
                            .clickable{
                                closeClick()
                            }
                            .background(color = Color(0xFFF1EFEF))
                            .padding(10.dp)
                            .align(Alignment.CenterVertically)
                        )

                    Spacer(modifier = Modifier.width(24.dp))

                    Text(text = "চালু করুন",
                        fontSize = 14.sp,
                        fontFamily = BanglaFont.font(),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(shape = RoundedCornerShape(12.dp))
                            .clickable{

                                openClick()

                                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                                context.startActivity(intent)

                            }
                            .background(color = Color(0xFF2DB732))
                            .padding(10.dp)
                            .align(Alignment.CenterVertically)
                    )

                }//row

            }//column

        }//box

    }//fun end

}
