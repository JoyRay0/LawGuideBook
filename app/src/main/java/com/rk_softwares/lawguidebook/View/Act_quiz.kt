package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import com.rk_softwares.lawguidebook.Helper.Bangla
import com.rk_softwares.lawguidebook.Helper.ShortMessageHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.View.theme_main.LightNav
import com.rk_softwares.lawguidebook.View.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBarIcon
import kotlinx.coroutines.delay

class Act_quiz : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = true
            )

            LawGuideBookTheme {

                QuizFullScreen(
                    backClick = { finish() }
                )

            }
        }
    }//on create=============================
}//class=====================================

@Preview(showBackground = true)
@Composable
private fun QuizFullScreen(
    backClick: () -> Unit = {}
) {


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
                .padding(5.dp)
                .align(Alignment.CenterStart)

        ) {

            Spacer(modifier = Modifier.width(3.dp))

            IconButton(
                onClick = { backClick() },
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(shape = CircleShape)
                    .size(34.dp)
                    //.background(color = Color(0xFFDEC3C3))
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
    title : String = "বাংলাদেশের সংবিধান কত সালে গৃহীত হয়?",
    optionA : String = "১৯৭১",
    optionB: String = "১৯৭২",
    optionC : String = "১৯৭৫",
    optionD: String = "১৯৮১",
    answer : String = "১৯৭২",
    userSelectedItem : Int? = null,
    userSelectedInput : (Int) -> Unit = {}
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
                .padding(10.dp)

        ) {

            Text( text = title,
                fontSize = 15.sp,
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

                                    userSelectedInput(index + 1)
                                }
                                .alpha(alpha = if (!isAnswerClicked.value || selectedIndex.value == index) 1f else 0.5f)
                                .padding(12.dp)

                        ) {

                            Text( text = serial[index] ,
                                fontSize = 15.sp,
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
                                fontSize = 15.sp,
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
                                        .size( if (isCorrect) 21.dp else 17.dp)
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
