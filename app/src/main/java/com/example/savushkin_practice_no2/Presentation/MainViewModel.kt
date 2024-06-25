package com.example.savushkin_practice_no2.Presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savushkin_practice_no2.Domain.Repository.DatabaseRepository
import com.example.savushkin_practice_no2.Domain.Repository.ParserRepository
import com.example.savushkin_practice_no2.Domain.UseCase.GetDataFromFileUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.GetRequestUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val fileRepository: ParserRepository) : ViewModel()  {

    private val getDataFile = GetDataFromFileUseCase(fileRepository)
    private val getRequest = GetRequestUseCase(fileRepository)

    private val _request = MutableLiveData("none")

    fun writeToBd(context: Context, fileName: String, viewModel: MainViewModel) {
        val dataList = getDataFile.invoke(context, fileName)
        for (item in dataList){
            Log.d("File text", "$item")
        }
    }

    fun getJsonRequest(context: Context, fileName: String):String{
        _request.value = getRequest.invoke(context, fileName)
        var CREATE_TABLE = _request.value.toString()
        Log.d("CREATE_TABLE", CREATE_TABLE)
        return _request.value.toString()
    }
}