package com.rk_softwares.lawguidebook.View

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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.rk_softwares.lawguidebook.View.theme_main.LawGuideBookTheme
import com.rk_softwares.lawguidebook.View.theme_main.LightNav
import com.rk_softwares.lawguidebook.View.theme_main.LightStatusBar
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBar
import com.rk_softwares.lawguidebook.Database.BookmarkDatabase
import com.rk_softwares.lawguidebook.Database.HistoryDatabase
import com.rk_softwares.lawguidebook.Helper.BanglaFont
import com.rk_softwares.lawguidebook.Helper.ComposeHelper
import com.rk_softwares.lawguidebook.Helper.IntentHelper
import com.rk_softwares.lawguidebook.Helper.InternetChecker
import com.rk_softwares.lawguidebook.Helper.InternetStatus
import com.rk_softwares.lawguidebook.Helper.KeyHelper
import com.rk_softwares.lawguidebook.Helper.ShortMessageHelper
import com.rk_softwares.lawguidebook.Helper.ThemeHelper
import com.rk_softwares.lawguidebook.Model.Items
import com.rk_softwares.lawguidebook.Presenter.Home
import com.rk_softwares.lawguidebook.Presenter.HomePresenter
import com.rk_softwares.lawguidebook.R
import com.rk_softwares.lawguidebook.View.theme_main.LightToolBarIcon

class Act_home : ComponentActivity(), Home, InternetStatus {

    private lateinit var historyDB : HistoryDatabase
    private lateinit var bookmarkDatabase: BookmarkDatabase
    private lateinit var presenter: HomePresenter

    private lateinit var internetChecker: InternetChecker

    //init-----
    private var isInternet = mutableStateOf(false)
    private val searchList = mutableStateListOf<Items>()
    private val historyList = mutableStateListOf<Items>()
    private val categoryList = mutableStateListOf<Items>()
    private val bookmarkList = mutableStateListOf<Items>()
    private val calculationList = mutableStateListOf<Items>()
    private var serverStatus = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ThemeHelper.SystemUi(
                statusBarColor = LightStatusBar,
                navColor = LightNav,
                darkIcons = true
            )

            init()

            internetChecker.onStart()

            var isSearchScreenList by remember { mutableStateOf(true) }
            var historyData by remember { mutableIntStateOf(0) }

            LaunchedEffect(historyData) {

                presenter.getAllHistory()

            }


