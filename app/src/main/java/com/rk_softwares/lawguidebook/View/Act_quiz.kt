package com.rk_softwares.lawguidebook.View

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rk_softwares.lawguidebook.Database.QuizDatabase
import com.rk_softwares.lawguidebook.Helper.Bangla
import com.rk_softwares.lawguidebook.Helper.IntentHelper
import com.rk_softwares.lawguidebook.Helper.ScreenSize
import com.rk_softwares.lawguidebook.Helper.ShortMessageHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.Model.QuizData
import com.rk_softwares.lawguidebook.Presenter.Quiz
import com.rk_softwares.lawguidebook.Presenter.QuizPresenter
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.View.theme_main.LightNav
import com.rk_softwares.lawguidebook.View.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBarIcon
import kotlinx.coroutines.delay

class Act_quiz : ComponentActivity(), Quiz{

    private lateinit var qDB : QuizDatabase
    private lateinit var qPresenter : QuizPresenter

    /* Initialize variable */

    private var qList = mutableStateListOf<QuizData>()
    private var qStatus = mutableStateOf("")


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

            qPresenter.getAllQuiz()

            //ShortMessageHelper.toast(this, ScreenSize().height().toString())


            LawGuideBookTheme {

                QuizFullScreen(
                    backClick = {
                        IntentHelper.normalIntent(this, Act_home::class.java)
                        finishAffinity()
                                },
                    list = qList,
                    qStatus = qStatus.value,
                    userSelectedInput = { title, userInput ->

                        Log.d("input_title", "$title > $userInput")

                        qPresenter.updateQuiz( title = title, userInput = userInput )

                    }
                )

            }

            BackHandler() {

                IntentHelper.normalIntent(this, Act_home::class.java)
                finishAffinity()

            }

        }
    }//on create=============================

    private fun init(){

        qDB = QuizDatabase(this)

        qPresenter = QuizPresenter(this, qDB)

    }

    override fun onDestroy() {
        super.onDestroy()
        qPresenter.onDestroy()
    }

    override fun quizList(list: List<QuizData>) {

        qList.clear()
        qList.addAll(list)

    }

    override fun quizCount(tQuiz: Int, cQuiz: Int) {

    }

    override fun dbStatus(status: String) {
        qStatus.value = status
    }

}//class=====================================

@Preview(showBackground = true)
@Composable
private fun QuizFullScreen(
    backClick: () -> Unit = {},
    list : List<QuizData> = emptyList(),
    qStatus : String = "",
    userSelectedInput: (String, Int) -> Unit = {_, _ ->}
) {

    val lazyState = rememberLazyListState()
    
    Scaffold(

        topBar = { Toolbar( backClick = backClick ) },
        modifier = Modifier
            .fillMaxSize()
            .background(color = LightToolBar)
            .systemBarsPadding()

    ) { innerPadding ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {

            if (list.isEmpty()){

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)

                ) {

                    Image( painter = painterResource(R.drawable.img_empty_quiz),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(90.dp)
                            .align(Alignment.CenterHorizontally)

                    )

                    Spacer(modifier = Modifier.height(9.dp))

                    Text( text = "কোন কুইজ নেই।",
                        fontSize = 15.sp,
                        fontFamily = Bangla.banglaFont(),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)

                    )

                }//column

            }else{

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart),
                    state = lazyState

                ) {

                    items(

                        items = list,
                        key = null

                    ){ quiz ->

                        Item(
                            title = quiz.title,
                            optionA = quiz.optionA,
                            optionB = quiz.optionB,
                            optionC = quiz.optionC,
                            optionD = quiz.optionD,
                            answer = quiz.answer,
                            userSelectedItem = if (quiz.userInput > 0) quiz.userInput else null,
                            userSelectedInput = { qttitle, input->
                                userSelectedInput(qttitle, input)
                            }
                        )

                    }

                }//lazy column

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
                .padding(ScreenSize().responsivePadding(5, 8, 11))
                .align(Alignment.CenterStart)

        ) {

            Spacer(modifier = Modifier.width(3.dp))

            IconButton(
                onClick = { backClick() },
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(shape = CircleShape)
                    .size(ScreenSize().responsiveImageSize(35, 38, 41))
                    //.background(color = Color(0xFFDEC3C3))
                    .align(Alignment.CenterVertically)
            ) {

                Icon( painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = LightToolBarIcon,
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(ScreenSize().responsiveImageSize(22, 25, 28))

                )

            }

        }//row

    }//box

}//fun end

