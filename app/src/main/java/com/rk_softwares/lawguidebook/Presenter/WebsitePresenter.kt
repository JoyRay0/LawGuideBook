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

class WebsitePresenter(
    private val view : Websites
) {

    private val model = WebsiteModel()
    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun lawWebsites(){

        view.serverStatus("websites_pending")

        scopeIO.launch {

            model.allLawWebsiteLinks(
                onSuccess = { result ->

                    scopeMain.launch {

                        view.serverStatus("websites_success")
                        view.websiteList(result)

                    }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch {

                        view.serverStatus("websites_failed")

                    }

                }

            })

        }

    }

    fun govWebsites(){

        view.serverStatus("websites_pending")

        scopeIO.launch {

            model.allGovWebsiteLinks(
                onSuccess = { result ->

                    scopeMain.launch {

                        view.serverStatus("websites_success")
                        view.websiteList(result)

                    }

                }, onFailed = { isFailed ->

                    if (isFailed){

                        scopeMain.launch {

                            view.serverStatus("websites_failed")

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