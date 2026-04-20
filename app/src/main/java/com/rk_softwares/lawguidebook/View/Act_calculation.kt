package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.colintheshots.twain.MarkdownText
import com.rk_softwares.lawguidebook.Helper.*
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.*
import kotlinx.coroutines.delay


class Act_calculation : ComponentActivity(), InternetStatus {


    private lateinit var internetChecker : InternetChecker

    //init----
    private var toolbarText = mutableStateOf("")
    private var isInternet = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            
            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = true
            )

            init()

            if (savedInstanceState == null){

                toolbarText.value = intent.getStringExtra(KeyHelper.calculationTitle_IntentKey()) ?: ""

            }


            LawGuideBookTheme {

                CalculationFullScreen(
                    backClick = {
                        finish()
                        toolbarText.value = ""
                    },
                    toolbarTitle = toolbarText.value,
                    internet = isInternet.value
                )

            }

            BackHandler {

                toolbarText.value = ""
                finish()

            }
        }
    }//on create===============================================

    private fun init(){

        internetChecker = InternetChecker(this, this)

        internetChecker.onStart()

    }

    override fun isInternet(internet: Boolean) {
       isInternet.value = internet
    }

    override fun onDestroy() {
        super.onDestroy()
        internetChecker.onStop()
    }
}//class=======================================================


@Preview(showBackground = true)
@Composable
private fun CalculationFullScreen(
    backClick: () -> Unit = {},
    toolbarTitle: String = "",
    internet: Boolean = false
){

    var isInternetDialogVisible by remember { mutableStateOf(false) }

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
            toolbarTitle = toolbarTitle
        ) },

        modifier = Modifier
            .fillMaxSize()
            .background(color = LightStatusBar)
            .systemBarsPadding()
    )

    { innerPadding ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())


            ) {

                TextToFunction(toolbarTitle)

            }//column

            if (isInternetDialogVisible){

                ComposeHelper.InternetDialog(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )

            }
            
        }//box

    }//scaffold

}//fun end


@Preview(showBackground = true)
@Composable
private fun Toolbar(
    backClick : () -> Unit = {},
    toolbarTitle : String = "",
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

            Spacer(modifier = Modifier.width(5.dp))

            Text(toolbarTitle,
                fontSize = 16.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF9C27B0),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically)
            )

        }//row

    }//box

}//fun end


@Composable
private fun TextToFunction(title : String){

    when(title){

        "উওরাধিকার" -> { InheritanceCalculator() }
        "রেজিস্ট্রেশন ফি" -> {}
        "দেনমোহর" -> {}

    }

}//fun end

