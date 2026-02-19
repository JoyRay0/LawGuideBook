package com.rk_softwares.lawguidebook.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.rk_softwares.lawguidebook.Activity.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.Activity.theme_main.LightNav
import com.rk_softwares.lawguidebook.Activity.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.Activity.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.Database.BookmarkDatabase
import com.rk_softwares.lawguidebook.Database.HistoryDatabase
import com.rk_softwares.lawguidebook.Helper.BanglaFont
import com.rk_softwares.lawguidebook.Helper.IntentHelper
import com.rk_softwares.lawguidebook.Helper.InternetChecker
import com.rk_softwares.lawguidebook.Helper.KeyHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.Model.GridList
import com.rk_softwares.lawguidebook.Model.ItemList
import com.rk_softwares.lawguidebook.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Act_home : ComponentActivity() {

    private lateinit var historyDB : HistoryDatabase

    private lateinit var bookmarkDatabase: BookmarkDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            init()

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = false
            )

            val searchList = remember { mutableStateListOf<ItemList>() }
            val historyList = remember { mutableStateListOf<ItemList>() }
            var isSearchScreenList by remember { mutableStateOf(true) }
            var inputFiledData by remember { mutableStateOf("") }
            val list = remember { mutableStateListOf<GridList>() }
            val bookmarkQuestionList = remember { mutableStateListOf<ItemList>() }
            val scope = rememberCoroutineScope()
            var bookmarkReloadDB by remember { mutableIntStateOf(0) }
            var historyReloadDB by remember { mutableIntStateOf(0) }

            list.clear()
            list.add(GridList(

                image = "https://rksoftwares.fun/All_app/cdn/images/ad.png",
                title = "আইন"
            ))

            list.add(GridList(

                image = "https://rksoftwares.fun/All_app/cdn/images/education.png",
                title = "পুলিশ স্টেশন"
            ))

            list.add(GridList(

                image = "https://rksoftwares.fun/All_app/cdn/images/agriculture.png",
                title = "জমি"
            ))

            list.add(GridList(

                image = "https://rksoftwares.fun/All_app/cdn/images/food.png",
                title = "লোডিং"
            ))

            LaunchedEffect(bookmarkReloadDB) {

                val item = withContext(Dispatchers.IO){

                    bookmarkDatabase.getAll()

                }

                bookmarkQuestionList.clear()
                bookmarkQuestionList.addAll(item)

            }

            if (isSearchScreenList){

                searchList.clear()
                searchList.add(ItemList(question = "সরণ কাকে বলে? কত প্রকার ও কি কি?"))
                searchList.add(ItemList(question = "কেন্দ্রীয় প্রবণতা কাকে বলে? কেন এটি পরিসংখ্যানের প্রাণকেন্দ্র?"))
                searchList.add(ItemList(question = "দর্শন কাকে বলে? কত প্রকার ও কি কি?"))
                searchList.add(ItemList(question = "রাষ্ট্রবিজ্ঞান কাকে বলে?"))
                searchList.add(ItemList(question = "বিশ্বের সর্বকালের সেরা ফুটবলার কে? ২০২৫"))
                searchList.add(ItemList(question = "বাক্য কাকে বলে? বাক্যের প্রকারভেদ?"))
                searchList.add(ItemList(question = "কারক ও বিভক্তি কাকে বলে?"))

            }else{

                LaunchedEffect(historyReloadDB) {

                    val historyItem = withContext(Dispatchers.IO){

                        historyDB.getAll()

                    }

                    historyList.clear()
                    historyList.addAll(historyItem)

                }

            }

            if (inputFiledData.isNotEmpty()){

                LaunchedEffect(inputFiledData) {

                    withContext(Dispatchers.IO){

                        historyDB.inset(inputFiledData)

                    }

                }

            }

            val internet = InternetChecker.liveInternetStatus(this)

            Toast.makeText(this,if (internet) "On" else "Off", Toast.LENGTH_SHORT).show()

            var isHome by remember { mutableStateOf(false) }

            LawGuideBookTheme {

                HomeFullScreen(
                    searchList = searchList,
                    historyList = historyList,
                    searchScreenBool = { isSearchScreenList = it },
                    searchInput = { inputFiledData = it },
                    searchClick = {
                        historyReloadDB++
                    },
                    historyTitleClick = {},
                    searchItemClick = {

                        IntentHelper.dataIntent(
                            this,
                            Act_answer::class.java,
                            KeyHelper.sendQuestion_IntentKey(),
                            it
                        )

                    },
                    gridClick = {

                        IntentHelper.dataIntent(
                            this,
                            Act_question::class.java,
                            KeyHelper.sendTitle_IntentKey(),
                            it
                        )


                    },
                    gridList = list,
                    settingClick = {

                        startActivity(Intent(this, Act_setting::class.java))
                        finishAffinity()
                    },
                    bookmarkClick = { item ->

                        scope.launch {

                            withContext(Dispatchers.IO){

                                bookmarkDatabase.insert(item)

                            }
                            bookmarkReloadDB++

                            Toast.makeText(this@Act_home, "সেভ হয়েছে", Toast.LENGTH_SHORT).show()

                        }

                    },

                    bookmarkList = bookmarkQuestionList,
                    bookmarkQuestionClick = {

                        IntentHelper.dataIntent(
                            this,
                            Act_answer::class.java,
                            KeyHelper.sendQuestion_IntentKey(),
                            it
                        )

                    },

                    deleteBookmarkClick = { item ->

                        scope.launch {

                           val deleted = withContext(Dispatchers.IO){

                               bookmarkDatabase.deleteOne(item)

                            }

                            if (deleted){

                                bookmarkReloadDB++

                                Toast.makeText(this@Act_home, "ডিলিট হয়েছে", Toast.LENGTH_SHORT).show()

                            }

                        }

                    },
                    aiChatClick = {

                        IntentHelper.normalIntent(this, Act_ai_chat::class.java)

                    }


                )

            }

        }
    }// on create===========================================

    private fun init(){

        historyDB = HistoryDatabase(this)

        bookmarkDatabase = BookmarkDatabase(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        historyDB.closeDB()
        bookmarkDatabase.closeDB()
    }

}// class====================================================


