package com.rk_softwares.lawguidebook.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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

        init()

        toolbarText.value = intent.getStringExtra(KeyHelper.calculationTitle_IntentKey()) ?: ""

        setContent {
            
            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = true
            )

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

    }

    override fun isInternet(internet: Boolean) {
       isInternet.value = internet
    }

    override fun onStart() {
        super.onStart()
        internetChecker.onStart()
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
        "রেজিস্ট্রেশন ফি" -> { DocumentRegistration() }
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
                .padding(7.dp)
                .imePadding()

        ) {

            //land

            Text( text = "১। জমি",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.Start)

            )

            Spacer(modifier = Modifier.height(5.dp))

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(7.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFBEA1A1),
                        shape = RoundedCornerShape(7.dp)
                    )
                    .padding(3.dp)
                    .align(Alignment.CenterHorizontally)

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
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
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

            //gold

            Spacer(modifier = Modifier.height(15.dp))

            Text( text = "২। স্বর্ন:",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.Start)

            )

            Spacer(modifier = Modifier.height(5.dp))

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(7.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFBEA1A1),
                        shape = RoundedCornerShape(7.dp)
                    )
                    .padding(3.dp)
                    .align(Alignment.Start)

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
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
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

            //silver

            Spacer(modifier = Modifier.height(15.dp))

            Text( text = "৩। রৌপ্য:",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.Start)

            )

            Spacer(modifier = Modifier.height(5.dp))

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(7.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFBEA1A1),
                        shape = RoundedCornerShape(7.dp)
                    )
                    .padding(3.dp)
                    .align(Alignment.Start)

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
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
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

            //money

            Spacer(modifier = Modifier.height(15.dp))

            Text( text = "৪। টাকা:",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.Start)

            )

            Spacer(modifier = Modifier.height(5.dp))

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(7.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFBEA1A1),
                        shape = RoundedCornerShape(7.dp)
                    )
                    .padding(3.dp)
                    .align(Alignment.Start)

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
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
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

                Spacer(modifier = Modifier.width(16.dp))

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