@Preview(showBackground = true)
@Composable
private fun InheritanceCalculator() {

    var land = remember { mutableStateOf("") }
    var gold = remember { mutableStateOf("") }
    var silver = remember{ mutableStateOf("") }
    var money= remember { mutableStateOf("") }

    var isReset = remember { mutableStateOf(false) }
    var isCalculate = remember { mutableStateOf(false) }
    var isMarkdownTableVisible = remember { mutableStateOf(false) }

    val relations = remember {
        mutableStateListOf(
            "স্বামী", "স্ত্রী", "পুত্র", "কন্যা", "পিতা", "মাতা"
        )
    }

    val checkedState = remember {
        mutableStateListOf(
            false, false, false, false, false, false,
        )
    }

    val landCount = remember {
        mutableStateListOf(
            "০", "০", "০", "০", "০", "০"
        )
    }

    val goldCount = remember {
        mutableStateListOf(
            "০", "০", "০", "০", "০", "০"
        )
    }

    val silverCount = remember {
        mutableStateListOf(
            "০", "০", "০", "০", "০", "০"
        )
    }

    val moneyCount = remember {
        mutableStateListOf(
            "০", "০", "০", "০", "০", "০"
        )
    }


    //checking true ot false
    val husbandChecked = if (checkedState[0]) 1 else 0
    val wifeChecked = if (checkedState[1]) 1 else 0
    val sonChecked = if (checkedState[2]) 1 else 0
    val daughterChecked = if (checkedState[3]) 1 else 0
    val fatherChecked = if (checkedState[4]) 1 else 0
    val motherChecked = if (checkedState[5]) 1 else 0

    //relations name
    val husbandName = relations[0]
    val wifeName = relations[1]
    val sonName = relations[2]
    val daughterName = relations[3]
    val fatherName = relations[4]
    val motherName = relations[5]


    //all inheritance count

    val husbandLandPart = landCount[0]
    val husbandGoldPart = goldCount[0]
    val husbandSilverPart = silverCount[0]
    val husbandMoneyPart = moneyCount[0]

    val wifeLandPart = landCount[1]
    val wifeGoldPart = goldCount[1]
    val wifeSilverPart = silverCount[1]
    val wifeMoneyPart = moneyCount[1]

    val sonLandPart = landCount[2]
    val sonGoldPart = goldCount[2]
    val sonSilverPart = silverCount[2]
    val sonMoneyPart = moneyCount[2]

    val daughterLandPart = landCount[3]
    val daughterGoldPart = goldCount[3]
    val daughterSilverPart = silverCount[3]
    val daughterMoneyPart = moneyCount[3]

    val fatherLandPart = landCount[4]
    val fatherGoldPart = goldCount[4]
    val fatherSilverPart = silverCount[4]
    val fatherMoneyPart = moneyCount[4]

    val motherLandPart = landCount[5]
    val motherGoldPart = goldCount[5]
    val motherSilverPart = silverCount[5]
    val motherMoneyPart = moneyCount[5]

    /*

      * when the reset button is clicked, everything will be initialized

     */

    LaunchedEffect(isReset.value) {

        if (isReset.value){

            land.value = ""
            gold.value = ""
            silver.value = ""
            money.value = ""

            checkedState.replaceAll{ false }

            landCount.replaceAll{ "০" }
            goldCount.replaceAll{ "০" }
            silverCount.replaceAll{ "০" }
            moneyCount.replaceAll{ "০" }

            isReset.value = false
            isCalculate.value = false
            isMarkdownTableVisible.value = false

        }

    }

    val defaultChecked = husbandChecked == 0 && wifeChecked == 0 &&
            sonChecked == 0 && daughterChecked == 0 &&
            fatherChecked == 0 && motherChecked == 0

    val defaultCount = land.value.isBlank() && gold.value.isBlank() && silver.value.isBlank()
            && money.value.isBlank()

    //checking check mark behavior for Markdown visibility & calculation

    LaunchedEffect(isCalculate.value) {

        if (!defaultChecked && isCalculate.value){

            //calculating the land, gold, silver, money

            val land = inheritanceCalculationHelper(
                land.value,
                husbandChecked,
                wifeChecked,
                sonChecked,
                daughterChecked,
                fatherChecked,
                motherChecked
            )

            val gold = inheritanceCalculationHelper(
                gold.value,
                husbandChecked,
                wifeChecked,
                sonChecked,
                daughterChecked,
                fatherChecked,
                motherChecked
            )

            val silver = inheritanceCalculationHelper(
                silver.value,
                husbandChecked,
                wifeChecked,
                sonChecked,
                daughterChecked,
                fatherChecked,
                motherChecked
            )

            val money = inheritanceCalculationHelper(
                money.value,
                husbandChecked,
                wifeChecked,
                sonChecked,
                daughterChecked,
                fatherChecked,
                motherChecked
            )

            landCount[0] = if (land["husband"].toString() == "null" || land["husband"].toString().isEmpty()) "০.০০" else land["husband"].toString()
            landCount[1] = if (land["wife"].toString() == "null" || land["wife"].toString().isEmpty()) "০.০০" else land["wife"].toString()
            landCount[2] = if (land["son"].toString() == "null" || land["son"].toString().isEmpty()) "০.০০" else land["son"].toString()
            landCount[3] = if (land["daughter"].toString() == "null" || land["daughter"].toString().isEmpty()) "০.০০" else land["daughter"].toString()
            landCount[4] = if (land["father"].toString() == "null" || land["father"].toString().isEmpty()) "০.০০" else land["father"].toString()
            landCount[5] = if (land["mother"].toString() == "null" || land["mother"].toString().isEmpty()) "০.০০" else land["mother"].toString()

            goldCount[0] = if (gold["husband"].toString() == "null" || gold["husband"].toString().isEmpty()) "০.০০" else gold["husband"].toString()
            goldCount[1] = if (gold["wife"].toString() == "null" || gold["wife"].toString().isEmpty()) "০.০০" else gold["wife"].toString()
            goldCount[2] = if (gold["son"].toString() == "null" || gold["son"].toString().isEmpty()) "০.০০" else gold["son"].toString()
            goldCount[3] = if (gold["daughter"].toString() == "null" || gold["daughter"].toString().isEmpty()) "০.০০" else gold["daughter"].toString()
            goldCount[4] = if (gold["father"].toString() == "null" || gold["father"].toString().isEmpty()) "০.০০" else gold["father"].toString()
            goldCount[5] = if (gold["mother"].toString() == "null" || gold["mother"].toString().isEmpty()) "০.০০" else gold["mother"].toString()

            silverCount[0] = if (silver["husband"].toString() == "null" || silver["husband"].toString().isEmpty()) "০.০০" else silver["husband"].toString()
            silverCount[1] = if (silver["wife"].toString() == "null" || silver["wife"].toString().isEmpty()) "০.০০" else silver["wife"].toString()
            silverCount[2] = if (silver["son"].toString() == "null" || silver["son"].toString().isEmpty()) "০.০০" else silver["son"].toString()
            silverCount[3] = if (silver["daughter"].toString() == "null" || silver["daughter"].toString().isEmpty()) "০.০০" else silver["daughter"].toString()
            silverCount[4] = if (silver["father"].toString() == "null" || silver["father"].toString().isEmpty()) "০.০০" else silver["father"].toString()
            silverCount[5] = if (silver["mother"].toString() == "null" || silver["mother"].toString().isEmpty()) "০.০০" else silver["mother"].toString()

            moneyCount[0] = if (money["husband"].toString() == "null" || money["husband"].toString().isEmpty()) "০.০০" else money["husband"].toString()
            moneyCount[1] = if (money["wife"].toString() == "null" || money["wife"].toString().isEmpty()) "০.০০" else money["wife"].toString()
            moneyCount[2] = if (money["son"].toString() == "null" || money["son"].toString().isEmpty()) "০.০০" else money["son"].toString()
            moneyCount[3] = if (money["daughter"].toString() == "null" || money["daughter"].toString().isEmpty()) "০.০০" else money["daughter"].toString()
            moneyCount[4] = if (money["father"].toString() == "null" || money["father"].toString().isEmpty()) "০.০০" else money["father"].toString()
            moneyCount[5] = if (money["mother"].toString() == "null" || money["mother"].toString().isEmpty()) "০.০০" else money["mother"].toString()

            isMarkdownTableVisible.value = true

        }else{

            isMarkdownTableVisible.value = false

            isCalculate.value = false

        }

    }


    Box(

        modifier = Modifier
            .fillMaxWidth()

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()

        ) {

            //land

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(9.dp)

            ) {

                Spacer(modifier = Modifier.width(13.dp))

                Text( text = "জমি:",
                    fontSize = 15.sp,
                    fontFamily = Bangla.banglaFont(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    color = Color(0xFF000000),
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically)

                )

                Spacer(modifier = Modifier.width(15.dp))

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFBEA1A1),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(3.dp)
                        .align(Alignment.CenterVertically)
                        .imePadding()

                ) {

                    if (land.value.isEmpty()){

                        Text(text = "০.০০ শতাংশ",
                            fontSize = 15.sp,
                            fontFamily = Bangla.banglaFont(),
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF9F7A7A),
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(start = 10.dp)
                                .align(Alignment.CenterStart)
                        )

                    }

                    BasicTextField(
                        value = land.value,
                        onValueChange = { land.value = it },
                        textStyle = TextStyle(color = Color(0xFF000000), fontSize = 15.sp, fontFamily = Bangla.banglaFont(), fontWeight = FontWeight.Normal),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {}
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.90f)
                            .padding(5.dp)
                            .align(Alignment.CenterStart)
                    )

                    if (land.value.isNotEmpty()){

                        IconButton(
                            onClick = { land.value = "" },
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(end = 5.dp)
                                .clip(shape = CircleShape)
                                .size(30.dp)
                                .align(Alignment.CenterEnd)
                        ) {

                            Icon( painter = painterResource(R.drawable.ic_close),
                                contentDescription = "Clear",
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .size(18.dp)
                                    .align(Alignment.Center)

                            )

                        }

                    }

                }//box

            }//row

            //gold

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(9.dp)

            ) {

                Spacer(modifier = Modifier.width(13.dp))

                Text( text = "স্বর্ন:",
                    fontSize = 15.sp,
                    fontFamily = Bangla.banglaFont(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    color = Color(0xFF000000),
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically)

                )

                Spacer(modifier = Modifier.width(15.dp))

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFBEA1A1),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(3.dp)
                        .align(Alignment.CenterVertically)
                        .imePadding()

                ) {

                    if (gold.value.isEmpty()){

                        Text(text = "০ ভরি",
                            fontSize = 15.sp,
                            fontFamily = Bangla.banglaFont(),
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF9F7A7A),
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(start = 10.dp)
                                .align(Alignment.CenterStart)
                        )

                    }

                    BasicTextField(
                        value = gold.value,
                        onValueChange = { gold.value = it },
                        textStyle = TextStyle(color = Color(0xFF000000), fontSize = 15.sp, fontFamily = Bangla.banglaFont(), fontWeight = FontWeight.Normal),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {}
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.90f)
                            .padding(5.dp)
                            .align(Alignment.CenterStart)
                    )

                    if (gold.value.isNotEmpty()){

                        IconButton(
                            onClick = { gold.value = "" },
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(end = 5.dp)
                                .clip(shape = CircleShape)
                                .size(30.dp)
                                .align(Alignment.CenterEnd)
                        ) {

                            Icon( painter = painterResource(R.drawable.ic_close),
                                contentDescription = "Clear",
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .size(18.dp)
                                    .align(Alignment.Center)

                            )

                        }

                    }

                }//box

            }//row

            //silver

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(9.dp)

            ) {

                Spacer(modifier = Modifier.width(13.dp))

                Text( text = "রৌপ্য:",
                    fontSize = 15.sp,
                    fontFamily = Bangla.banglaFont(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    color = Color(0xFF000000),
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically)

                )

                Spacer(modifier = Modifier.width(15.dp))

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFBEA1A1),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(3.dp)
                        .align(Alignment.CenterVertically)
                        .imePadding()

                ) {

                    if (silver.value.isEmpty()){

                        Text(text = "০ ভরি",
                            fontSize = 15.sp,
                            fontFamily = Bangla.banglaFont(),
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF9F7A7A),
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(start = 10.dp)
                                .align(Alignment.CenterStart)
                        )

                    }

                    BasicTextField(
                        value = silver.value,
                        onValueChange = { silver.value = it },
                        textStyle = TextStyle(color = Color(0xFF000000), fontSize = 15.sp, fontFamily = Bangla.banglaFont(), fontWeight = FontWeight.Normal),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {}
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.90f)
                            .padding(5.dp)
                            .align(Alignment.CenterStart)
                    )

                    if (silver.value.isNotEmpty()){

                        IconButton(
                            onClick = { silver.value = "" },
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(end = 5.dp)
                                .clip(shape = CircleShape)
                                .size(30.dp)
                                .align(Alignment.CenterEnd)
                        ) {

                            Icon( painter = painterResource(R.drawable.ic_close),
                                contentDescription = "Clear",
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .size(18.dp)
                                    .align(Alignment.Center)

                            )

                        }

                    }

                }//box

            }//row

            //money

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(9.dp)

            ) {

                Spacer(modifier = Modifier.width(13.dp))

                Text( text = "টাকা:",
                    fontSize = 15.sp,
                    fontFamily = Bangla.banglaFont(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    color = Color(0xFF000000),
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterVertically)

                )

                Spacer(modifier = Modifier.width(15.dp))

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFBEA1A1),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(3.dp)
                        .align(Alignment.CenterVertically)
                        .imePadding()

                ) {

                    if (money.value.isEmpty()){

                        Text(text = "০ টাকা",
                            fontSize = 15.sp,
                            fontFamily = Bangla.banglaFont(),
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF9F7A7A),
                            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(start = 10.dp)
                                .align(Alignment.CenterStart)
                        )

                    }

                    BasicTextField(
                        value = money.value,
                        onValueChange = { money.value = it },
                        textStyle = TextStyle(color = Color(0xFF000000), fontSize = 15.sp, fontFamily = Bangla.banglaFont(), fontWeight = FontWeight.Normal),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {}
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.90f)
                            .padding(5.dp)
                            .align(Alignment.CenterStart)
                    )

                    if (money.value.isNotEmpty()){

                        IconButton(
                            onClick = { money.value = "" },
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(end = 5.dp)
                                .clip(shape = CircleShape)
                                .size(30.dp)
                                .align(Alignment.CenterEnd)
                        ) {

                            Icon( painter = painterResource(R.drawable.ic_close),
                                contentDescription = "Clear",
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .size(18.dp)
                                    .align(Alignment.Center)

                            )

                        }

                    }

                }//box

            }//row

            Spacer(modifier = Modifier.height(7.dp))

            Text( text = "ওয়ারিশ নির্বাচন",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF000000),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(5.dp)
                    .align(Alignment.Start)

            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .align(Alignment.CenterHorizontally)
            ) {

                items(
                    count = relations.size
                ){ index ->

                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(3.dp)

                    ) {

                        Row(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(3.dp)

                        ) {

                            Checkbox(
                                checked = checkedState[index],
                                onCheckedChange = { checkedState[index] = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF7CAD43)
                                ),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .align(Alignment.CenterVertically)
                            )

                            Text(text = relations[index],
                                fontSize = 15.sp,
                                fontFamily = Bangla.banglaFont(),
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF000000),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically)
                                )

                        }//row

                    }//box

                }//items

            }//lazy grid

            //buttons

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp)

            ) {

                Spacer(modifier = Modifier.width(10.dp))

                Text( text = "হিসাব করুন",
                    fontSize = 14.sp,
                    fontFamily = Bangla.banglaFont(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .alpha(if (defaultChecked) 0.5f else 1f)
                        .clickable(
                            !defaultChecked
                        ) { isCalculate.value = true }
                        .background(color = Color(0xFF4CAF50))
                        .padding(6.dp)
                        .align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text( text = "রিসেট",
                    fontSize = 14.sp,
                    fontFamily = Bangla.banglaFont(),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF000000),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .clickable { isReset.value = true }
                        .background(color = Color(0xFFD6E3D6))
                        .padding(6.dp)
                        .align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.width(10.dp))

            }//row

            Spacer(modifier = Modifier.height(12.dp))

            //result with Markdown table

            val result = buildString {

                append("| ওয়ারিশ | জমি (শতাংশ) | স্বর্ন (ভরি) | রৌপ্য (ভরি) | টাকা |\n")
                append("|------|-----|----|-----|-----|\n")

                if (husbandChecked == 1){

                    if (defaultCount){

                        append("| $husbandName | $husbandLandPart | $husbandGoldPart | $husbandSilverPart | $husbandMoneyPart |\n")

                    }else{

                        append("| $husbandName | $husbandLandPart | $husbandGoldPart | $husbandSilverPart | $husbandMoneyPart |\n")

                    }

                }

                if (wifeChecked == 1){

                    if (defaultCount){

                        append("| $wifeName | $wifeLandPart | $wifeGoldPart | $wifeSilverPart | $wifeMoneyPart |\n")

                    }else{

                        append("| $wifeName | $wifeLandPart | $wifeGoldPart | $wifeSilverPart | $wifeMoneyPart |\n")

                    }

                }

                if (sonChecked == 1){

                    if (defaultCount){

                        append("| $sonName | $sonLandPart | $sonGoldPart | $sonSilverPart | $sonMoneyPart |\n")

                    }else{

                        append("| $sonName | $sonLandPart | $sonGoldPart | $sonSilverPart | $sonMoneyPart |\n")

                    }

                }

                if (daughterChecked == 1){

                    if (defaultCount){

                        append("| $daughterName | $daughterLandPart | $daughterGoldPart | $daughterSilverPart | $daughterMoneyPart |\n")

                    }else{

                        append("| $daughterName | $daughterLandPart | $daughterGoldPart | $daughterSilverPart | $daughterMoneyPart |\n")

                    }

                }

                if (fatherChecked == 1){

                    if (defaultCount){

                        append("| $fatherName | $fatherLandPart | $fatherGoldPart | $fatherSilverPart | $fatherMoneyPart |\n")

                    }else{

                        append("| $fatherName | $fatherLandPart | $fatherGoldPart | $fatherSilverPart | $fatherMoneyPart |\n")

                    }

                }

                if (motherChecked == 1){

                    if (defaultCount){

                        append("| $motherName | $motherLandPart | $motherGoldPart | $motherSilverPart | $motherMoneyPart |\n")

                    }else{

                        append("| $motherName | $motherLandPart | $motherGoldPart | $motherSilverPart | $motherMoneyPart |\n")

                    }

                }

            }

            if (isMarkdownTableVisible.value){

                MarkdownText(
                    markdown = result,
                    fontSize = 15.sp,
                    color = Color(0xFF000000),
                    fontResource = R.font.noto_serif_bengali,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(9.dp)
                )

            }

            Spacer(modifier = Modifier.height(12.dp))


        }//column

    }//box

}//fun end