@Preview(showBackground = true)
@Composable
private fun HomeFullScreen(
    searchList : MutableList<ItemList> = mutableListOf(),
    historyList : MutableList<ItemList> = mutableListOf(),
    searchScreenBool: (Boolean) -> Unit = {},
    searchInput: (String) -> Unit = {},
    searchClick: () -> Unit = {},
    historyTitleClick: () -> Unit = {},
    searchItemClick: (String) -> Unit = {},
    gridClick: (String) -> Unit = {},
    gridList : List<GridList> = emptyList(),
    settingClick : () -> Unit = {},
    bookmarkClick: (String) -> Unit = {},
    bookmarkList: List<ItemList> = emptyList(),
    bookmarkQuestionClick: (String) -> Unit = {},
    deleteBookmarkClick : (String) -> Unit = {},
    aiChatClick: () -> Unit = {}
    ) {

    var screen by remember { mutableIntStateOf(0) }

    Scaffold(

        topBar = { Toolbar(
            settingClick = {settingClick()}
        )},
        bottomBar = { BottomNav(  screenIndex = { screen = it }) },
        modifier = Modifier.fillMaxSize())

    { innerPadding ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {

            when(screen){       //navigation switch

                0 -> HomeScreen(
                    aiChatClick = {aiChatClick()}
                )
                1 -> ListScreen(
                    gridClick = { gridClick(it) },
                    list = gridList
                )
                2 -> SearchScreen(
                    searchList = searchList,
                    historyList = historyList,
                    searchScreenBool = { searchScreenBool( it ) },
                    searchInput = { searchInput(it) },
                    searchClick = { searchClick()},
                    historyTitleClick = {historyTitleClick()},
                    searchItemClick = {searchItemClick(it)},
                    bookmarkClick = { bookmarkClick(it) }

                )
                3 -> BookmarkScreen(
                    bookmarkList = bookmarkList,
                    titleClick = { bookmarkQuestionClick(it) },
                    deleteClick = { deleteBookmarkClick(it) }
                )

            }

        }//box
        

    }//scaffold

}//fun end


@Preview(showBackground = true)
@Composable
private fun Toolbar(

    settingClick: () -> Unit = {}

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

            Text("আইনি গাইডবুক",
                fontSize = 17.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFFFFF),
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(10.dp)
                    .align(Alignment.CenterStart)
                )

            Row(

                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterEnd)

            ) {

                IconButton(
                    onClick = { settingClick() },
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(shape = CircleShape)
                        //.background(color = Color(0xFF00BCD4))
                        .size(32.dp)
                        .align(Alignment.CenterVertically)

                ) {

                    Icon( painter = painterResource(R.drawable.ic_setting),
                        contentDescription = "Setting",
                        tint = Color(0xFFFFFFFF),
                        modifier = Modifier
                            .wrapContentWidth()
                            .size(19.dp)
                            .align(Alignment.CenterVertically)

                    )

                }

                Spacer(modifier = Modifier.width(5.dp))

            }//row

        }//box

    }//box

}//fun end


