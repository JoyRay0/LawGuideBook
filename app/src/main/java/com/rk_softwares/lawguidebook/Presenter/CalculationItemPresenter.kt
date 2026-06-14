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

enum class CalculationStatus(val value : String){

    CalculationPending("calculation_item_pending"),
    CalculationSuccess("calculation_item_success"),
    CalculationFailed("calculation_item_failed")

}

class CalculationItemPresenter(
    private val view : CalculationItemView
) {

    private val model = CalculationItemModel()

    private val scopeIO = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeMain = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun calculationDataFromServer(){

        view.serverStatus(CalculationStatus.CalculationPending.value)

        scopeIO.launch {

            model.calculationAllItemServer(onSuccess = { result ->

                scopeMain.launch {

                    view.serverStatus(CalculationStatus.CalculationSuccess.value)
                    view.onCalculationList(result)

                }

            }, onFailed = { isFailed ->

                if (isFailed){

                    scopeMain.launch {

                        view.serverStatus(CalculationStatus.CalculationFailed.value)

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