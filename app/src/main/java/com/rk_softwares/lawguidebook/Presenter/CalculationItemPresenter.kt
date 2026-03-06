package com.rk_softwares.lawguidebook.Presenter

import com.rk_softwares.lawguidebook.Model.Calculation
import com.rk_softwares.lawguidebook.Model.CalculationItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

interface CalculationItemView{

    fun onCalculationList (list: List<Calculation>)
    fun serverStatus (status : String)

}

class CalculationItemPresenter(
    private val view : CalculationItemView
) {

    private val model = CalculationItemModel()

    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun calculationDataFromServer(){

        view.serverStatus("Pending")

        scopeIO.launch {

            model.calculationServer(onSuccess = { result ->

                scopeMain.launch {

                    view.serverStatus("Success")
                    view.onCalculationList(result)

                }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch {

                        view.serverStatus("Failed")

                    }

                }

            })

        }

    }//fun end

    fun onDestroy(){
        scopeIO.cancel()
        scopeMain.cancel()
    }//fun end

}