@Preview(showBackground = true)
@Composable
private fun BottomNav(
    screenIndex : (Int) -> Unit = {}

) {

    var selectedIndex by remember { mutableIntStateOf(0) }

    //bottom
    Row(

        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(elevation = 3.dp)
            .background(color = Color(0xFFFFFFFF)),
        horizontalArrangement = Arrangement.SpaceAround

    ) {

        val itemText = arrayOf("হোম", "লিস্ট", "সার্চ", "বুকমার্ক")
        val icons = arrayOf(R.drawable.ic_home, R.drawable.ic_list, R.drawable.ic_search, R.drawable.ic_bookmark_fill)

        itemText.forEachIndexed { index, it ->

            BottomNavHelper(
                modifier = Modifier
                    .wrapContentWidth()
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                selected =  selectedIndex == index ,
                navClick = {
                    selectedIndex = index
                    screenIndex(index)
                           },
                icon = icons[index],
                labelText = it

            )

        }

    }//row


}//fun end


@Composable
private fun BottomNavHelper(
    modifier: Modifier = Modifier,
    selected : Boolean = true,
    navClick : () -> Unit = {},
    icon : Int,
    labelText : String = "Home"

) {

    Box(

        modifier = modifier
            .fillMaxSize()

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Icon( painter = painterResource(icon),
                contentDescription = labelText,
                tint = if (selected) Color(0xFFCC3AE5) else Color(0xFF796565),
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(shape = RoundedCornerShape(17.dp))
                    .clickable { navClick() }
                    .background(color = if (selected) Color(0xFFB5E9FF) else Color.Transparent)
                    .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
                    .size(22.dp)
                    .align(Alignment.CenterHorizontally)
            )

            //Spacer(modifier = Modifier.height(2.dp))

            Text( text = labelText,
                fontSize = 13.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color(0xFFCC3AE5) else Color.Gray,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                modifier = Modifier
                    .wrapContentWidth()
                    //.padding(2.dp)
                    .align(Alignment.CenterHorizontally)

            )

        }//box

    }//box
    
}//fun end


@Preview(showBackground = true)
@Composable
private fun HomeScreen(
    aiChatClick: () -> Unit = {}
) {

    Box(

        modifier = Modifier
            .fillMaxSize()

    ) {

        Column(

            modifier = Modifier
                .fillMaxHeight()

        ) {

            AiChatBot(aiChatClick = aiChatClick)

        }//column

    }//box
    

}//fun end

@Preview(showBackground = true)
@Composable
private fun AiChatBot(
    aiChatClick : () -> Unit = {}
) {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp)

    ) {
        
        Row(

            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(14.dp))
                .clickable{ aiChatClick() }
                .border(width = (1.5).dp, color = Color(0xFF2196F3), shape = RoundedCornerShape(14.dp))
                .padding(10.dp)


        ) {

            Image( painter = painterResource(R.drawable.ic_ai_chat),
                contentDescription = "Ai",
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(text = "আপনার আইনি সহায়ক AI",
                fontSize = 15.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF494343),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically)
                )

        }//row
        
    }//box

}//fun end




@Preview(showBackground = true)
@Composable
private fun ListScreen(
    list: List<GridList> = emptyList(),
    gridClick : (String) -> Unit = {}
) {


    Box(

        modifier = Modifier.fillMaxSize()

    ) {

        val lazyState = rememberLazyGridState()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(5.dp),
            state = lazyState,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            
           items(
               items = list,
               key = { it.title }
           ){ it ->

               ListGridHelper(
                   gridImageUrl = it.image,
                   gridText = it.title,
                   onGridClick = { gridClick(it.title) }
               )

           }

        }

    }//box

}//fun end

@Preview(showBackground = true)
@Composable
private fun ListGridHelper(
    gridImageUrl : String = "",
    gridText : String = "Title",
    onGridClick : () -> Unit = {}

) {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(14.dp))
                .clip(shape = RoundedCornerShape(14.dp))
                .clickable { onGridClick() }
                .background(color = Color(0xFFFFFFFF))

                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Spacer(modifier = Modifier.height(5.dp))

            //image
            Box(

                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .padding(5.dp)
                    .align(Alignment.CenterHorizontally)

            ) {

                AsyncImage( model = gridImageUrl,
                    contentDescription = "Image",
                    placeholder = painterResource(R.drawable.img_loading),
                    error = painterResource(R.drawable.img_loading),
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)

                    )

            }//box

            Text(text = gridText,
                fontSize = 14.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .align(Alignment.CenterHorizontally)
                )


        }//column

    }//box

}//fun end