private fun inheritanceCalculationHelper (
    item : String,
    husband : Int,
    wife : Int,
    son : Int,
    daughter : Int,
    father : Int,
    mother : Int
) : Map<String, String> {

    val total = if (item.isBlank() || item.isEmpty()) 0.0 else item.toDouble()

    var remaining = total

    val map = mutableMapOf<String, String>()

    val hasChild = (son == 1 || daughter == 1)

    if (husband == 1){

        val husbandPart = if (hasChild) total / 4 else total / 2

        remaining -= husbandPart

        val f_husband = String.format(java.util.Locale.ENGLISH, "%.2f", husbandPart)

        map["husband"] = Bangla.banglaNumber(f_husband)

    }

    if (wife == 1){

        val wifePart = if (hasChild) total / 8 else total / 4

        remaining -= wifePart

        val f_wife = String.format(java.util.Locale.ENGLISH, "%.2f", wifePart)

        map["wife"] = Bangla.banglaNumber(f_wife)

    }

    if (mother == 1){

        val motherPart = if (hasChild) total / 6 else total / 3

        remaining -= motherPart

        val f_mother = String.format(java.util.Locale.ENGLISH, "%.2f", motherPart)

        map["mother"] = Bangla.banglaNumber(f_mother)

    }

    if (father == 1){

        val fatherPart = if (hasChild) total / 6 else remaining

        if (hasChild) remaining -= fatherPart

        val f_father = String.format(java.util.Locale.ENGLISH, "%.2f", fatherPart)

        map["father"] = Bangla.banglaNumber(f_father)

    }

    if (hasChild){

        val unit = remaining / ((son * 2) + daughter)

        if (son == 1){

            val sonPart = unit * 2

            val f_son = String.format(java.util.Locale.ENGLISH, "%.2f", sonPart)

            map["son"] = Bangla.banglaNumber(f_son)

        }

        if (daughter == 1){

            val f_daughter = String.format(java.util.Locale.ENGLISH, "%.2f", unit)

            map["daughter"] = Bangla.banglaNumber(f_daughter)

        }

    }

    return map

}//fun end