            LawGuideBookTheme {

                HomeFullScreen(
                    searchList = searchList,
                    historyList = historyList,
                    searchScreenBool = { isSearchScreenList = it },

                    searchClick = {
                        presenter.searchAndHistoryToServer(it)

                        historyData++
                                  },
                    historyTitleClick = { presenter.searchAndHistoryToServer(it) },
                    historyClick = {

                        historyData++ },
                    searchItemClick = {

                        IntentHelper.dataIntent(
                            this,
                            Act_answer::class.java,
                            KeyHelper.sendQuestion_IntentKey(),
                            it
                        )

                    },
                    gridClick = { it ->

                        val intent = Intent(this, Act_question::class.java)
                        intent.putExtra(KeyHelper.sendTitle_IntentKey(), it.title)
                        intent.putExtra(KeyHelper.sendTableName_IntentKey(), it.tableName)
                        startActivity(intent)


                    },
                    gridList = categoryList,
                    settingClick = {

                        startActivity(Intent(this, Act_setting::class.java))
                        finishAffinity()
                    },
                    searchBookmarkClick = { item ->

                        presenter.insertBookmark(item)

                    },

                    bookmarkList = bookmarkList,
                    bookmarkQuestionClick = {

                        IntentHelper.dataIntent(
                            this,
                            Act_answer::class.java,
                            KeyHelper.sendQuestion_IntentKey(),
                            it
                        )

                    },

                    deleteBookmarkClick = { item ->

                        presenter.deleteOneBookmarkItem(item)

                    },
                    aiChatClick = {

                        IntentHelper.normalIntent(this, Act_ai_chat::class.java)

                    },
                    aiChatMoreClick = {

                        IntentHelper.normalIntent(this, Act_calculation_item::class.java)

                    },
                    internet = isInternet.value,
                    navCategoryClick = {
                        presenter.categoryItemFromServer()
                        historyList.clear()
                        searchList.clear()
                        bookmarkList.clear()
                                       },
                    navHomeClick = {

                        //presenter.homeItemFromServer()
                        presenter.calculationLimitItemFromServer()
                        historyList.clear()
                        searchList.clear()
                        bookmarkList.clear()

                    },
                    navBookmark = {
                        presenter.getAllBookmark()
                        historyList.clear()
                        searchList.clear()
                        bookmarkList.clear()
                                  },
                    serverStatus = serverStatus.value,
                    categoryRetryClick = {
                        presenter.categoryItemFromServer()
                    },
                    lawWebsiteClick = { IntentHelper.normalIntent(this, Act_lawwebsites::class.java) },
                    calculationList = calculationList,
                    calculationClick = {

                        IntentHelper.dataIntent(this, Act_calculation::class.java, KeyHelper.calculationTitle_IntentKey(), it)

                    }

                )

            }

        }
    }// on create===========================================

    private fun init(){

        historyDB = HistoryDatabase(this)

        bookmarkDatabase = BookmarkDatabase(this)

        presenter = HomePresenter(this, historyDB, bookmarkDatabase)

        internetChecker = InternetChecker(this, this)

    }

    override fun onDestroy() {
        super.onDestroy()
        historyDB.closeDB()
        bookmarkDatabase.closeDB()
        presenter.onDestroy()
        internetChecker.onStop()
    }

    override fun onHistoryList(list: List<Items>) {
        historyList.clear()
        historyList.addAll(list)
    }

    override fun onSearchList(list: List<Items>) {
        searchList.clear()
        searchList.addAll(list)
    }

    override fun onCategoryList(list: List<Items>) {
        categoryList.clear()
        categoryList.addAll(list)
    }

    override fun onBookmarkList(list: List<Items>) {
        bookmarkList.clear()
        bookmarkList.addAll(list)
    }

    override fun onCalculationList(list: List<Items>) {
        calculationList.clear()
        calculationList.addAll(list)
    }

    override fun serverStatus(message: String) {
        serverStatus.value = message
        ShortMessageHelper.toast(this, message)
    }

    override fun message(status: String) {
        ShortMessageHelper.toast(this, status)
    }

    override fun isInternet(internet: Boolean) {
        isInternet.value = internet
    }

}// class====================================================


