package com.example.savushkin_practice_no2.Presentation

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savushkin_practice_no2.Domain.Repository.DatabaseRepository
import com.example.savushkin_practice_no2.Domain.Repository.ParserRepository
import com.example.savushkin_practice_no2.Domain.UseCase.CreateTableUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.DeleteDataUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.GetDataFromFileUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.GetDataUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.GetListTableDbUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.GetListTableFieldsUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.GetRequestUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.InsertDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val dataRepository: DatabaseRepository,  private val fileRepository: ParserRepository) : ViewModel()  {


    private val insertUseCase = InsertDataUseCase(dataRepository)
    private val getDataseCase = GetDataUseCase(dataRepository)
    private val deleteDataUseCase = DeleteDataUseCase(dataRepository)
    private val createTable = CreateTableUseCase(dataRepository)
    private val getListTable = GetListTableDbUseCase(dataRepository)
    private val getListTableFieldsUseCase = GetListTableFieldsUseCase(dataRepository)

    private val getDataFile = GetDataFromFileUseCase(fileRepository)
    private val getRequest = GetRequestUseCase(fileRepository)

    private val _selectedColumns = mutableMapOf<String, String?>()
    val selectedColumns: Map<String, String?>
        get() = _selectedColumns.toMap()

    var selectedTable by mutableStateOf("")

    private val _orientation = MutableLiveData("Vertical")
    val orientation: LiveData<String> = _orientation

    private val _request = MutableLiveData("none")
    val request: LiveData<String> =  _request

    private val _dataList = MutableLiveData<List<ContentValues>>()
    val dataList: LiveData<List<ContentValues>> get() = _dataList

    var CREATE_TABLE: String? = null

    private val _selectedColumnsReturn = mutableStateListOf<String>()
    val selectedColumnsReturn: List<String> get()= _selectedColumnsReturn

    private val _tableNames = MutableLiveData<List<String>>()
    val tableNames: LiveData<List<String>> get() = _tableNames

    private val _tableField = MutableLiveData <List<String>>()
    val tableField: LiveData<List<String>> get() = _tableField

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress



    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isInsert = MutableStateFlow(false)
    val isInsert: StateFlow<Boolean> = _isInsert.asStateFlow()

    private val _valueLoading = MutableStateFlow(0)
    val valueLoading: StateFlow<Int> = _valueLoading.asStateFlow()

    fun updateIsLoading(value: Boolean) {
        _isLoading.value = value
    }

    fun updateValueLoading(value: Int) {
        _valueLoading.value = value
    }
    fun setInsert(isLoading: Boolean) {
        _isInsert.value = isLoading
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun writeToBd(context: Context, fileName: String, viewModel: MainViewModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dataList = getDataFile.invoke(context, fileName)
                insertUseCase.invoke(dataList, "NS_SEMK", viewModel)
            }
        }

    }

    fun getDataFromBd(
        tableName: String,
        selectedColumns: Map<String, String?>,
        listColumnsForReturn: List<String>,
        viewModel: MainViewModel
    ) {
        if(listColumnsForReturn.isNotEmpty()){
            viewModelScope.launch {
                val data = getDataseCase.invoke(tableName,selectedColumns, listColumnsForReturn, viewModel)
                data.observeForever { dataList ->
                    _dataList.postValue(dataList)
                    Log.d("MainViewModel", "Data list updated: $dataList")
                }
                /*                data.observeForever { dataList ->
                                    dataList?.let { list ->
                                        for (contentValues in list) {
                                            Log.d("LiveDataContent", "ContentValues: $contentValues")
                                        }
                                    }
                                }*/
            }
        }
    }

    fun deleteDataFromBd(tableName: String, selectedColumn: String?, selectedValue: String?){
        viewModelScope.launch {
            deleteDataUseCase.invoke(tableName, selectedColumn, selectedValue)
        }
    }

    fun createTable(context: Context, fileName: String){
        var DELETE_Table = "DROP TABLE NS_MC"
        createTable.invoke(getJsonRequest(context, fileName))
        //createTable.createTable(DELETE_Table)
    }


    fun getJsonRequest(context: Context, fileName: String):String{
        _request.value = getRequest.invoke(context, fileName)
        CREATE_TABLE = _request.value
        Log.d("CREATE_TABLE", CREATE_TABLE.toString())
        return _request.value.toString()
    }

}
