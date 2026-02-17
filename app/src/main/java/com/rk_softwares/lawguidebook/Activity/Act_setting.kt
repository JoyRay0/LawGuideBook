package com.rk_softwares.lawguidebook.Activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.rk_softwares.lawguidebook.Activity.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.Activity.theme_main.LightNav
import com.rk_softwares.lawguidebook.Activity.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.Activity.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.Database.HistoryDatabase
import com.rk_softwares.lawguidebook.Helper.BanglaFont
import com.rk_softwares.lawguidebook.Helper.IntentHelper
import com.rk_softwares.lawguidebook.Helper.InternetChecker
import com.rk_softwares.lawguidebook.Helper.KeyHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Act_setting : ComponentActivity() {

    private val appPackageName = "com.rk_softwares.lawguidebook"

    private lateinit var historyDatabase: HistoryDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            init()

            var isSearchHistoryDeleted by remember { mutableStateOf(false) }

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            if (isSearchHistoryDeleted){

                LaunchedEffect(Unit) {

                    withContext(Dispatchers.IO){

                        historyDatabase.deleteAll()

                    }

                }

            }

            val internet = InternetChecker.liveInternetStatus(this)

            Toast.makeText(this,if (internet) "On" else "Off", Toast.LENGTH_SHORT).show()

            LawGuideBookTheme {

                SettingFullScreen(

                    backClick = {

                        startActivity(Intent(this, Act_home::class.java))
                        finishAffinity()

                    },
                    otherAppClick = {

                        IntentHelper.dataIntent(this, Act_webview::class.java, KeyHelper.otherApp_privacy_IntentKey(), "other_apps")

                    },
                    privacyClick = {

                        IntentHelper.dataIntent(this, Act_webview::class.java, KeyHelper.otherApp_privacy_IntentKey(), "privacy&policy")

                    },
                    feedbackClick = {

                        val intent = Intent(Intent.ACTION_SENDTO)
                        val uriText = "mailto:" + Uri.encode(" r.k.softwares17@gmail.com")
                        val uri = uriText.toUri()
                        intent.setData(uri)
                        startActivity(Intent.createChooser(intent, " "))

                    },
                    appShareClick = {

                        val intent = Intent(Intent.ACTION_SEND)
                        intent.setType("text/plain")
                        val body = "Download this App"
                        val sub = "https://play.google.com/store/apps/details?id=$appPackageName"
                        intent.putExtra(Intent.EXTRA_TEXT, body)
                        intent.putExtra(Intent.EXTRA_TEXT, sub)
                        startActivity(Intent.createChooser(intent, null))

                    },
                    reviewClick = {

                        try {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$appPackageName".toUri()
                                )
                            )

                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW, "market://details?id=$appPackageName".toUri()
                                )
                            )

                        }

                    },
                    deleteSearchHistory = {

                        isSearchHistoryDeleted = true

                        Toast.makeText(this,"ডিলিট হয়েছে", Toast.LENGTH_SHORT).show()

                    }

                )
            }

            BackHandler {

                startActivity(Intent(this, Act_home::class.java))
                finishAffinity()

            }
        }
    }//on create==============================

    private fun init(){

        historyDatabase = HistoryDatabase(this)

    }

}//class=========================================

