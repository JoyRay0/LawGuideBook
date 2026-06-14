package com.rk_softwares.lawguidebook.Presenter

import android.util.Log
import com.rk_softwares.lawguidebook.Model.WebsiteModel
import com.rk_softwares.lawguidebook.Model.WebsiteData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

interface Websites{

    fun websiteList (list: List<WebsiteData>)
    fun serverStatus (status : String)

}

enum class WebsiteStatus(val value : String){

    WebsitePending("websites_pending"),
    WebsiteSuccess("websites_success"),
    WebsiteFailed("websites_failed"),

}

class WebsitePresenter(
    private val view : Websites
) {

    private val model = WebsiteModel()
    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun lawWebsites(){

        view.serverStatus(WebsiteStatus.WebsitePending.value)

        scopeIO.launch {

            model.allLawWebsiteLinks(
                onSuccess = { result ->

                    scopeMain.launch {

                        view.serverStatus(WebsiteStatus.WebsiteSuccess.value)
                        view.websiteList(result)

                    }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch {

                        view.serverStatus(WebsiteStatus.WebsiteFailed.value)

                    }

                }

            })

        }

    }

    fun govWebsites(){

        view.serverStatus(WebsiteStatus.WebsitePending.value)

        scopeIO.launch {

            model.allGovWebsiteLinks(
                onSuccess = { result ->

                    scopeMain.launch {

                        view.serverStatus(WebsiteStatus.WebsiteSuccess.value)
                        view.websiteList(result)

                    }

                }, onFailed = { isFailed ->

                    if (isFailed){

                        scopeMain.launch {

                            view.serverStatus(WebsiteStatus.WebsiteFailed.value)

                        }

                    }

                })

        }

    }

    fun onDestroy(){
        scopeIO.cancel()
        scopeMain.cancel()
    }
}