@Preview(showBackground = true)
@Composable
private fun SearchScreen(
    searchList : MutableList<ItemList> = mutableListOf(),
    historyList : MutableList<ItemList> = mutableListOf(),
    searchScreenBool : (Boolean) -> Unit = {},
    searchInput : (String) -> Unit = {},
    searchClick: () -> Unit = {},
    historyTitleClick: () -> Unit = {},
    searchItemClick : (String) -> Unit = {},
    bookmarkClick: (String) -> Unit = {}
    ) {

    val lazyState = rememberLazyListState()
    var isSearchDataVisible by remember { mutableStateOf(true) }
    val historyTitle = remember { mutableStateOf("") }


    Box(

        modifier = Modifier.fillMaxSize()

    ) {

        LazyColumn(

            state = lazyState,
            contentPadding = PaddingValues(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)

        ) {

            item {

                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp))

            }

            if (isSearchDataVisible){

                items(
                    items = searchList,
                    key = { it. question}
                ){ it ->

                    QuestionItem(
                        title = it.question,
                        titleClick = { searchItemClick(it.question) },
                        bookmarkClick = { bookmarkClick(it.question) },
                        blockBookmarkIcon = "visible"

                    )

                }

            }else{

                items(
                    items = historyList,
                    key = { it. question}
                ){ it ->

                    HistoryItem(
                        title = it.question,
                        titleData = { historyTitle.value = it },
                        titleClick = {
                            historyTitleClick()
                            isSearchDataVisible = true
                        }
                    )

                }

            }

        }

        searchScreenBool(isSearchDataVisible) //sending list boolean to activity

        if (searchList.isEmpty() && isSearchDataVisible){

            Image( painter = painterResource(R.drawable.img_search),
                contentDescription = "Search",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(130.dp)
                    .align(Alignment.Center)

            )

        }

        if (historyList .isEmpty() && !isSearchDataVisible){

            Text("কোন হিস্ট্রি নেই।",
                fontSize = 17.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )

        }

        LaunchedEffect(isSearchDataVisible) {

           if (!isSearchDataVisible){

               searchList.clear()

           }else{

               historyList.clear()

           }

        }

        SearchBarHelper(
            search = { searchInput(it) },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            historyClick = { isSearchDataVisible = !isSearchDataVisible },
            historyTitle = historyTitle,
            searchClick = {searchClick()}
        )

    }//box

}//fun end


@Preview(showBackground = true)
@Composable
private fun SearchBarHelper(
    search: (String) -> Unit = {},
    historyClick: () -> Unit = {},
    historyTitle: MutableState<String> = mutableStateOf(""),
    modifier: Modifier = Modifier,
    searchClick : () -> Unit = {}
) {

    var searchFiled by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current


    Box(

        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)

        ) {

            Box(

                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp))
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(color = Color(0xFFFFFFFF))
                    .padding(5.dp)
                    .align(Alignment.CenterVertically)
                    .imePadding()

            ) {

                Row(

                    modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .padding(7.dp)
                        .align(Alignment.CenterStart)

                ) {

                    Icon( painter = painterResource(R.drawable.ic_search2),
                        contentDescription = "Search",
                        tint = Color(0xFF000000),
                        modifier = Modifier
                            .wrapContentWidth()
                            .size(26.dp)
                            .align(Alignment.CenterVertically)

                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(

                        modifier = Modifier
                            .fillMaxWidth()

                            .align(Alignment.CenterVertically)

                    ) {

                        if (searchFiled.isEmpty()){

                            Text("যেকোনো আইনি বিষয় সার্চ করুন",
                                fontSize = 15.sp,
                                fontFamily = BanglaFont.font(),
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF695D5D),
                                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .align(Alignment.CenterStart)
                            )

                        }

                        BasicTextField(
                            value = searchFiled,
                            onValueChange = { searchFiled = it },
                            textStyle = TextStyle(fontSize = 15.sp, fontFamily = BanglaFont.font(), fontWeight = FontWeight.Normal, color = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterStart),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search,
                                keyboardType = KeyboardType.Text,
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {

                                    searchClick()

                                    if (searchFiled.isNotEmpty()) search(searchFiled) //sending search data to other functions

                                    keyboardController?.hide()

                                }
                            )
                        )

                    }//box


                }//row

                if (searchFiled.isNotEmpty()){

                    IconButton(
                        onClick = {
                            searchFiled = ""
                            historyTitle.value = ""
                                  },
                        modifier = Modifier
                            .wrapContentWidth()
                            .clip(shape = CircleShape)
                            //.background(color = Color.Blue)
                            .size(30.dp)
                            .align(Alignment.CenterEnd)

                    ) {

                        Icon( painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Close",
                            tint = Color.DarkGray,
                            modifier = Modifier
                                .wrapContentWidth()
                                .size(20.dp)
                                .align(Alignment.Center)

                        )

                    }

                }


            }//box

            Spacer(modifier = Modifier.width(5.dp))

            Box(

                modifier = Modifier
                    .wrapContentWidth()
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp))
                    .clip(shape = RoundedCornerShape(12.dp))
                    .clickable { historyClick() }
                    .background(color = Color(0xFFFFFFFF))
                    .padding(12.dp)
                    .align(Alignment.CenterVertically)

            ) {

                Icon( painter = painterResource(R.drawable.ic_history),
                    contentDescription = "History",
                    tint = Color(0xFF000000),
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(26.dp)
                        .align(Alignment.Center)

                )

            }//box
            
        }//row

        if (historyTitle.value.isNotEmpty()) searchFiled = historyTitle.value

    }//box


}//fun end


