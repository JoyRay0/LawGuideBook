package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.razzaghi.compose_loading_dots.core.rememberDotsLoadingController
import com.rk_softwares.lawguidebook.View.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.View.theme_main.LightNav
import com.rk_softwares.lawguidebook.View.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.Database.ChatDatabase
import com.rk_softwares.lawguidebook.Helper.BanglaFont
import com.rk_softwares.lawguidebook.Helper.CacheHelper
import com.rk_softwares.lawguidebook.Helper.ComposeHelper
import com.rk_softwares.lawguidebook.Helper.InternetChecker
import com.rk_softwares.lawguidebook.Helper.InternetStatus
import com.rk_softwares.lawguidebook.Helper.ShortMessageHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.Model.ChatMessage
import com.rk_softwares.lawguidebook.Presenter.ChatPresenter
import com.rk_softwares.lawguidebook.Presenter.ChatView
import com.rk_softwares.lawguidebook.R

class Act_ai_chat : ComponentActivity(), ChatView, InternetStatus{

    private lateinit var chatDatabase: ChatDatabase
    private lateinit var cacheHelper: CacheHelper
    private lateinit var chatPresenter: ChatPresenter

    private lateinit var internetChecker: InternetChecker

    //init---------------------------

    private val chatList = mutableStateListOf<ChatMessage>()
    private var messageStatus = mutableStateOf("")
    private var cacheStatus = mutableStateOf("")
    private var isInternet = mutableStateOf(false)


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            init()

            internetChecker.onStart()

            var messageId by remember { mutableIntStateOf(0) }
            var userMessage by remember { mutableStateOf("") }
            val clipBoardManager = LocalClipboardManager.current

            chatPresenter.getCache("alert_message")
            chatPresenter.getMessages()



            LawGuideBookTheme {

                ChatFullScreen(
                    backClick = {
                        userMessage = ""
                        finish()
                                },

                    chatList = chatList,

                    message = { if (it.isNotEmpty()) userMessage = it },

                    messageId = { messageId = it },

                    deleteClick = { chatPresenter.deleteMessage(messageId) },

                    deleteAllMessage = { chatPresenter.deleteAllMessages()
                                       },
                    sendClick = {

                        chatPresenter.userSendMessage(message = userMessage)

                    },
                    userCheckedMessage = if (cacheStatus.value == "showed") true else false ,
                    alertCloseClick = { chatPresenter.setCache("alert_message", "showed") },

                    copyClick = {

                        if (messageId > 0){

                            val chatMessage = chatList.find { it.id == messageId }

                            clipBoardManager.setText(AnnotatedString(chatMessage?.message ?: ""))

                            ShortMessageHelper.toast(this, "কপি হয়েছে")

                        }

                    },
                    resultAvailableStatus = messageStatus.value,
                    internet = isInternet.value

                )

            }

            LaunchedEffect(isInternet.value) {


            }

            BackHandler{

                userMessage = ""
                finish()

            }

        }
    }//on create=============================

    private fun init(){

        chatDatabase = ChatDatabase(this)
        cacheHelper = CacheHelper(this, "ai_chat")
        chatPresenter = ChatPresenter(this, chatDatabase, cacheHelper)
        internetChecker = InternetChecker(this, this)

    }

    override fun onDestroy() {
        super.onDestroy()
        chatPresenter.onDestroy()
        internetChecker.onStop()
    }

    override fun messages(messages: List<ChatMessage>) {

        chatList.clear()
        chatList.addAll(messages)

    }

    override fun messageStatus(status: String) {
        messageStatus.value = status

    }

    override fun deleteStatus(isDeleted: Boolean, message: String) {

        if (isDeleted) ShortMessageHelper.toast(this, message)

    }

    override fun cacheStatus(status: String) {
        cacheStatus.value = status
    }

    override fun isInternet(internet: Boolean) {
        isInternet.value = internet
    }

}//class=====================================


