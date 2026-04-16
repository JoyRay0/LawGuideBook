package com.rk_softwares.lawguidebook.Presenter

import com.rk_softwares.lawguidebook.Model.LawWebsiteModel
import com.rk_softwares.lawguidebook.Model.WebsiteData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

interface LawWebsites{

    fun websiteList (list: List<WebsiteData>)
    fun serverStatus (status : String)

}

class LawWebsitePresenter(
    private val view : LawWebsites
) {

    private val model = LawWebsiteModel()
    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun websitesData(){

        view.serverStatus("websites_pending")

        scopeIO.launch {

            model.allWebsiteLinks(
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