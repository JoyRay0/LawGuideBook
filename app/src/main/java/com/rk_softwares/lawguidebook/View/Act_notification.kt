package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.rk_softwares.lawguidebook.Database.NotificationDatabase
import com.rk_softwares.lawguidebook.Helper.*
import com.rk_softwares.lawguidebook.Model.*
import com.rk_softwares.lawguidebook.Presenter.*
import com.rk_softwares.lawguidebook.Presenter.Notification
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.*
import kotlinx.coroutines.delay

class Act_notification : ComponentActivity(), InternetStatus, Notification {

    //init
    private lateinit var internetChecker : InternetChecker

    private lateinit var presenter: NotificationPresenter

    private lateinit var notificationDatabase: NotificationDatabase


    private var isInternet = mutableStateOf(false)

    private var notificationList = mutableStateListOf<NotificationData>()

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        init()

        insert()

        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = true
            )

            presenter.getAllNotification()
            presenter.isNotificationSeen()

            LawGuideBookTheme {

                NotificationFullScreen(
                    backClick = {
                        IntentHelper.normalIntent(this, Act_home::class.java)
                        finishAffinity()
                                },
                    internet = isInternet.value,
                    notificationList = notificationList,
                    notificationTitleClick = {

                        presenter.updateNotificationStatus(it)

                    },
                    deleteAll = { presenter.deleteAllNotification() },
                    markAsRead = { presenter.updateAllNotificationStatus() },
                    deleteClick = { presenter.deleteNotification(it) }
                )

            }

            BackHandler {

                IntentHelper.normalIntent(this, Act_home::class.java)
                finishAffinity()

            }

        }
    }//on create==============================

    private fun init() {

        internetChecker = InternetChecker(this, this)
        notificationDatabase = NotificationDatabase(this)
        presenter = NotificationPresenter(this, notificationDatabase)

    }

    override fun onStart() {
        super.onStart()
        internetChecker.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        internetChecker.onStop()
        presenter.onDestroy()
    }

    override fun isInternet(internet: Boolean) {
        isInternet.value = internet
    }

    override fun notificationList(list: List<NotificationData>) {
        notificationList.clear()
        notificationList.addAll(list)
    }

    override fun message(status: String) {

        ShortMessageHelper.toast(this, status)

    }

    override fun hasUnseenNotification(isSeen: Boolean) {

    }

    fun insert(){

        //presenter.deleteAllNotification()

        presenter.insertNotification(
            data = NotificationData(
                id = "1",
                title = "নতুন নোটিফিকেশন1",
                description = "সম্ভবত github-readme-streak-stats.herokuapp.com বা stats image URL এর কারণে error আসতেছে।\n" +
                        "অনেক সময় GitHub README stats service down থাকে বা username ভুল থাকলে error দেখায়।",
                isSeen = false
            )
        )

        presenter.insertNotification(
            data = NotificationData(
                id = "2",
                title = "নতুন নোটিফিকেশন2",
                description = "সম্ভবত github-readme-streak-stats.herokuapp.com বা stats image URL এর কারণে error আসতেছে।\n" +
                        "অনেক সময় GitHub README stats service down থাকে বা username ভুল থাকলে error দেখায়।",
                isSeen = false
            )
        )

        presenter.insertNotification(
            data = NotificationData(
                id = "3",
                title = "নতুন নোটিফিকেশন3",
                description = "সম্ভবত github-readme-streak-stats.herokuapp.com বা stats image URL এর কারণে error আসতেছে।\n" +
                        "অনেক সময় GitHub README stats service down থাকে বা username ভুল থাকলে error দেখায়।",
                isSeen = false
            )
        )

        presenter.insertNotification(
            data = NotificationData(
                id = "4",
                title = "নতুন নোটিফিকেশন4",
                description = "সম্ভবত github-readme-streak-stats.herokuapp.com বা stats image URL এর কারণে error আসতেছে।\n" +
                        "অনেক সময় GitHub README stats service down থাকে বা username ভুল থাকলে error দেখায়।",
                isSeen = false
            )
        )

        presenter.insertNotification(
            data = NotificationData(
                id = "5",
                title = "নতুন নোটিফিকেশন5",
                description = "সম্ভবত github-readme-streak-stats.herokuapp.com বা stats image URL এর কারণে error আসতেছে।\n" +
                        "অনেক সময় GitHub README stats service down থাকে বা username ভুল থাকলে error দেখায়।",
                isSeen = false
            )
        )

        presenter.insertNotification(
            data = NotificationData(
                id = "6",
                title = "নতুন নোটিফিকেশন6",
                description = "সম্ভবত github-readme-streak-stats.herokuapp.com বা stats image URL এর কারণে error আসতেছে।\n" +
                        "অনেক সময় GitHub README stats service down থাকে বা username ভুল থাকলে error দেখায়।",
                isSeen = false
            )
        )


    }

}//class=======================================