@Preview(showBackground = true)
@Composable
private fun ChatFullScreen(
    backClick: () -> Unit = {},
    chatList: List<ChatMessage> = emptyList(),
    message: (String) -> Unit = {},
    messageId: (Int) -> Unit = {},
    deleteClick: () -> Unit = {},
    deleteAllMessage: () -> Unit = {},
    sendClick: () -> Unit = {},
    userCheckedMessage : Boolean = true,
    alertCloseClick: () -> Unit = {},
    copyClick: () -> Unit = {},
    resultAvailableStatus : String = "",
    internet : Boolean = false
) {

    val lazyState = rememberLazyListState()
    var isDeleteDialogVisible by remember { mutableStateOf(false) }
    var isPopUpMenuVisible by remember { mutableStateOf(false) }
    var isInternetDialogVisible by remember { mutableStateOf(false) }

    if (internet) isInternetDialogVisible = false else isInternetDialogVisible = true

    Scaffold(
        topBar = { Toolbar(
            backClick = { backClick() },
            moreClick = { isPopUpMenuVisible = !isPopUpMenuVisible }
        ) },

        bottomBar = { ChatNav(
            message = { message(it) },
            sendClick = { sendClick() }
        ) },

        modifier = Modifier.fillMaxSize())

    { innerPadding ->

        LaunchedEffect(chatList.size) {

            if (chatList.isNotEmpty()) lazyState.animateScrollToItem(0)

        }

        val reverseList = chatList.reversed()

        Box(
            
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            
        ) {

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = null
                    ) { isPopUpMenuVisible = false }

            ) {

                LazyColumn(

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    state = lazyState,
                    reverseLayout = true

                ) {

                    items(
                        items = reverseList,
                        key = { it.id }
                    ){ item ->

                        ChatBubble(
                            message = item.message,
                            isUser = item.isUser,
                            deleteMessageLongClick = {
                                messageId(item.id)
                                isDeleteDialogVisible = true
                            },
                            resultAvailableStatus = resultAvailableStatus
                        )

                    }

                }//lazy column

            }//column

            if (chatList.isEmpty()){

                EmptyChatMessage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )

            }

            if (isDeleteDialogVisible){     //delete single message

                DeleteDialog(

                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    deleteClick = {
                        isDeleteDialogVisible = false
                        deleteClick()
                                  },
                    closeClick = { isDeleteDialogVisible = false },
                    copyClick = {
                        isDeleteDialogVisible = false
                        copyClick()
                    }

                )

            }

            if (isPopUpMenuVisible){

                PopUpMenu(

                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopEnd),
                    deleteAllMessage = {
                        deleteAllMessage()
                        isPopUpMenuVisible = false

                    }

                )

            }

            if (!userCheckedMessage){

                AlertDialog(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    alertCloseClick = { alertCloseClick() }
                )

            }

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
    moreClick : () -> Unit = {}
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
                        .align(Alignment.Center)

                )

            }
            
            IconButton(
                onClick = { moreClick() },
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterEnd)
            ) {

                Icon( painter = painterResource(R.drawable.ic_vertical_three_dot),
                    contentDescription = "Back",
                    tint = Color(0xFFFFFFFF),
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(20.dp)
                        .align(Alignment.Center)

                )

            }


            Row(

                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.Center)

            ) {

                Image( painter = painterResource(R.drawable.img_bot),
                    contentDescription = "AI",
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(22.dp)
                        //.align(Alignment.CenterVertically)

                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(text = "আইনি অ্যাসিস্ট্যান্ট",
                    fontSize = 17.sp,
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

    }//box

}//fun end


@Preview(showBackground = true)
@Composable
private fun ChatNav(
    message : (String) -> Unit = {},
    sendClick : () -> Unit = {}
) {

    var inputMessage by remember { mutableStateOf("") }

    Box(

        modifier = Modifier
            .fillMaxWidth()

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                //.shadow(elevation = 10.dp, shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .clip(shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .border(
                    width = 1.dp,
                    color = Color(0xFFF3DFDF),
                    shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                )
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
                        .border(
                            width = 1.dp,
                            color = Color(0xFF938181),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(10.dp)
                        .align(Alignment.CenterStart)
                )

            }//box

            Spacer(modifier = Modifier.width(5.dp))

            IconButton(
                onClick = {
                    if (inputMessage.isNotEmpty()) message(inputMessage)
                    inputMessage = ""
                    sendClick()
                          },
                enabled = if (inputMessage.isEmpty()) false else true,
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(shape = CircleShape)
                    //.background(color = Color(0xFF00BCD4))
                    .alpha(if (inputMessage.isEmpty()) 0.5f else 1f)
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
    message : String = "hi",
    isUser : Boolean = false,
    deleteMessageLongClick : () -> Unit = {},
    resultAvailableStatus: String = "pending"
) {

    //var isDotVisible by remember { mutableStateOf(false) }
    val dotWave = rememberDotsLoadingController()


    Box(

        modifier = Modifier
            .fillMaxWidth()

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start

        ) {

            /* chatbot icon */
            if (!isUser){

                Spacer(modifier = Modifier.width(4.dp))

                Image( painter = painterResource(R.drawable.ic_bot),
                    contentDescription = "User",
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(17.dp)
                        .align(Alignment.Top)

                )

            }

            Box(

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
                        onLongClick = { deleteMessageLongClick() }
                    )
                    .background(color = if (isUser) Color(0xFFD849F1) else Color(0xFFFAD9D9))
                    .padding(10.dp)

            ) {

                /*
                if (!isUser){

                    when(resultAvailableStatus){

                        "pending" ->  LoadingWavy(
                            controller = dotWave,
                            modifier = Modifier
                                .wrapContentWidth(),
                            //.align(Alignment.Center)
                            //.padding(2.dp),
                            dotsCount = 3,
                            dotsColor = Color(0xFFD849F1),
                            dotsSize = 10.dp,
                            duration = 700,
                            easing = LinearEasing
                        )

                        "success" -> Text(text = message,
                            fontSize = 15.sp,
                            fontFamily = BanglaFont.font(),
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF000000),
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.Center)
                        )

                        "failed" -> Text(text = "পুনরায় চেষ্টা করুন।",
                            fontSize = 15.sp,
                            fontFamily = BanglaFont.font(),
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF000000),
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.Center)
                        )

                    }

                }else{

                    Text(text = message,
                        fontSize = 15.sp,
                        fontFamily = BanglaFont.font(),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFFFFFFF),
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.Center)
                    )

                }

                 */


                BoxWithConstraints{

                    val maxW = maxWidth * 0.7f

                    Text(text = message,
                        fontSize = 15.sp,
                        fontFamily = BanglaFont.font(),
                        fontWeight = FontWeight.Normal,
                        color = if (isUser) Color(0xFFFFFFFF) else Color(0xFF000000),
                        modifier = Modifier
                            .widthIn(max = maxW)
                            .align(Alignment.Center)
                    )

                }

            }//box

            /* user icon */
            if (isUser){

                Spacer(modifier = Modifier.width(4.dp))

                Image( painter = painterResource(R.drawable.ic_user),
                    contentDescription = "User",
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(17.dp)
                        .align(Alignment.Top)

                )

            }



        }//row

    }//box
    
}//fun end