@Preview(showBackground = true)
@Composable
private fun HomeFullScreen(
    searchList : MutableList<Items> = mutableListOf(),
    historyList : MutableList<Items> = mutableListOf(),
    searchScreenBool: (Boolean) -> Unit = {},
    searchClick: (String) -> Unit = {},
    historyTitleClick: (String) -> Unit = {},
    historyClick: () -> Unit = {},
    searchItemClick: (String) -> Unit = {},
    gridClick: (Items) -> Unit = {},
    gridList : List<Items> = emptyList(),
    settingClick : () -> Unit = {},
    searchBookmarkClick: (String) -> Unit = {},
    bookmarkList: List<Items> = emptyList(),
    bookmarkQuestionClick: (String) -> Unit = {},
    deleteBookmarkClick : (String) -> Unit = {},
    aiChatClick: () -> Unit = {},
    aiChatMoreClick: () -> Unit = {},
    internet: Boolean = false,
    navCategoryClick : () -> Unit = {},
    navHomeClick : () -> Unit = {},
    navBookmark : () -> Unit = {},
    serverStatus : String = "",
    categoryRetryClick: () -> Unit = {},
    lawWebsiteClick: () -> Unit = {},
    calculationList: List<Items> = emptyList(),
    calculationClick : (String) -> Unit = {}
    ) {

    var screen by remember { mutableIntStateOf(0) }
    var isInternetDialogVisible by remember { mutableStateOf(false) }

    if (internet) isInternetDialogVisible = false else isInternetDialogVisible = true

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

            LaunchedEffect(screen) {

                when (screen) {

                    0 -> { navHomeClick() }
                    1 -> { navCategoryClick() }
                    3 -> { navBookmark() }
                }

            }
            
            when(screen){       //navigation switch

                0 ->
                    HomeScreen(
                        aiChatClick = { aiChatClick() },
                        moreClick = { aiChatMoreClick() },
                        calculationClick = { calculationClick(it) },
                        calculationList = calculationList,
                        lawWebsiteClick = { lawWebsiteClick() },
                        status = serverStatus
                        )

                1 ->
                    ListScreen(
                        gridClick = { gridClick(it) }
                        , list = gridList,
                        serverStatus = serverStatus,
                        internet = internet,
                        categoryRetryClick = { categoryRetryClick() }
                        )

                2 -> SearchScreen(
                    searchList = searchList,
                    historyList = historyList,
                    searchScreenBool = { searchScreenBool( it ) },
                    searchClick = { searchClick(it)},
                    historyTitleClick = {historyTitleClick(it)},
                    historyClick = {historyClick()},
                    searchItemClick = {searchItemClick(it)},
                    searchBookmarkClick = { searchBookmarkClick(it) }

                )
                3 -> BookmarkScreen(
                    bookmarkList = bookmarkList,
                    titleClick = { bookmarkQuestionClick(it) },
                    deleteClick = { deleteBookmarkClick(it) }
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
                color = Color(0xFF9C27B0),
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(7.dp)
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
                        tint = LightToolBarIcon,
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
            .height(50.dp)
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
                .padding(3.dp)
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
                    .size(20.dp)
                    .align(Alignment.CenterHorizontally)
            )

            //Spacer(modifier = Modifier.height(2.dp))

            Text( text = labelText,
                fontSize = 12.sp,
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
    aiChatClick: () -> Unit = {},
    moreClick: () -> Unit = {},
    calculationClick: (String) -> Unit = {},
    calculationList: List<Items> = emptyList(),
    lawWebsiteClick : () -> Unit = {},
    status: String = ""
) {

    Box(

        modifier = Modifier
            .fillMaxSize()

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()

        ) {

            AiChatBot(aiChatClick = aiChatClick)

            Spacer(modifier = Modifier.height(7.dp))

            Calculator(
                moreClick = { moreClick() },
                calculationClick = {calculationClick(it)},
                calculationList = calculationList,
                status = status
            )

            Spacer(modifier = Modifier.height(7.dp))

            //law website btn

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)

            ) {

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
                        .clip(shape = RoundedCornerShape(16.dp))
                        .clickable{ lawWebsiteClick() }
                        //.border(width = 1.dp, color = Color(0xFFF1B2B2), shape = RoundedCornerShape(12.dp))
                        .background(color = Color(0xFFFFFFFF))
                        .padding(10.dp)

                ) {

                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(3.dp)
                            .align(Alignment.CenterStart)
                    ) {

                        Icon( painter = painterResource(R.drawable.ic_website),
                            contentDescription = "Forward",
                            tint = Color(0xFFB98C8C),
                            modifier = Modifier
                                .wrapContentWidth()
                                .size(20.dp)
                                .align(Alignment.CenterVertically)

                        )

                        Spacer(modifier = Modifier.width(15.dp))

                        Text(text = "আইনি ওয়েবসাইট",
                            fontSize = 15.sp,
                            fontFamily = BanglaFont.font(),
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start,
                            color = Color(0xFF3F3838),
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.CenterVertically)
                        )

                    }//row

                    Icon( painter = painterResource(R.drawable.ic_right),
                        contentDescription = "Forward",
                        tint = Color(0xFFC99898),
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(3.dp)
                            .align(Alignment.CenterEnd)

                    )

                }//box

            }//box
            //law website btn

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
            .padding(9.dp)

    ) {
        
        Row(

            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
                .clip(shape = RoundedCornerShape(16.dp))
                .clickable { aiChatClick() }
                .background(color = Color(0xFFFFFFFF))
                .padding(11.dp)


        ) {

            Image( painter = painterResource(R.drawable.ic_ai_chat),
                contentDescription = "Ai",
                modifier = Modifier
                    .wrapContentWidth()
                    .size(18.dp)
                    .align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(text = "আইনি সহায়ক AI",
                fontSize = 15.sp,
                fontFamily = BanglaFont.font(),
                fontWeight = FontWeight.Normal,
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
private fun Calculator(
    moreClick : () -> Unit = {},
    calculationClick : (String) -> Unit = {},
    calculationList : List<Items> = emptyList(),
    status : String = "Pending"
) {

    val lazyScroll = rememberLazyGridState()

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()

        ) {

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)

            ) {

                Text("ক্যালকুলেটর",
                    fontSize = 15.sp,
                    fontFamily = BanglaFont.font(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF000000),
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(start = 4.dp)
                        .align(Alignment.CenterStart)
                )

                Row(

                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(shape = RoundedCornerShape(12.dp))
                        .clickable{ moreClick() }
                        .padding(5.dp)
                        .align(Alignment.CenterEnd)

                ) {

                    Text("আরো দেখুন",
                        fontSize = 12.sp,
                        fontFamily = BanglaFont.font(),
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF000000),
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterVertically)
                    )

                    Icon( painter = painterResource(R.drawable.ic_right),
                        contentDescription = "More",
                        tint = Color(0xFF796868),
                        modifier = Modifier
                            .wrapContentWidth()
                            .size(20.dp)
                            .align(Alignment.CenterVertically)

                    )

                }//row

            }//box

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = lazyScroll,
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                if (status == "Pending"){

                    items(
                        count = 3
                    ){
                        ComposeHelper.SkeletonLoading(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(7.dp),
                            shape = 14.dp,
                            innerPadding = 40.dp
                        )
                    }

                }else{

                    items(
                        items = calculationList,
                        //key = { it.title }
                    ){it ->

                        Box(

                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(5.dp)

                        ) {

                            Column(

                                modifier = Modifier
                                    .fillMaxWidth()
                                    //.border(width = 1.dp, color = Color(0xFFF2AAFF), shape = RoundedCornerShape(12.dp))
                                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
                                    .clip(shape = RoundedCornerShape(16.dp))
                                    .clickable{ calculationClick(it.title) }
                                    .background(color = Color(0xFFFFFFFF))
                                    .align(Alignment.Center)
                                    .padding(10.dp)

                            ) {

                                AsyncImage( model = it.image,
                                    contentDescription = "Calculation",
                                    modifier = Modifier
                                        .width(33.dp)
                                        .height(33.dp)
                                        .align(Alignment.CenterHorizontally)
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(text = it.title,
                                    fontSize = 12.sp,
                                    fontFamily = BanglaFont.font(),
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF000000),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterHorizontally)
                                )


                            }//column

                        }//box

                    }

                }

            }

            /*
            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceAround

            ) {

               // val calName = arrayOf("উওরাধিকার", "রেজিস্ট্রেশন ফি", "দেনমোহর")
                //val calImage = arrayOf(R.drawable.img_family, R.drawable.img_regi, R.drawable.img_denmhor)





                calName.forEachIndexed { index, name ->

                    Box(

                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(5.dp)

                    ) {

                        Column(

                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 1.dp, color = Color(0xFFF2AAFF), shape = RoundedCornerShape(12.dp))
                                .clip(shape = RoundedCornerShape(12.dp))
                                .clickable{ calculationName(name) }
                                .align(Alignment.Center)
                                .padding(10.dp)

                        ) {

                            Image( painter = painterResource(calImage[index]),
                                contentDescription = "Calculation",
                                modifier = Modifier
                                    .width(43.dp)
                                    .height(43.dp)
                                    .align(Alignment.CenterHorizontally)

                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(text = name,
                                fontSize = 13.sp,
                                fontFamily = BanglaFont.font(),
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF000000),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally)
                            )


                        }//column

                    }//box


                }//loop



            }//row

             */


        }//column

    }//box

}//fun end

