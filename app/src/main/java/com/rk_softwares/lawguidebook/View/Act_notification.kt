package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.rk_softwares.lawguidebook.Helper.*
import com.rk_softwares.lawguidebook.Model.*
import com.rk_softwares.lawguidebook.Presenter.*
import com.rk_softwares.lawguidebook.Presenter.Home
import com.rk_softwares.lawguidebook.Presenter.Notification
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.*
import kotlinx.coroutines.delay

class Act_notification : ComponentActivity(), InternetStatus, Notification {

    //init
    private lateinit var internetChecker : InternetChecker

    private lateinit var presenter: NotificationPresenter


    private var isInternet = mutableStateOf(false)

    private var notificationList = mutableStateListOf<NotificationData>()

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        init()

        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = true
            )

            LawGuideBookTheme {

                NotificationFullScreen(
                    backClick = { finish() },
                    internet = isInternet.value,
                    notificationList = notificationList
                )

            }
        }
    }//on create==============================

    private fun init() {

        internetChecker = InternetChecker(this, this)
        presenter = NotificationPresenter(this)

    }

    override fun onStart() {
        super.onStart()
        internetChecker.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        internetChecker.onStop()
    }

    override fun isInternet(internet: Boolean) {
        isInternet.value = internet
    }

    override fun notificationList(list: List<NotificationData>) {
        notificationList.clear()
        notificationList.addAll(list)
    }

    override fun notificationStatus(status: String) {

        ShortMessageHelper.toast(this, status)

    }

}//class=======================================

@Preview(showBackground = true)
@Composable
private fun NotificationFullScreen(
    backClick: () -> Unit = {},
    internet: Boolean = false,
    notificationList : List<NotificationData> = emptyList(),
    notificationTitleClick : (String) -> Unit = {}
) {

    var isInternetDialogVisible by remember { mutableStateOf(false) }
    var lazyState = rememberLazyListState()

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

            //========================================
            //Notification list
            //=========================================

            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxWidth()

            ) {

                items(
                    items = notificationList,
                    key = { it.id }
                ){it ->

                    Item(
                        title = it.title,
                        description = it.description,
                        titleClick = { notificationTitleClick(it.id) }
                    )

                }

            }

            //internet dialog

            if (isInternetDialogVisible){

                ComposeHelper.InternetDialog(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
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
                    .clip(shape = CircleShape)
                    .size(35.dp)
                    .align(Alignment.CenterVertically)
            ) {

                Icon( painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = LightToolBarIcon,
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(22.dp)

                )

            }

        }//row

    }//box

}//fun end


@Preview(showBackground = true)
@Composable
private fun Item(
    title : String = "Title",
    description : String = "Description",
    titleClick : () -> Unit = {},
) {

    var isDescriptionVisible = remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isDescriptionVisible.value) 270f else 90f,
        label = ""
    )

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)

    ) {

        Box(

            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(12.dp))
                .clickable {
                    titleClick()
                    isDescriptionVisible.value = !isDescriptionVisible.value
                }

                //.background(color = Color(0xFFFFFFFF))
                .border(width = 1.dp, color = Color(0xFFFAC8C8), shape = RoundedCornerShape(12.dp))
                .padding(7.dp)
                .align(Alignment.CenterHorizontally)

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
                    .rotate(rotation)
                    .wrapContentWidth()
                    .align(Alignment.CenterEnd)

            )

        }//box

        if (isDescriptionVisible.value){


            Spacer(modifier = Modifier.height(10.dp))

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(10.dp))
                    .border(width = 1.dp, color = Color(0xFFFFD7D7),shape = RoundedCornerShape(10.dp))
                    .padding(5.dp)
                    .align(Alignment.CenterHorizontally)

            ) {

                Text(text = description,
                    fontSize = 15.sp,
                    fontFamily = Bangla.banglaFont(),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)

                )

            }//box

        }

    }//column

}//fun end