@Preview(showBackground = true)
@Composable
private fun NotificationFullScreen(
    backClick: () -> Unit = {},
    internet: Boolean = false,
    notificationList : List<NotificationData> = emptyList(),
    notificationTitleClick : (String) -> Unit = {},
    deleteAll: () -> Unit = {},
    markAsRead: () -> Unit = {},
    deleteClick: (String) -> Unit = {}
) {

    var isInternetDialogVisible by remember { mutableStateOf(false) }
    var lazyState = rememberLazyListState()
    var isMenuVisible = remember { mutableStateOf(false) }

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
            backClick = { backClick() },
            menuClick = { isMenuVisible.value = true }
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

            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = null
                    ){
                        isMenuVisible.value = false

                    }

            ) {

                //========================================
                //Notification list
                //=========================================

                if (notificationList.isEmpty()){

                    Column(

                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)

                    ) {

                        Image( painter = painterResource(R.drawable.img_notification),
                            contentDescription = "Notification",
                            modifier = Modifier
                                .fillMaxWidth()
                                .size(90.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "কোন নোটিফিকেশন নেই।",
                            fontSize = 15.sp,
                            fontFamily = Bangla.banglaFont(),
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.CenterHorizontally)
                        )

                    }


                }else{

                    LazyColumn(
                        state = lazyState,
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {

                        items(
                            items = notificationList,
                            //key = { it.id }
                        ){it ->

                            Item(
                                title = it.title,
                                description = it.description,
                                titleClick = { notificationTitleClick(it.id) },
                                deleteClick = { deleteClick(it.id) },
                                isNewNotification = !it.isSeen
                            )

                        }

                    }

                }

                //Menu item

                if (isMenuVisible.value){

                    ToolbarMenu(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopEnd),
                        deleteAll = {

                            deleteAll()
                            isMenuVisible.value = false

                        },
                        markAsRead = {

                            markAsRead()
                            isMenuVisible.value = false

                        }
                    )

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

        }//box

    }//scaffold

}//fun end

@Preview(showBackground = true)
@Composable
private fun Toolbar(
    backClick : () -> Unit = {},
    menuClick : () -> Unit = {}
) {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .background(color = LightToolBar)

    ) {

        Row(

            modifier = Modifier
                .wrapContentWidth()
                .padding(3.dp)
                .align(Alignment.CenterStart)

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

        Row(

            modifier = Modifier
                .wrapContentWidth()
                .padding(3.dp)
                .align(Alignment.CenterEnd)

        ) {

            IconButton(
                onClick = { menuClick() },
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(shape = CircleShape)
                    .size(35.dp)
                    .align(Alignment.CenterVertically)
            ) {

                Icon( painter = painterResource(R.drawable.ic_vertical_three_dot),
                    contentDescription = "Back",
                    tint = LightToolBarIcon,
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(20.dp)

                )

            }

            Spacer(modifier = Modifier.width(3.dp))

        }//row

    }//box

}//fun end


