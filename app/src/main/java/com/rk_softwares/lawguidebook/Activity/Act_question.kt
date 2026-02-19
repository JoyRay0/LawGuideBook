package com.rk_softwares.lawguidebook.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.rk_softwares.lawguidebook.Activity.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.Activity.theme_main.LightNav
import com.rk_softwares.lawguidebook.Activity.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.Activity.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.Database.BookmarkDatabase
import com.rk_softwares.lawguidebook.Helper.BanglaFont
import com.rk_softwares.lawguidebook.Helper.IntentHelper
import com.rk_softwares.lawguidebook.Helper.InternetChecker
import com.rk_softwares.lawguidebook.Helper.KeyHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.Model.ItemList
import com.rk_softwares.lawguidebook.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Act_question : ComponentActivity() {

    private lateinit var bookmarkDatabase: BookmarkDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            init()

            var toolbarText by remember { mutableStateOf("") }
            val questionList = remember { mutableStateListOf<ItemList>() }
            val scope = rememberCoroutineScope()

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            questionList.clear()
            questionList.add(ItemList(question = "সরণ কাকে বলে? কত প্রকার ও কি কি?"))
            questionList.add(ItemList(question = "কেন্দ্রীয় প্রবণতা কাকে বলে? কেন এটি পরিসংখ্যানের প্রাণকেন্দ্র?"))
            questionList.add(ItemList(question = "দর্শন কাকে বলে? কত প্রকার ও কি কি?"))
            questionList.add(ItemList(question = "রাষ্ট্রবিজ্ঞান কাকে বলে?"))
            questionList.add(ItemList(question = "বিশ্বের সর্বকালের সেরা ফুটবলার কে? ২০২৫"))
            questionList.add(ItemList(question = "বাক্য কাকে বলে? বাক্যের প্রকারভেদ?"))
            questionList.add(ItemList(question = "কারক ও বিভক্তি কাকে বলে?"))


            toolbarText = intent.getStringExtra(KeyHelper.sendTitle_IntentKey()) ?: ""

            val internet = InternetChecker.liveInternetStatus(this)

            Toast.makeText(this,if (internet) "On" else "Off", Toast.LENGTH_SHORT).show()

            LawGuideBookTheme {

                QuestionFullScreen(
                    backClick = {
                        toolbarText = ""
                        finish()
                    },
                    toolbarTitle = toolbarText,
                    questionList = questionList,
                    questionClick = {

                        IntentHelper.dataIntent(
                            this,
                            Act_answer::class.java,
                            KeyHelper.sendQuestion_IntentKey(),
                            it
                        )

                    },
                    bookmarkClick = { item ->

                        scope.launch {

                            withContext(Dispatchers.IO){

                                bookmarkDatabase.insert(item)

                            }

                            Toast.makeText(this@Act_question, "সেভ হয়েছে", Toast.LENGTH_SHORT).show()

                        }

                    }
                )

            }

            BackHandler{
                toolbarText = ""
                finish()

            }

        }
    }//on create===============================================

    private fun init(){

        bookmarkDatabase = BookmarkDatabase(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        bookmarkDatabase.closeDB()
    }

}//class=======================================================


@Preview(showBackground = true)
@Composable
private fun QuestionFullScreen(
    backClick : () -> Unit = {},
    toolbarTitle :  String = "",
    questionList : List<ItemList> = emptyList(),
    questionClick : (String) -> Unit = {},
    bookmarkClick : (String) -> Unit = {}

) {

    val lazyState = rememberLazyListState()

    Scaffold(
        topBar = { Toolbar(
            backClick = { backClick() },
            toolbarTitle = toolbarTitle
        ) },

        modifier = Modifier.fillMaxSize())
    { innerPadding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {


            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                state = lazyState,
                contentPadding = PaddingValues(5.dp)

            ) { 
                
                items(
                    items = questionList,
                    key = { it.question }
                ){ it ->

                    Item(
                        title = it.question,
                        titleClick = { questionClick(it.question) },
                        bookmarkTitleClick = { bookmarkClick(it.question) }
                    )

                }
                
            }

        }//column

    }//scaffold

}//fun end


@Preview(showBackground = true)
@Composable
private fun Toolbar(
    backClick : () -> Unit = {},
    toolbarTitle : String = ""
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

            Spacer(modifier = Modifier.width(5.dp))

            Text(toolbarTitle,
                fontSize = 16.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFFFFF),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.Center)
            )

        }//row

    }//box

}//fun end


@Preview(showBackground = true)
@Composable
private fun Item(
    modifier: Modifier = Modifier,
    title : String = "Title",
    titleClick : () -> Unit = {},
    bookmarkTitleClick : () -> Unit = {}
    ) {

    var isBookmarkVisible by remember { mutableStateOf(false) }

    Box(

        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)

    ) {

        Box(

            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp))
                .clip(shape = RoundedCornerShape(12.dp))
                .combinedClickable(

                    onLongClick = {

                        isBookmarkVisible = true

                    },

                    onClick = {
                        isBookmarkVisible = false
                        titleClick()

                    }
                )
                .background(color = Color(0xFFFFFFFF))
                .padding(7.dp)
                .align(Alignment.Center)

        ) {

            Text(text = title,
                fontSize = 16.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
                modifier = Modifier
                    .fillMaxWidth(0.93f)
                    .padding(3.dp)
                    .align(Alignment.CenterStart)
            )

            if (isBookmarkVisible){

                IconButton(
                    onClick = {
                        bookmarkTitleClick()
                        isBookmarkVisible = false
                              },
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(shape = CircleShape)
                        .size(30.dp)
                        .align(Alignment.CenterEnd)

                ) {

                    Icon( painter = painterResource(R.drawable.ic_bookmark),
                        contentDescription = "Delete",
                        tint = Color(0xFF4D4747),
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
                        .wrapContentWidth()
                        .align(Alignment.CenterEnd)

                )

            }

        }//box

    }//box

}//fun end