@Preview(showBackground = true)
@Composable
private fun DocumentRegistration() {

    val context = LocalContext.current

    var isReset = remember { mutableStateOf(false) }
    var isResultClicked = remember { mutableStateOf(false) }

    //data input---
    var documentType = remember { mutableStateOf("") }
    var divisionType = remember { mutableStateOf("") }
    var districtType = remember { mutableStateOf("") }
    var allianceType = remember { mutableStateOf("") }
    var buildingType = remember { mutableStateOf("") }
    var sellerType = remember { mutableStateOf("") }
    var documentPageCount = remember { mutableStateOf("") }
    var landValueCount = remember { mutableStateOf("") }
    var buildingValueCount = remember { mutableStateOf("") }

    //data from calculation map
    var regiValue = remember { mutableStateOf("০.০০") }
    var stampDutyValue = remember { mutableStateOf("০.০০") }
    var allianceValue = remember { mutableStateOf("০.০০") }
    var sourceLandTax125 = remember { mutableStateOf("০.০০") }
    var sourceLandTax126 = remember { mutableStateOf("০.০০") }
    var sourceBuildingTax125 = remember { mutableStateOf("০.০০") }
    var pageValue1 = remember { mutableStateOf("০.০০") }
    var pageValue2 = remember { mutableStateOf("০.০০") }
    var vatValue = remember { mutableStateOf("০.০০") }
    var stampValue = remember { mutableStateOf("০.০০") }
    var eFeeValue = remember { mutableStateOf("০.০০") }
    var courtFeeValue = remember { mutableStateOf("০.০০") }
    var totalValue = remember { mutableStateOf("০.০০") }


    val allType = if (buildingType.value == "হ্যাঁ"){

        (documentType.value.isNotEmpty() && divisionType.value.isNotEmpty() &&
                districtType.value.isNotEmpty() && allianceType.value.isNotEmpty() &&
                buildingType.value.isNotEmpty() && sellerType.value.isNotEmpty() &&
                documentPageCount.value.isNotEmpty() && landValueCount.value.isNotEmpty() &&
                buildingValueCount.value.isNotEmpty())

    }else{

        (documentType.value.isNotEmpty() && divisionType.value.isNotEmpty() &&
                districtType.value.isNotEmpty() && allianceType.value.isNotEmpty() &&
                buildingType.value.isNotEmpty() && sellerType.value.isNotEmpty() &&
                documentPageCount.value.isNotEmpty() && landValueCount.value.isNotEmpty())

    }

    LaunchedEffect(isReset.value) {

        if (isReset.value){

            //input data
            documentType.value = ""
            divisionType.value = ""
            districtType.value = ""
            allianceType.value = ""
            buildingType.value = ""
            sellerType.value = ""
            documentPageCount.value = ""
            landValueCount.value = ""
            buildingValueCount.value = ""

            //calculation data
            regiValue.value = "০.০০"
            stampDutyValue.value = "০.০০"
            allianceValue.value = "০.০০"
            sourceLandTax125.value = "০.০০"
            sourceLandTax126.value = "০.০০"
            sourceBuildingTax125.value = "০.০০"
            pageValue1.value = "০.০০"
            pageValue2.value = "০.০০"
            vatValue.value = "০.০০"
            stampValue.value = "০.০০"
            eFeeValue.value = "০.০০"
            courtFeeValue.value = "০.০০"
            totalValue.value = "০.০০"

            isResultClicked.value = false
            isReset.value = false

        }

    }

    //total calculation

    LaunchedEffect(isResultClicked.value) {

        if (allType && isResultClicked.value){

            val totalCalculation = documentRegistrationCalculation(
                document = documentType.value,
                alliance = allianceType.value,
                building = buildingType.value,
                seller = sellerType.value,
                documentPageCount = documentPageCount.value,
                landValue = landValueCount.value,
                buildingValue = buildingValueCount.value
            )

            regiValue.value = if (totalCalculation["regi"].toString() == "null" || totalCalculation["regi"].toString().isEmpty()) "০.০০" else totalCalculation["regi"].toString()
            stampDutyValue.value = if (totalCalculation["stampDuty"].toString() == "null" || totalCalculation["stampDuty"].toString().isEmpty()) "০.০০" else totalCalculation["stampDuty"].toString()
            vatValue.value = if (totalCalculation["vat"].toString() == "null" || totalCalculation["vat"].toString().isEmpty()) "০.০০" else totalCalculation["vat"].toString()
            allianceValue.value = if (totalCalculation["alliance"].toString() == "null" || totalCalculation["alliance"].toString().isEmpty()) "০.০০" else totalCalculation["alliance"].toString()
            pageValue1.value = if (totalCalculation["pageFee1"].toString() == "null" || totalCalculation["pageFee1"].toString().isEmpty()) "০.০০" else totalCalculation["pageFee1"].toString()
            pageValue2.value = if (totalCalculation["pageFee2"].toString() == "null" || totalCalculation["pageFee2"].toString().isEmpty()) "০.০০" else totalCalculation["pageFee2"].toString()
            sourceLandTax125.value = if (totalCalculation["sourceTax125"].toString() == "null" || totalCalculation["sourceTax125"].toString().isEmpty()) "০.০০" else totalCalculation["sourceTax125"].toString()
            sourceLandTax126.value = if (totalCalculation["sourceTax126"].toString() == "null" || totalCalculation["sourceTax126"].toString().isEmpty()) "০.০০" else totalCalculation["sourceTax126"].toString()
            sourceBuildingTax125.value = if (totalCalculation["sourceTaxBuilding125"].toString() == "null" || totalCalculation["sourceTaxBuilding125"].toString().isEmpty()) "০.০০" else totalCalculation["sourceTaxBuilding125"].toString()
            eFeeValue.value = if (totalCalculation["eFee"].toString() == "null" || totalCalculation["eFee"].toString().isEmpty()) "০.০০" else totalCalculation["eFee"].toString()
            courtFeeValue.value = if (totalCalculation["courtFee"].toString() == "null" || totalCalculation["courtFee"].toString().isEmpty()) "০.০০" else totalCalculation["courtFee"].toString()
            stampValue.value = if (totalCalculation["stamp"].toString() == "null" || totalCalculation["stamp"].toString().isEmpty()) "০.০০" else totalCalculation["stamp"].toString()
            totalValue.value = if (totalCalculation["total"].toString() == "null" || totalCalculation["total"].toString().isEmpty()) "০.০০" else totalCalculation["total"].toString()

        }

    }

    Box(

        modifier = Modifier
            .fillMaxWidth()

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(7.dp)
                .imePadding()

        ) {

            //document
            Text(text = "১। দলিলের ধরন বাছাই করুন",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
                )

            RegistrationSelectorHelper(
                documentVisible = true,
                item = { documentType.value = it },
                reset = isReset.value
            )

            Spacer(modifier = Modifier.height(14.dp))

            //division
            Text(text = "২। বিভাগ বাছাই করুন",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
            )

            RegistrationSelectorHelper(
                divisionsVisible = true,
                item = { divisionType.value = it },
                reset = isReset.value
            )

            Spacer(modifier = Modifier.height(14.dp))

            //document
            Text(text = "৩। জেলা বাছাই করুন",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
            )

            RegistrationSelectorHelper(
                districtVisible = true,
                item = { districtType.value = it },
                reset = isReset.value
            )

            Spacer(modifier = Modifier.height(14.dp))

            //document
            Text(text = "৪। স্থানীয় সরকার বাছাই করুন",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
            )

            RegistrationSelectorHelper(
               allianceVisible = true,
                item = { allianceType.value = it },
                reset = isReset.value
            )

            Spacer(modifier = Modifier.height(14.dp))

            //document
            Text(text = "৫। স্থাপনা আছে কি না?",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
            )

            RegistrationSelectorHelper(
                buildingVisible = true,
                item = { buildingType.value = it },
                reset = isReset.value
            )

            Spacer(modifier = Modifier.height(14.dp))

            //document
            Text(text = "৬। বিক্রেতার ধরন বাছাই করুন",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
            )

            RegistrationSelectorHelper(
                sellerVisible = true,
                item = { sellerType.value = it },
                reset = isReset.value
            )

            Spacer(modifier = Modifier.height(14.dp))

            //document count
            Text(text = "৭। দলিলের পৃষ্ঠা সংখ্যা",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
            )

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp)

            ) {

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(7.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFCEA5A5),
                            shape = RoundedCornerShape(7.dp)
                        )
                        .padding(5.dp)
                        .align(Alignment.Center)

                ) {

                    if (documentPageCount.value.isEmpty()){

                        Text(text = "০ পৃষ্ঠা",
                            fontSize = 14.sp,
                            fontFamily = Bangla.banglaFont(),
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF8D7070),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(start = 5.dp)
                                .align(Alignment.CenterStart)
                        )

                    }

                    BasicTextField(
                        value = documentPageCount.value,
                        onValueChange = { documentPageCount.value = it },
                        textStyle = TextStyle(fontSize = 14.sp, fontFamily = Bangla.banglaFont(), fontWeight = FontWeight.Normal, color = Color(0xFF000000)),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),

                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .align(Alignment.CenterStart)
                    )


                }//box

            }//box

            Spacer(modifier = Modifier.height(14.dp))

            //land value count
            Text(text = "৮। জমির মূল্য (টাকা)",
                fontSize = 15.sp,
                fontFamily = Bangla.banglaFont(),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = Color(0xFF000000),
            )

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp)

            ) {

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(7.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFCEA5A5),
                            shape = RoundedCornerShape(7.dp)
                        )
                        .padding(5.dp)
                        .align(Alignment.Center)

                ) {

                    if (landValueCount.value.isEmpty()){

                        Text(text = "০.০০ টাকা",
                            fontSize = 14.sp,
                            fontFamily = Bangla.banglaFont(),
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF8D7070),
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(start = 5.dp)
                                .align(Alignment.CenterStart)
                        )

                    }

                    BasicTextField(
                        value = landValueCount.value,
                        onValueChange = { landValueCount.value = it },
                        textStyle = TextStyle(fontSize = 14.sp, fontFamily = Bangla.banglaFont(), fontWeight = FontWeight.Normal, color = Color(0xFF000000)),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),

                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth(0.90f)
                            .padding(5.dp)
                            .align(Alignment.CenterStart)
                    )

                    if (landValueCount.value.isNotEmpty()){

                        IconButton(
                            onClick = { landValueCount.value = "" },
                            modifier = Modifier
                                .wrapContentWidth()
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

            }//box

            Spacer(modifier = Modifier.height(18.dp))

            if (buildingType.value == "হ্যাঁ"){

                // building value count
                Text(text = "৯। স্থাপনার মূল্য (টাকা)",
                    fontSize = 15.sp,
                    fontFamily = Bangla.banglaFont(),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    color = Color(0xFF000000),
                )

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(7.dp)

                ) {

                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(7.dp))
                            .border(
                                width = 1.dp,
                                color = Color(0xFFCEA5A5),
                                shape = RoundedCornerShape(7.dp)
                            )
                            .padding(5.dp)
                            .align(Alignment.Center)

                    ) {

                        if (buildingValueCount.value.isEmpty()){

                            Text(text = "০.০০ টাকা",
                                fontSize = 14.sp,
                                fontFamily = Bangla.banglaFont(),
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF8D7070),
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .padding(start = 5.dp)
                                    .align(Alignment.CenterStart)
                            )

                        }

                        BasicTextField(
                            value = buildingValueCount.value,
                            onValueChange = { buildingValueCount.value = it },
                            textStyle = TextStyle(fontSize = 14.sp, fontFamily = Bangla.banglaFont(), fontWeight = FontWeight.Normal, color = Color(0xFF000000)),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),

                            singleLine = true,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth(0.90f)
                                .padding(5.dp)
                                .align(Alignment.CenterStart)
                        )

                        if (buildingValueCount.value.isNotEmpty()){

                            IconButton(
                                onClick = { buildingValueCount.value = "" },
                                modifier = Modifier
                                    .wrapContentWidth()
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

                }//box

                Spacer(modifier = Modifier.height(18.dp))

            }//condition

            //buttons
            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)

            ) {

                Text(text = "রিসেট",
                    fontSize = 14.sp,
                    fontFamily = Bangla.banglaFont(),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF725858),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .clickable { isReset.value = true }
                        .background(color = Color(0xFFE3E1E1))
                        .padding(7.dp)
                        .align(Alignment.CenterVertically)
                    )

                Spacer(modifier = Modifier.width(18.dp))

                Text(text = "ফলাফল",
                    fontSize = 14.sp,
                    fontFamily = Bangla.banglaFont(),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .alpha(alpha = if (allType) 1f else 0.5f)
                        .clickable(
                            enabled = if (allType) true else false
                        ) {
                            isResultClicked.value = true
                            isReset.value = false

                        }
                        .background(color = Color(0xFF46B44A))
                        .padding(7.dp)
                        .align(Alignment.CenterVertically)
                )

            }//row

            Spacer(modifier = Modifier.height(18.dp))

            //result

            if (isResultClicked.value){

                val result = buildString {

                    append("| খাত | শতকরা হার | টাকা |\n")
                    append("|----|----|--------|\n")

                    //==================================
                    append("| রেজিস্ট্রেশন ফি | ১.৫% | ${regiValue.value} |\n")
                    append("| স্ট্যাম্প শুল্ক | ১% | ${stampDutyValue.value} |\n")
                    //==================================

                    if (allianceType.value == "সিটি কর্পোরেশন"){

                        append("| স্থানীয় সরকার কর | ২% | ${allianceValue.value} |\n")

                    }else{

                        append("| স্থানীয় সরকার কর | ৩% | ${allianceValue.value} |\n")

                    }

                    //====================================
                    append("| উৎস কর ভূমি (ধারা ১২৫) | ২% | ${sourceLandTax125.value} |\n")
                    //====================================

                    if (sellerType.value == "ডেভেলপার / কোম্পানি"){

                        append("| উৎস কর ভূমি (ধারা ১২৬) | ৫% | ${sourceLandTax126.value} |\n")

                    }else{

                        append("| উৎস কর ভূমি (ধারা ১২৬) | প্রযোজ্য নয় |  |\n")

                    }

                    if (buildingType.value == "হ্যাঁ" && buildingValueCount.value.isNotEmpty()){

                        append("| উৎস কর স্থাপনা (ধারা ১২৫) | ৬% | ${sourceBuildingTax125.value} |\n")

                    }else{

                        append("| উৎস কর স্থাপনা (ধারা ১২৫) | প্রযোজ্য নয় |  |\n")

                    }


                    if (sellerType.value != "মূল মালিক"){

                        append("| ভ্যাট | ২% | ${vatValue.value} |\n")

                    }else{

                        append("| ভ্যাট | প্রযোজ্য নয় |  |\n")

                    }


                    //====================================
                    append("| ই-ফি |  | ${eFeeValue.value} |\n")
                    //====================================

                    if (documentPageCount.value.isNotEmpty()){

                        append("| ঢ (এন) ফি (পৃষ্ঠা অনুযায়ী) | ২৪ টাকা/পৃষ্ঠা | ${pageValue1.value} |\n")
                        append("| ঢঢ (এনএন) ফি (পৃষ্ঠা অনুযায়ী) | ৩৬ টাকা/পৃষ্ঠা | ${pageValue2.value} |\n")

                    }else{

                        append("| ঢ (এন) ফি (পৃষ্ঠা অনুযায়ী) |  | ${pageValue1.value} |\n")
                        append("| ঢঢ (এনএন) ফি (পৃষ্ঠা অনুযায়ী) |  | ${pageValue2.value} |\n")

                    }


                    //================================
                    append("| হলফনামার স্ট্যাম্প |  | ${stampValue.value} |\n")
                    append("| নোটিশ / কোর্ট ফি |  | ${courtFeeValue.value} |\n")
                    append("| **মোট আনুমানিক খরচ** |  | **${totalValue.value}** |\n")

                }

                MarkdownText(
                    markdown = result,
                    fontSize = 14.sp,
                    fontResource = R.font.noto_serif_bengali,
                    color = Color(0xFF000000),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(15.dp))

            }

        }//column

    }//box

}//fun end

