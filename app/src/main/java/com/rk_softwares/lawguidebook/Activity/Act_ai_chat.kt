package com.rk_softwares.lawguidebook.Activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rk_softwares.lawguidebook.Activity.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.Activity.theme_main.LightNav
import com.rk_softwares.lawguidebook.Activity.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.Activity.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.Helper.BanglaFont
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.R

class Act_ai_chat : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            LawGuideBookTheme {

                ChatFullScreen(
                    backClick = {finish()}
                )

            }

            BackHandler{

                finish()

            }

        }
    }//on create=============================
}//class=====================================


@Preview(showBackground = true)
@Composable
private fun ChatFullScreen(
    backClick: () -> Unit = {}
) {

    Scaffold(
        topBar = { Toolbar(
            backClick = { backClick() }
        ) },
        bottomBar = { ChatNav() },
        modifier = Modifier.fillMaxSize())
    { innerPadding ->

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
private fun ChatNav() {

    var inputMessage by remember { mutableStateOf("") }

    Box(

        modifier = Modifier
            .fillMaxWidth()

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 7.dp, shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .clip(shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .background(color = Color(0xFFFFFFFF))
                .padding(7.dp)

        ) {

            Box(

                modifier = Modifier
                    .fillMaxWidth(0.89f)
                    .align(Alignment.CenterVertically)
                    .imePadding()

            ) {

                if (inputMessage.isEmpty()){

                    Text(text = "আপনার আইনি সমস্যাটি লিখুন....",
                        fontSize = 15.sp,
                        fontFamily = BanglaFont.font(),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF836464),
                        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(start = 10.dp)
                            .align(Alignment.CenterStart)
                    )

                }

                BasicTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    textStyle = TextStyle(color = Color(0xFF000000), fontSize = 15.sp, fontFamily = BanglaFont.font(), fontWeight = FontWeight.Normal),
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(15.dp))
                        .border(width = 1.dp, color = Color(0xFF000000), shape = RoundedCornerShape(15.dp))
                        .padding(10.dp)
                        .align(Alignment.CenterStart)
                )

            }//box

            Spacer(modifier = Modifier.width(5.dp))

            IconButton(
                onClick = {},
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(shape = CircleShape)
                    //.background(color = Color(0xFF00BCD4))
                    .size(35.dp)
                    .align(Alignment.CenterVertically)
            ) {

                Icon( painter = painterResource(R.drawable.ic_send),
                    contentDescription = "Send",
                    tint = Color(0xFFD849F1),
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically)

                )

            }

        }//row

    }//box

}//fun end

@Preview(showBackground = true)
@Composable
private fun ChatBubble(
    message : String = "Hello",
    isUser : Boolean = true,
    deleteMessage : () -> Unit = {}
) {

    Box(

        modifier = Modifier
            .fillMaxWidth()

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start

        ) {

            Text(text = message,
                fontSize = 15.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Normal,
                color = if (isUser) Color(0xFFFFFFFF) else Color(0xFF000000),
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(
                        shape =
                            if (isUser) {

                                RoundedCornerShape(
                                    topStart = 17.dp,
                                    topEnd = 5.dp,
                                    bottomStart = 17.dp,
                                    bottomEnd = 17.dp
                                )

                            } else {

                                RoundedCornerShape(
                                    topStart = 5.dp,
                                    topEnd = 17.dp,
                                    bottomStart = 17.dp,
                                    bottomEnd = 17.dp
                                )

                            }

                    )
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { deleteMessage() }
                    )
                    .background(color = if (isUser) Color(0xFFD849F1) else Color(0xFFF3C9C9))
                    .padding(10.dp)
                )



        }//row

    }//box
    
}//fun end