@Preview(showBackground = true)
@Composable
private fun QuestionItem(
    modifier: Modifier = Modifier,
    title : String = "Title",
    titleClick : () -> Unit = {},
    bookmarkClick : () -> Unit = {},
    deleteClick : () -> Unit = {},
    blockBookmarkIcon : String = "",
    deleteIcon : String = ""
) {

    var isIconVisible by remember { mutableStateOf(false) }

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

                        if (blockBookmarkIcon.isNotEmpty()) {

                            isIconVisible = true

                        } else if (deleteIcon.isNotEmpty()) {

                            isIconVisible = true

                        } else {

                            isIconVisible = false

                        }

                    },

                    onClick = {
                        isIconVisible = false
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

            if (isIconVisible){

                IconButton(
                    onClick = {

                        if (blockBookmarkIcon.isNotEmpty()){

                            bookmarkClick()
                        }else{

                            deleteClick()
                        }

                        isIconVisible = false
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(shape = CircleShape)
                        .size(30.dp)
                        .align(Alignment.CenterEnd)

                ) {

                    Icon(
                        painter = painterResource(

                            if (deleteIcon.isNotEmpty()) {
                                R.drawable.ic_delete
                            }else{
                                R.drawable.ic_bookmark
                            }

                        ),
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


@Preview(showBackground = true)
@Composable
private fun HistoryItem(
    modifier: Modifier = Modifier,
    title : String = "Title",
    titleData : (String) -> Unit = {},
    titleClick : () -> Unit = {},
) {

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

                    onClick = {

                        titleClick()
                        if (title.isNotEmpty()) titleData(title)

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

            Icon( painter = painterResource(R.drawable.ic_arrow),
                contentDescription = "Right",
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
private fun BookmarkScreen(
    bookmarkList: List<ItemList> = emptyList(),
    titleClick: (String) -> Unit = {},
    deleteClick: (String) -> Unit = {}
) {

    val lazyState = rememberLazyListState()

    Box(

        modifier = Modifier
            .fillMaxSize()

    ) {

        Box(

            modifier = Modifier
                .fillMaxSize()

        ) {

            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxWidth()

            ) {


                items(
                    items = bookmarkList,
                    key = { it.question }
                ){ item ->

                    QuestionItem(
                        title = item.question,
                        titleClick = { titleClick(item.question) },
                        deleteClick = { deleteClick(item.question) },
                        deleteIcon = "visible"
                    )

                }

            }

            if (bookmarkList.isEmpty()){

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)

                ) {

                    Image( painter = painterResource(R.drawable.img_empty_folder),
                        contentDescription = "Empty",
                        modifier = Modifier
                            .wrapContentWidth()
                            .size(125.dp)
                            .align(Alignment.CenterHorizontally)

                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("কোন বুকমার্ক নেই।",
                        fontSize = 18.sp,
                        fontFamily = BanglaFont.font(),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterHorizontally)
                    )

                }

            }

        }//box

    }//box
    
}//fun end