@Preview(showBackground = true)
@Composable
private fun RegistrationSelectorHelper(
    documentVisible : Boolean = false,
    divisionsVisible : Boolean = false,
    districtVisible : Boolean = false,
    allianceVisible : Boolean = false,
    buildingVisible : Boolean = false,
    sellerVisible : Boolean = false,
    item : (String) -> Unit = {},
    reset : Boolean = false
) {

    val lazyState = rememberLazyListState()


    var selectedIndex = remember { mutableStateOf(-1) }
    var isSelectorClicked = remember { mutableStateOf(false) }

    val divisionList = arrayOf(
        "ঢাকা বিভাগ",
        "চট্টগ্রাম বিভাগ",
        "রাজশাহী বিভাগ",
        "খুলনা বিভাগ",
        "সিলেট বিভাগ",
        "বরিশাল বিভাগ",
        "রংপুর বিভাগ",
        "ময়মনসিংহ বিভাগ")

    val districtList = arrayOf(
        "ঢাকা জেলা",
        "ফরিদপুর জেলা",
        "গাজীপুর জেলা",
        "গোপালগঞ্জ জেলা",
        "কিশোরগঞ্জ জেলা",
        "মাদারীপুর জেলা",
        "মানিকগঞ্জ জেলা",
        "মুন্সীগঞ্জ জেলা",
        "নারায়ণগঞ্জ জেলা",
        "নরসিংদী জেলা",
        "রাজবাড়ী জেলা",
        "শরীয়তপুর জেলা",
        "টাঙ্গাইল জেলা",
        "বাগেরহাট জেলা",
        "চুয়াডাঙ্গা জেলা",
        "যশোর জেলা",
        "ঝিনাইদহ জেলা",
        "খুলনা জেলা",
        "কুষ্টিয়া জেলা",
        "মাগুরা জেলা",
        "মেহেরপুর জেলা",
        "নড়াইল জেলা",
        "সাতক্ষীরা জেলা",
        "বান্দরবান জেলা",
        "ব্রাহ্মণবাড়িয়া জেলা",
        "চাঁদপুর জেলা",
        "চট্টগ্রাম জেলা",
        "কুমিল্লা জেলা",
        "কক্সবাজার জেলা",
        "ফেনী জেলা",
        "খাগড়াছড়ি জেলা",
        "লক্ষ্মীপুর জেলা",
        "নোয়াখালী জেলা",
        "রাঙ্গামাটি পার্বত্য জেলা",
        "বগুড়া জেলা",
        "জয়পুরহাট জেলা",
        "নওগাঁ জেলা",
        "নাটোর জেলা",
        "চাঁপাইনবাবগঞ্জ জেলা",
        "পাবনা জেলা",
        "রাজশাহী জেলা",
        "সিরাজগঞ্জ জেলা",
        "হবিগঞ্জ জেলা",
        "মৌলভীবাজার জেলা",
        "সুনামগঞ্জ জেলা",
        "সিলেট জেলা",
        "দিনাজপুর জেলা",
        "গাইবান্ধা জেলা",
        "কুড়িগ্রাম জেলা",
        "লালমনিরহাট জেলা",
        "নীলফামারী জেলা",
        "পঞ্চগড় জেলা",
        "রংপুর জেলা",
        "ঠাকুরগাঁও জেলা",
        "জামালপুর জেলা",
        "ময়মনসিংহ জেলা",
        "নেত্রকোণা জেলা",
        "শেরপুর জেলা",
        "ঝালকাঠি জেলা",
        "বরগুনা জেলা",
        "বরিশাল জেলা",
        "ভোলা জেলা",
        "পটুয়াখালী জেলা",
        "পিরোজপুর জেলা",
    )

    val documentList = arrayOf(
        "বিক্রয় দলিল",
        "দান দলিল",
        "হেবা দলিল",
        "বিনিময় দলিল",
        "বন্ধক দলিল",
        "ইজারা দলিল",
        "বায়না দলিল",
        "পাওয়ার অব অ্যাটর্নি",
        "উইল দলিল",
        "বণ্টন দলিল",
    )

    val allianceList = arrayOf(
        "সিটি কর্পোরেশন",
        "পৌরসভা",
        "ইউনিয়ন",
    )

    val buildingList = arrayOf("না", "হ্যাঁ")

    val sellerList = arrayOf(
        "মূল মালিক",
        "ডেভেলপার / কোম্পানি",
    )


    val maxItem = 10
    val itemHeight = 40.dp

    val divisionsVisibleItemCount = if (divisionList.size > maxItem) maxItem else divisionList.size

    val districtVisibleItemCount = if (districtList.size > maxItem) maxItem else districtList.size

    val documentVisibleItemCount = if (documentList.size > maxItem) maxItem else documentList.size

    val allianceVisibleItemCount = if (allianceList.size > maxItem) maxItem else allianceList.size

    val buildingVisibleItemCount = if (buildingList.size > maxItem) maxItem else buildingList.size

    val sellerVisibleItemCount = if (sellerList.size > maxItem) maxItem else sellerList.size

    LaunchedEffect(reset) {

        if (reset) selectedIndex.value = -1

    }

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp)

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()


        ) {

            //default value
            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(7.dp))
                    .clickable { isSelectorClicked.value = !isSelectorClicked.value }
                    .border(
                        width = 1.dp,
                        color = Color(0xFFCEA5A5),
                        shape = RoundedCornerShape(7.dp)
                    )
                    .padding(8.dp)

            ) {

                if (selectedIndex.value <= -1){

                    Text( text =

                        if (documentVisible){

                            "দলিল বাছাই করুন"

                        }else if (divisionsVisible){

                            "বিভাগ বাছাই করুন"

                        }else if (districtVisible){

                            "জেলা বাছাই করুন"

                        }else if (allianceVisible){

                            "এলাকা বাছাই করুন"

                        }else if (buildingVisible){

                            "হ্যাঁ / না"

                        }else {

                            "বিক্রেতার ধরন"

                        }
                        ,
                        fontSize = 14.sp,
                        fontFamily = Bangla.banglaFont(),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        color = Color(0xFF655353),
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterStart)

                    )

                }else{

                    Text( text =

                        if (documentVisible){

                            documentList[selectedIndex.value]

                        }else if (divisionsVisible){

                            divisionList[selectedIndex.value]

                        }else if (districtVisible){

                            districtList[selectedIndex.value]

                        }else if (allianceVisible){

                            allianceList[selectedIndex.value]

                        }else if (buildingVisible){

                            buildingList[selectedIndex.value]

                        }else{

                            sellerList[selectedIndex.value]

                        }
                        ,
                        fontSize = 14.sp,
                        fontFamily = Bangla.banglaFont(),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        color = Color(0xFF000000),
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterStart)

                    )

                }

                if (isSelectorClicked.value){

                    Icon( painter = painterResource(R.drawable.ic_right),
                        contentDescription = "",
                        tint = Color(0xFFAB8A8A),
                        modifier = Modifier
                            .wrapContentWidth()
                            .rotate(270f)
                            .align(Alignment.CenterEnd)

                    )

                }else{

                    Icon( painter = painterResource(R.drawable.ic_right),
                        contentDescription = "",
                        tint = Color(0xFFAB8A8A),
                        modifier = Modifier
                            .wrapContentWidth()
                            .rotate(90f)
                            .align(Alignment.CenterEnd)

                    )

                }

            }//box


            if (isSelectorClicked.value){

                LazyColumn(
                      modifier = Modifier
                          .fillMaxWidth()

                          .height(
                              height = if (documentVisible) {

                                  itemHeight * documentVisibleItemCount

                              } else if (divisionsVisible) {

                                  itemHeight * divisionsVisibleItemCount

                              } else if (districtVisible) {

                                  itemHeight * districtVisibleItemCount

                              } else if (allianceVisible) {

                                  itemHeight * allianceVisibleItemCount

                              } else if (buildingVisible) {

                                  itemHeight * buildingVisibleItemCount

                              } else {

                                  itemHeight * sellerVisibleItemCount

                              }
                          )

                          .shadow(elevation = 2.dp, shape = RoundedCornerShape(10.dp))
                          .clip(shape = RoundedCornerShape(10.dp))
                          .background(color = Color(0xFFFFFFFF))
                          .align(Alignment.CenterHorizontally),
                    state = lazyState,
                    contentPadding = PaddingValues(3.dp)

                ) {

                    itemsIndexed(
                        items = if (documentVisible){

                            documentList

                        }else if (divisionsVisible){

                            divisionList

                        }else if (districtVisible){

                            districtList

                        }else if (allianceVisible){

                            allianceList

                        }else if (buildingVisible){

                            buildingList

                        }else{

                            sellerList

                        }
                    ){index, item ->

                        Box(

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeight)
                                .clip(shape = RoundedCornerShape(12.dp))
                                .clickable {
                                    if (item.isNotEmpty()) item(item) else item("")
                                    selectedIndex.value = index
                                    isSelectorClicked.value = false
                                }
                                .padding(5.dp)

                        ) {

                            Text(text = item,
                                fontSize = 14.sp,
                                fontFamily = Bangla.banglaFont(),
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF000000),
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 7.dp)
                                    .align(Alignment.CenterStart)
                            )

                        }//box

                    }

                }//lazy

            }


        }//column

    }//box

}//fun end