@Preview(showBackground = true)
@Composable
private fun Item(
    title : String = "Title",
    description : String = "Description",
    titleClick : () -> Unit = {},
    deleteClick : () -> Unit = {},
    isNewNotification : Boolean = false
) {

    var isDescriptionVisible = remember { mutableStateOf(false) }
    var isDeleteIconVisible = remember { mutableStateOf(false) }

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
                .combinedClickable(
                    onClick = {

                        titleClick()
                        isDescriptionVisible.value = !isDescriptionVisible.value
                        isDeleteIconVisible.value = false

                    },
                    onLongClick = {

                        isDescriptionVisible.value = false
                        isDeleteIconVisible.value = true

                    }
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFFAC8C8),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(7.dp)
                .align(Alignment.CenterHorizontally)

        ) {

            Text(text = title,
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = if (isNewNotification) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Start,
                color = if (isNewNotification) Color(0xFF000000) else Color(0xFF504646),
                modifier = Modifier
                    .fillMaxWidth(0.93f)
                    .padding(3.dp)
                    .align(Alignment.CenterStart)
            )

            if (isDeleteIconVisible.value){

                IconButton(
                    onClick = {
                        deleteClick()
                        isDeleteIconVisible.value = false
                              },
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(shape = CircleShape)
                        //.background(color = Color(0xFFA48989))
                        .size(30.dp)
                        .align(Alignment.CenterEnd)
                ) {

                    Icon( painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Delete",
                        tint = Color(0xFF333030),
                        modifier = Modifier
                            .wrapContentWidth()
                            .size(20.dp)
                            .align(Alignment.Center)

                    )

                }

            }else{

                Icon( painter = painterResource(R.drawable.ic_right),
                    contentDescription = "Right",
                    tint = Color.Gray,
                    modifier = Modifier
                        .rotate(rotation)
                        .wrapContentWidth()
                        .align(Alignment.CenterEnd)

                )

            }

        }//box

        if (isDescriptionVisible.value){

            Spacer(modifier = Modifier.height(6.dp))

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)

            ) {

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFFFD7D7),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(6.dp)
                        .align(Alignment.Center)

                ) {

                    Text(text = description,
                        fontSize = 14.sp,
                        fontFamily = Bangla.banglaFont(),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Justify,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(3.dp)
                            .align(Alignment.Center)

                    )

                }//box

            }//box

        }

    }//column

}//fun end


@Preview(showBackground = true)
@Composable
private fun ToolbarMenu(
    modifier: Modifier = Modifier,
    deleteAll : () -> Unit = {},
    markAsRead : () -> Unit = {}
) {

    Box(

        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp),
        contentAlignment = Alignment.TopEnd

    ) {

        Column(

            modifier = Modifier
                .width(170.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = Color.White)
                .padding(5.dp)
                .align(Alignment.CenterEnd)

        ) {

            val text = arrayOf("সব ডিলিট", "সব পড়া হয়েছে")
            val icon = arrayOf(R.drawable.ic_delete, R.drawable.ic_ok)

            text.forEachIndexed { index, text ->

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .clickable{

                            if (index == 0) deleteAll() else markAsRead()

                        }
                        .padding(5.dp)

                ) {

                    Spacer(modifier = Modifier.width(3.dp))

                    Icon(painter = painterResource(icon[index]),
                        contentDescription = "",
                        tint = Color(0xFF524747),
                        modifier = Modifier
                            .wrapContentWidth()
                            .size(17.dp)
                            .align(Alignment.CenterVertically)

                    )

                    Spacer(modifier = Modifier.width(9.dp))

                    Text( text = text,
                        fontSize = 14.sp,
                        fontFamily = Bangla.banglaFont(),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(3.dp)
                            .align(Alignment.CenterVertically)

                    )

                    Spacer(modifier = Modifier.width(3.dp))

                }//row

            }//loop

        }//column

    }//box

}//fun end

