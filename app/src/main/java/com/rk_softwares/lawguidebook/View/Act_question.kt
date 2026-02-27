package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import android.util.Log
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
import com.rk_softwares.lawguidebook.View.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.View.theme_main.LightNav
import com.rk_softwares.lawguidebook.View.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.Database.BookmarkDatabase
import com.rk_softwares.lawguidebook.Helper.BanglaFont
import com.rk_softwares.lawguidebook.Helper.ComposeHelper
import com.rk_softwares.lawguidebook.Helper.IntentHelper
import com.rk_softwares.lawguidebook.Helper.InternetChecker
import com.rk_softwares.lawguidebook.Helper.InternetStatus
import com.rk_softwares.lawguidebook.Helper.KeyHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.Model.Data
import com.rk_softwares.lawguidebook.Presenter.QuestionPresenter
import com.rk_softwares.lawguidebook.Presenter.Questions
import com.rk_softwares.lawguidebook.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Act_question : ComponentActivity(), Questions, InternetStatus {//class=======================================================

    //init------
    private lateinit var bookmarkDatabase: BookmarkDatabase
    private val questionsList = mutableStateListOf<Data>()
    private lateinit var presenter : QuestionPresenter
    private lateinit var internetChecker: InternetChecker

    private var isInternet = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            init()

            internetChecker.onStart()

            var toolbarText by remember { mutableStateOf("") }

            toolbarText = intent.getStringExtra(KeyHelper.sendTitle_IntentKey()) ?: ""

            LaunchedEffect(isInternet.value) {

                presenter.questionsFromServer(toolbarText)

            }

            LawGuideBookTheme {

                QuestionFullScreen(
                    backClick = {
                        toolbarText = ""
                        finish()
                    },
                    toolbarTitle = toolbarText,
                    questionList = questionsList,
                    questionClick = {

                        IntentHelper.dataIntent(
                            this,
                            Act_answer::class.java,
                            KeyHelper.sendQuestion_IntentKey(),
                            it
                        )

                    },
                    bookmarkClick = { item -> presenter.dbInsert(item)},
                    internet = isInternet.value
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
        presenter = QuestionPresenter(this, bookmarkDatabase)
        internetChecker = InternetChecker(this, this)

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
        internetChecker.onStop()
    }


    override fun questionList(list: List<Data>) {

        questionsList.clear()
        questionsList.addAll(list)


    }

    override fun serverStatus(message: String) {

        Log.d("status", message)

    }

    override fun dbStatus(status: String) {
        Toast.makeText(this,status , Toast.LENGTH_SHORT).show()
    }

    override fun isInternet(internet: Boolean) {
        isInternet.value = internet
    }

}


@Preview(showBackground = true)
@Composable
private fun QuestionFullScreen(
    backClick : () -> Unit = {},
    toolbarTitle :  String = "",
    questionList : List<Data> = emptyList(),
    questionClick : (String) -> Unit = {},
    bookmarkClick : (String) -> Unit = {},
    internet : Boolean = false

) {

    val lazyState = rememberLazyListState()
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

            Column(

                modifier = Modifier
                    .fillMaxWidth()

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