private fun documentRegistrationCalculation(
    document : String,
    alliance : String,
    building : String,
    seller : String,
    documentPageCount : String,
    landValue : String,
    buildingValue : String
) : Map<String, String>{


    val map = mutableMapOf<String, String>()

    val regiFee = 0.01
    val stampDuty = 0.015
    val allianceCount = if (alliance == "সিটি কর্পোরেশন") 0.02 else 0.03
    val pageFee1 = 24.00
    val pageFee2 = 36.00
    val eFee = 100.00
    val courtFee = 10.00
    val stamp = 300.00
    val vat = if (seller == "মূল মালিক") 0.0 else 0.02
    val sourceTaxLand126 = if (seller == "ডেভেলপার / কোম্পানি") 0.05 else 0.0
    val sourceTaxLand125 = 0.02
    val sourceTaxBuilding125 = if (building == "হ্যাঁ") 0.06 else 0.0

    val remaining = landValue.toDoubleOrNull() ?: 0.0
    val d_documentRageCount = documentPageCount.toDoubleOrNull() ?: 0.0
    val d_buildingValue = buildingValue.toDoubleOrNull() ?: 0.0

    val _regi = (remaining * regiFee)
    map["regi"] = Bangla.banglaNumber(_regi.toString())


    val _stampDuty = (remaining * stampDuty)
    map["stampDuty"] = Bangla.banglaNumber(_stampDuty.toString())

    val _vat = (remaining * vat)
    map["vat"] = Bangla.banglaNumber(_vat.toString())


    val _alliance = (remaining * allianceCount)
    map["alliance"] = Bangla.banglaNumber(_alliance.toString())


    val _page1 = (pageFee1 * d_documentRageCount)
    map["pageFee1"] = Bangla.banglaNumber(_page1.toString())


    val _page2 = (pageFee2 * d_documentRageCount)
    map["pageFee2"] = Bangla.banglaNumber(_page2.toString())


    val _tax125 = (remaining * sourceTaxLand125)
    map["sourceTax125"] = Bangla.banglaNumber(_tax125.toString())


    val _tax126 = (remaining * sourceTaxLand126)
    map["sourceTax126"] = Bangla.banglaNumber(_tax126.toString())

    val _btax125 = (d_buildingValue * sourceTaxBuilding125)
    map["sourceTaxBuilding125"] = Bangla.banglaNumber(_btax125.toString())


    map["eFee"] = Bangla.banglaNumber(eFee.toString())

    map["courtFee"] = Bangla.banglaNumber(courtFee.toString())

    map["stamp"] = Bangla.banglaNumber(stamp.toString())


    if (buildingValue.isNotEmpty()){

        val _total = (_regi + _stampDuty + _alliance + _page1 + _page2 + _tax125 + _tax126 + _btax125 + eFee + courtFee + stamp + _vat)
        map["total"] = Bangla.banglaNumber(_total.toString())

    }else{

        val _total = (_regi + _stampDuty + _alliance + _page1 + _page2 + _tax125 + _tax126 + eFee + courtFee + stamp + _vat)
        map["total"] = Bangla.banglaNumber(_total.toString())

    }



/* val documentMap = mapOf(

        "বিক্রয় দলিল" to 3.0,
        "দান দলিল" to 1.0,
        "হেবা দলিল" to 0.5,
        "বিনিময় দলিল" to 3.0,
        "বন্ধক দলিল" to 1.0,
        "ইজারা দলিল" to 1.5,
        "বায়না দলিল" to 1.0,
        "পাওয়ার অব অ্যাটর্নি" to 1.0,
        "উইল দলিল" to 0.0,
        "বণ্টন দলিল" to 1.0,

    )*/

   return map

}//fun end