@Preview(showBackground = true)
@Composable
private fun DeleteDialog(
    modifier : Modifier = Modifier,
    deleteClick : () -> Unit = {},
    closeClick : () -> Unit = {},
    copyClick : () -> Unit = {}
) {

    Box(

        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(15.dp))
                .clip(shape = RoundedCornerShape(15.dp))
                .background(color = Color(0xFFFFFFFF))
                .padding(14.dp)

        ) {

            Text(text = "কপি মেসেজ",
                fontSize = 15.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF695A5A),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(12.dp))
                    .clickable { copyClick() }
                    .border(
                        width = 1.dp,
                        color = Color(0xC6625353),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "ডিলিট মেসেজ",
                fontSize = 15.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFFF44336),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(12.dp))
                    .clickable { deleteClick() }
                    .border(
                        width = 1.dp,
                        color = Color(0xBEF44336),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
                )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "বন্ধ করুন",
                fontSize = 15.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF9F8888),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(12.dp))
                    .clickable { closeClick() }
                    .border(
                        width = 1.dp,
                        color = Color(0xFFB69494),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            )


        }//column

    }//box

}//fun end


@Preview(showBackground = true)
@Composable
private fun PopUpMenu(
    modifier: Modifier = Modifier,
    deleteAllMessage : () -> Unit = {}

) {

    Box(

        modifier = modifier
            .fillMaxWidth()
            .padding(7.dp)

    ) {

        Column(

            modifier = Modifier
                .width(170.dp)
                .wrapContentHeight()
                .shadow(elevation = 5.dp, shape = RoundedCornerShape(12.dp))
                .clip(shape = RoundedCornerShape(12.dp))
                .background(color = Color(0xFFFFFFFF))
                .padding(7.dp)
                .align(Alignment.CenterEnd)

        ) {

            Text("সব মেসেজ ডিলিট করুন",
                fontSize = 14.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(10.dp))
                    .clickable { deleteAllMessage() }
                    //.background(color = Color(0xFF00BCD4))
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
                )

        }//column

    }//box

    
}//fun end