@Preview(showBackground = true)
@Composable
private fun ListScreen(
    list: List<Items> = emptyList(),
    gridClick : (Items) -> Unit = {},
    serverStatus : String = "",
    internet: Boolean = false,
    categoryRetryClick : () -> Unit = {}
) {

    Box(

        modifier = Modifier.fillMaxSize()

    ) {

        val lazyState = rememberLazyGridState()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(5.dp),
            state = lazyState,
            userScrollEnabled = if (serverStatus == "Pending") false else true,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            if (serverStatus == "Pending"){

                items(17){

                    ComposeHelper.SkeletonLoading(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        shape = 12.dp,
                        innerPadding = 60.dp
                    )

                }

            }else{

                items(
                    items = list,
                    key = { it.title }
                ){ it ->

                    ListGridHelper(
                        gridImageUrl = it.image,
                        gridText = it.title,
                        onGridClick = { gridClick(it) }
                    )

                }

            }

        }

        /*
        if (internet && list.isEmpty()){

            Text(text = "পুনরায় চেষ্টা করুন",
                fontFamily = BanglaFont.font(),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF595151),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(shape = RoundedCornerShape(10.dp))
                    .clickable{ categoryRetryClick() }
                    .border(width = 1.dp, color = Color(0xFFC7A1A1), shape = RoundedCornerShape(10.dp))
                    .padding(10.dp)
                    .align(Alignment.Center)
                )

        }

         */

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
                .border(width = 1.dp, color = Color(0xFFFDE5E5), shape = RoundedCornerShape(12.dp))
                .clip(shape = RoundedCornerShape(12.dp))
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
    searchList : MutableList<Items> = mutableListOf(),
    historyList : MutableList<Items> = mutableListOf(),
    searchScreenBool : (Boolean) -> Unit = {},
    searchClick: (String) -> Unit = {},
    historyTitleClick: (String) -> Unit = {},
    historyClick: () -> Unit = {},
    searchItemClick : (String) -> Unit = {},
    searchBookmarkClick: (String) -> Unit = {}
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
                        bookmarkClick = { searchBookmarkClick(it.question) },
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
                            historyTitleClick(historyTitle.value)
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
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            historyClick = {
                isSearchDataVisible = !isSearchDataVisible
                           historyClick()
                           },
            historyTitle = historyTitle,
            searchClick = {searchClick(it)}
        )

    }//box

}//fun end


@Preview(showBackground = true)
@Composable
private fun SearchBarHelper(
    historyClick: () -> Unit = {},
    historyTitle: MutableState<String> = mutableStateOf(""),
    modifier: Modifier = Modifier,
    searchClick : (String) -> Unit = {}
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

                                    searchClick(searchFiled)  //sending search data to other functions

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
                .border(width = 1.dp, color = Color(0xFFFFDCDC), shape = RoundedCornerShape(12.dp))
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

                //.background(color = Color(0xFFFFFFFF))
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
                .border(width = 1.dp, color = Color(0xFFFFD3D3), shape = RoundedCornerShape(12.dp))
                .clip(shape = RoundedCornerShape(12.dp))
                .combinedClickable(

                    onClick = {

                        titleClick()
                        if (title.isNotEmpty()) titleData(title)

                    }
                )

                //.background(color = Color(0xFFFFFFFF))
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
    bookmarkList: List<Items> = emptyList(),
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