@Preview(showBackground = true)
@Composable
private fun SettingFullScreen(
    backClick: () -> Unit = {},
    featureClick : () -> Unit = {},
    otherAppClick : () -> Unit = {},
    privacyClick : () -> Unit = {},
    feedbackClick : () -> Unit = {},
    appShareClick : () -> Unit = {},
    reviewClick : () -> Unit = {},
    deleteSearchHistory : () -> Unit = {}
) {

    var isInfoDialogVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { Toolbar( backClick = {backClick()}) },
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
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.TopCenter)

            ) {

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)

                ) {

                    TextButtonHelper(
                        text = "নতুন ফিচার",
                        textClick = { featureClick() },
                        topStartCorner = 17,
                        topEndCorner = 17,
                        startIcon = R.drawable.ic_feature,
                        startIconSize = 28,
                        spaceWidth = 15
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    TextButtonHelper(
                        text = "অনান্য অ্যাপ",
                        textClick = { otherAppClick() },
                        bottomStartCorner = 17,
                        bottomEndCorner = 17,
                        startIcon = R.drawable.ic_other_app,
                        startIconSize = 20,
                        spaceWidth = 23
                    )


                }//column

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)

                ) {

                    TextButtonHelper(
                        text = "মতামত",
                        textClick = { feedbackClick() },
                        topStartCorner = 17,
                        topEndCorner = 17,
                        startIcon = R.drawable.ic_feedback,
                        startIconSize = 20,
                        spaceWidth = 22
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    TextButtonHelper(
                        text = "অ্যাপ শেয়ার",
                        textClick = { appShareClick() },
                        startIcon = R.drawable.ic_share,
                        startIconSize = 20,
                        spaceWidth = 22
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    TextButtonHelper(
                        text = "অ্যাপ রিভিউ ",
                        textClick = { reviewClick() },
                        bottomStartCorner = 17,
                        bottomEndCorner = 17,
                        startIcon = R.drawable.ic_review,
                        startIconSize = 22,
                        spaceWidth = 22
                    )

                }//column

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)

                ) {

                    TextButtonHelper(
                        text = "প্রাইভেসি পলিসি",
                        textClick = { privacyClick() },
                        topStartCorner = 17,
                        topEndCorner = 17,
                        startIcon = R.drawable.ic_privacy,
                        startIconSize = 24,
                        spaceWidth = 22
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    TextButtonHelper(
                        text = "কিছু তথ্য",
                        textClick = { isInfoDialogVisible = true },
                        bottomStartCorner = 17,
                        bottomEndCorner = 17,
                        startIcon = R.drawable.ic_info,
                        startIconSize = 20,
                        spaceWidth = 25
                    )

                }//column

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)

                ) {

                    TextButtonHelper(
                        text = "ডিলিট সার্চ হিস্ট্রি",
                        textClick = { deleteSearchHistory() },
                        topStartCorner = 15,
                        topEndCorner = 15,
                        bottomStartCorner = 15,
                        bottomEndCorner = 15,
                        startIcon = R.drawable.ic_delete,
                        startIconSize = 25,
                        spaceWidth = 15
                    )


                }//column

            }//column

            if (isInfoDialogVisible){

                InfoDialog(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    okClick = { isInfoDialogVisible = false }
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


@Preview(showBackground = true)
@Composable
private fun TextButtonHelper(
    text : String = "",
    textClick : () -> Unit = {},
    topStartCorner : Int = 5,
    topEndCorner : Int = 5,
    bottomStartCorner : Int = 5,
    bottomEndCorner : Int = 5,
    startIcon : Int  = 0,
    startIconSize : Int = 0,
    spaceWidth : Int = 10
) {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            //.padding(top = 7.dp, bottom = 2.dp, start = 7.dp, end = 7.dp)

    ) {

        Box(

            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(
                        topStart = topStartCorner.dp,
                        topEnd = topEndCorner.dp,
                        bottomStart = bottomStartCorner.dp,
                        bottomEnd = bottomEndCorner.dp
                    )
                )
                .clip(
                    shape = RoundedCornerShape(
                        topStart = topStartCorner.dp,
                        topEnd = topEndCorner.dp,
                        bottomStart = bottomStartCorner.dp,
                        bottomEnd = bottomEndCorner.dp
                    )
                )
                .clickable { textClick() }
                .background(color = Color(0xFFFAF5F5))
                .padding(7.dp)

        ) {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .align(Alignment.CenterStart)

            ) {

                if (startIcon > 0){

                    Icon( painter = painterResource(startIcon),
                        contentDescription = "Icon",
                        tint = Color(0xFF000000),
                        modifier = Modifier
                            .wrapContentWidth()
                            .size(if (startIconSize > 0) startIconSize.dp else 20.dp)
                            .align(Alignment.CenterVertically)

                    )

                }

                Spacer(modifier = Modifier.width(spaceWidth.dp))

                Text(text = text,
                    fontSize = 16.sp,
                    fontFamily = BanglaFont.font(),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF000000),
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically)

                    )


            }//row

            Icon( painter = painterResource(R.drawable.ic_right),
                contentDescription = "Icon",
                tint = Color.Gray,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterEnd)

            )

        }//box

    }//box

}//fun end


@Preview(showBackground = true)
@Composable
private fun InfoDialog(
    modifier: Modifier = Modifier,
    okClick : () -> Unit = {}
) {

    Box(

        modifier = modifier
            .wrapContentWidth()
            .padding(7.dp)

    ) {

        Column(

            modifier = Modifier
                .wrapContentWidth()
                .shadow(elevation = 5.dp, shape = RoundedCornerShape(15.dp))
                .clip(shape = RoundedCornerShape(15.dp))
                .background(color = Color(0xFFFFFFFF))
                .padding(13.dp)
                .align(Alignment.Center)

        ) {

            Text(text = "ধন্যবাদ আমাদের অ্যাপ ব্যবহার করার জন্য",
                fontSize = 18.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally)
                )

            Spacer(modifier = Modifier.height(15.dp))

            Text(text = "আমরা অ্যাপ এর মধ্যে নিয়মিত আপডেট দিই এবং প্রতিটি আপডেটে নতুন নতুন ফিচার যুক্ত করি। আমাদের অ্যাপ ব্যবহার করতে যদি কোনো প্রকার সমস্যা হয় তবে দয়া করে আমাদের জানান। আমরা আপনার সমস্যা সমাধানের যথাসাধ্য চেষ্টা করবো।",
                fontSize = 15.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(17.dp))

            Text("ঠিক আছে",
                fontSize = 13.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(shape = RoundedCornerShape(12.dp))
                    .clickable{ okClick() }
                    .align(Alignment.End)
                    .padding(10.dp)
                )

        }//column

    }//box

}//fun end