@Preview(showBackground = true)
@Composable
fun AlertDialog(
    modifier: Modifier = Modifier,
    alertCloseClick : () -> Unit = {}
) {

    var isChecked by remember { mutableStateOf(false) }

    Box(

        modifier = modifier
            .fillMaxWidth()
            .padding(15.dp)

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 7.dp, shape = RoundedCornerShape(14.dp))
                .clip(shape = RoundedCornerShape(14.dp))
                .background(color = Color(0xFFFFFFFF))
                .padding(10.dp)
                .align(Alignment.Center)

        ) {

            Text(text = "আমাদের AI আইসিস্ট্যান্ট আপনােক আইনি ধারনা দিতে প্রস্তত। " +
                    "তবে এটি কোনো পেশাদার আইনজীবীর সরাসরি বিকল্প নয়। " +
                    "প্রতিটি আইনি বিষয়ের দিকগুলো ভিন্ন হতে পারে, তাই এই অ্যাপ থেকে প্রাপ্ত তথ্যগুলো প্রাথমিক গাইডলাইন হিসেবে বিবেচনা করুন।" +
                    "যেকোনো আইনি বাধ্যবাধকতা বা দাপ্তরিক কাজে ব্যবহারের আগে তথ্যগুলো সংশ্লিষ্ট আইন বিশেষজ্ঞের দ্বারা যাচাই করে নেওয়ার জন্য বিশেষ অনুরোধ রইলো।",
                fontSize = 17.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .align(Alignment.Start)

            ) {


                Checkbox(

                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    colors = CheckboxDefaults.colors(

                        checkedColor = Color(0xFF9C27B0),
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.White

                    ),
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(18.dp)
                        .align(Alignment.CenterVertically)

                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(text =  "আমি সম্মত আছি",
                    fontSize = 15.sp,
                    fontFamily = BanglaFont.font(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF463D3D),
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically)
                    )

            }//row

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "ঠিক আছে",
                fontSize = 13.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = Color(0xFF000000),
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(shape = RoundedCornerShape(12.dp))
                    .clickable(
                        enabled = if (isChecked) true else false
                    ) {
                        alertCloseClick()
                    }
                    //.background(color = Color(0xFF00BCD4))
                    .padding(8.dp)
                    .alpha(if (isChecked) 1f else 0.5f)
                    .align(Alignment.End)
                )

        }//column

    }//box
    
}//fun end


@Preview(showBackground = true)
@Composable
private fun EmptyChatMessage(
    modifier: Modifier = Modifier
) {

    Box(

        modifier = modifier
            .fillMaxWidth()

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)


        ) {

            Image( painter = painterResource(R.drawable.img_bot),
                contentDescription = "AI",
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .align(Alignment.CenterHorizontally)

            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "হ্যালো!",
                fontSize = 16.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = Color(0xFFFAEEEE))
                    .padding(start = 20.dp, end = 20.dp, top = 9.dp, bottom = 9.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "আমি কীভাবে আপনাকে সাহায্য করতে পারি?",
                fontSize = 16.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = Color(0xFFFAEEEE))
                    .padding(start = 20.dp, end = 20.dp, top = 9.dp, bottom = 9.dp)
                    .align(Alignment.CenterHorizontally)
            )

        }//column

    }//box

}//fun end