@Preview(showBackground = true)
@Composable
private fun Item(
    title : String = "বাংলাদেশের সংবিধান কত সালে গৃহীত হয়?",
    optionA : String = "১৯৭১",
    optionB: String = "১৯৭২",
    optionC : String = "১৯৭৫",
    optionD: String = "১৯৮১",
    answer : String = "১৯৭২",
    userSelectedItem : Int? = null,
    userSelectedInput : (String, Int) -> Unit = {_, _ ->}
) {

    var isOptionsVisible = remember { mutableStateOf(false) }
    var selectedIndex = remember { mutableStateOf(userSelectedItem) }
    var isAnswerClicked = remember { mutableStateOf(false) }


    LaunchedEffect(userSelectedItem) {

        if (userSelectedItem != null) {

            selectedIndex.value = if ( userSelectedItem > -1) userSelectedItem - 1 else -1

        }else selectedIndex.value = userSelectedItem

        if (userSelectedItem != null) isAnswerClicked.value = true

    }

    val currentSelected = selectedIndex.value ?: userSelectedItem

    val arrowAnimation = animateFloatAsState(
        targetValue = if (isOptionsVisible.value) 270f else 90f,
        label = "",
    )

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)

    ) {

        Box(

            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 1.dp, shape = RoundedCornerShape(13.dp))
                .clip(shape = RoundedCornerShape(13.dp))
                .clickable{ isOptionsVisible.value = !isOptionsVisible.value }
                .background(color = Color(0xFFFFFFFF))
                .padding(ScreenSize().responsivePadding(7, 10, 13))

        ) {

            Text( text = title,
                fontSize = ScreenSize().responsiveTextSize(14, 16, 18),
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
                maxLines = if (!isOptionsVisible.value) 1 else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .padding(3.dp)
                    .align(Alignment.CenterStart)

            )

            Icon( painter = painterResource(R.drawable.ic_right),
                contentDescription = "",
                tint = Color(0xFF736E6E),
                modifier = Modifier
                    .wrapContentWidth()
                    .size(ScreenSize().responsiveImageSize(24, 27, 29))
                    .rotate(arrowAnimation.value)
                    .align(Alignment.CenterEnd)

            )

        }//box

        //=========================
        // Options
        //=========================

        if (isOptionsVisible.value){

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(9.dp)
                    .align(Alignment.CenterHorizontally)

            ) {

                val options = arrayOf(optionA, optionB, optionC, optionD)

                val serial = arrayOf("ক: ", "খ: ", "গ: ", "ঘ: ")

                options.forEachIndexed { index, text ->

                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)

                    ) {

                        Row(

                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 1.dp, color = Color(0xFFDEDCDC), shape = RoundedCornerShape(13.dp))
                                .clip(shape = RoundedCornerShape(13.dp))
                                .clickable(
                                    enabled = if (isAnswerClicked.value) false else true
                                ){
                                    selectedIndex.value = index

                                    isAnswerClicked.value = true

                                    userSelectedInput(title ,index + 1)
                                }
                                .alpha(alpha = if (!isAnswerClicked.value || selectedIndex.value == index) 1f else 0.5f)
                                .padding(ScreenSize().responsivePadding(9, 12, 15))

                        ) {

                            Text( text = serial[index] ,
                                fontSize = ScreenSize().responsiveTextSize(14, 16, 18),
                                fontFamily = Bangla.banglaFont(),
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                color = Color(0xFF000000),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .padding(start = 3.dp)
                                    .align(Alignment.CenterVertically)
                            )

                            Text( text = text ,
                                fontSize = ScreenSize().responsiveTextSize(14, 16, 18),
                                fontFamily = Bangla.banglaFont(),
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start,
                                color = Color(0xFF000000),
                                modifier = Modifier
                                    .fillMaxWidth(0.92f)
                                    .padding(start = 2.dp)
                                    .align(Alignment.CenterVertically)
                            )

                            if (currentSelected == index){

                                val isCorrect = options[index] == answer

                                Spacer(modifier = Modifier.width(4.dp))

                                Icon( painter = painterResource(if (isCorrect) R.drawable.ic_ok else R.drawable.ic_wrong),
                                    contentDescription = "",
                                    tint = Color(0xFF5E5C5C),
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .size( if (isCorrect) ScreenSize().responsiveImageSize(19, 21, 24) else ScreenSize().responsiveImageSize(14, 17, 20))
                                        .align(Alignment.CenterVertically)

                                )

                            }

                        }//row

                    }//box

                }//loop

            }//column

        }//condition

    }//column

}//fun end
