package com.example.savushkin_practice_no2.Presentation

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savushkin_practice_no2.Domain.Repository.DatabaseRepository
import com.example.savushkin_practice_no2.Domain.Repository.ParserRepository
import com.example.savushkin_practice_no2.Domain.UseCase.CreateTableUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.DeleteDataUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.GetDataFromFileUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.GetDataUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.GetJsonDataUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.GetListTableDbUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.GetListTableFieldsUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.InsertDataUseCase
import com.example.savushkin_practice_no2.Domain.UseCase.ParseGs1_128BacodeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val dataRepository: DatabaseRepository,  private val fileRepository: ParserRepository) : ViewModel()  {


    private val insertUseCase = InsertDataUseCase(dataRepository)
    private val getDataDbCase = GetDataUseCase(dataRepository)
    private val deleteDataUseCase = DeleteDataUseCase(dataRepository)
    private val createTable = CreateTableUseCase(dataRepository)
    private val getListTable = GetListTableDbUseCase(dataRepository)
    private val getListTableFieldsUseCase = GetListTableFieldsUseCase(dataRepository)

    private val getDataFile = GetDataFromFileUseCase(fileRepository)
    private val getRequest = GetJsonDataUseCase(fileRepository)
    private val parseGS1_128 =  ParseGs1_128BacodeUseCase(fileRepository)

    private val _selectedColumns = mutableMapOf<String, String?>()

    private val _request = MutableLiveData("none")

    private val _dataList = MutableLiveData<List<ContentValues>>()

    private val _tableField = MutableLiveData <List<String>>()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _valueLoading = MutableStateFlow(0)
    val valueLoading: StateFlow<Int> = _valueLoading.asStateFlow()

    private val _loadingTasksCompleted = MutableLiveData(0)
    val loadingTasksCompleted: LiveData<Int> get() = _loadingTasksCompleted

    var barcodeStr: String? = ""

    val value: ContentValues = ContentValues()

    private val _dataListProducts = MutableLiveData<List<ContentValues>>()

    val dataListProducts: LiveData<List<ContentValues>> get() = _dataListProducts

    var listReturn: List<String> = listOf("Name", "Capacity", "Length", "Weight", "Square", "Containers on pallet", "Quantity", "Production date", "Lot Number", "Pallet Number", "Volume",)

    private val channel = Channel<suspend () -> Unit>(capacity = Channel.UNLIMITED)


    init {
        viewModelScope.launch {
            for (task in channel) {
                task()
            }
        }
    }

    fun incrementTasksCompleted() {
        _loadingTasksCompleted.value =_loadingTasksCompleted.value?.plus(1)
    }

    fun decreaseTasksCompleted() {
        _loadingTasksCompleted.postValue( _loadingTasksCompleted.value?.minus(1))
    }

    fun updateBarcode(contents: String?, viewModel: MainViewModel) {
        contents?.let { barcode ->

            barcodeStr = barcode
            Log.d("string BARCODE", "$barcodeStr")

            val parsedData = parseGS1_128.invoke(barcodeStr.toString())
            setInfo(parsedData)
            for ((key, value) in parsedData) {
                println("AI: $key, Value: $value")
            }

            takeDataNs_mc(viewModel,parsedData)
        }
    }

    private fun setInfo(list: Map<String, String>) {

        value.apply {
            list["310"]?.let { weight ->
                put("Weight", "${weight.toInt()} kg")
            }
            list["311"]?.let { length ->
                put("Length", "${length.toInt()} m")
            }
            list["314"]?.let { square ->
                put("Square", "${square.toInt()} m^2")
            }

            if (list["30"].isNullOrEmpty()) {
                put("Capacity", list["37"])
            } else {
                put("Capacity", list["30"])
                put("Containers on pallet", list["37"])
            }

            list["11"]?.takeIf { it.length == 6 }?.let { dateString ->
                val year = dateString.substring(0, 2).toInt()
                val month = dateString.substring(2, 4).toInt()
                val day = dateString.substring(4, 6).toInt()
                val formattedDate = String.format("%02d.%02d.%02d", day, month, year)
                put("Production date", formattedDate)
            } ?: put("Production date", "")

            put("Lot Number", list["10"])
            put("Pallet Number", list["21"])
        }

        Log.d("SetInfo", "$value")
    }

    fun takeGtinOfNs_semk(selectedField: String, selectedTable: String, viewModel: MainViewModel, parsedData: Map<String, String>): LiveData<List<ContentValues>> {
        val gtin = parsedData["02"] ?: parsedData["01"] ?: ""
        Log.d("takeGtinOfNs_semk", " Взятый GTIN $gtin")

        _selectedColumns.clear()
        _selectedColumns[selectedField] = gtin
        Log.d("takeGtinOfNs_semk", " Полученная колонка для вывода $_selectedColumns")

        return getDataDbCase.invoke(selectedTable, _selectedColumns, listOf("KMC", "EMK"), viewModel)
    }

    fun takeDataNs_mc(viewModel: MainViewModel, parsedData: Map<String, String>) {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                takeGtinOfNs_semk("GTIN", "NS_SEMK", viewModel, parsedData)
            }

            val observer = Observer<List<ContentValues>> { dataList ->
                dataList?.forEach { contentValues ->
                    Log.d("LiveDataContent", "ContentValues: $contentValues")
                    handleContentValues(contentValues, viewModel)
                }
            }

            data.observeForever(observer)
        }
    }

    private fun handleContentValues(contentValues: ContentValues, viewModel: MainViewModel) {
        _selectedColumns.clear()
        _selectedColumns["KMC"] = contentValues.getAsString("KMC")

        if (!value.getAsString("Capacity").isNullOrEmpty()) {
            value.put("Volume", contentValues.getAsDouble("EMK") * 37)
        }

        val dataNs_mc = getDataDbCase.invoke("NS_MC", _selectedColumns, null, viewModel)
        val innerObserver = Observer<List<ContentValues>> { dataListNs_mc ->
            _dataList.postValue(dataListNs_mc)
            dataListNs_mc?.forEach { contentValuesNs_mc ->
                value.put("Name", contentValuesNs_mc.getAsString("SNM"))
                Log.d("2 LiveDataContent", "2 ContentValues: $contentValuesNs_mc SNM ${value.getAsString("SNM")} Volumn ${value.getAsString("Volume")}")
            }
        }

        dataNs_mc.observeForever(innerObserver)

        _dataListProducts.postValue(listOf(value))
        Log.d("2 LiveDataContent _dataList", "_dataList: ${_dataList.value}")
    }


    fun updateValueLoading(value: Int) {
        _valueLoading.value = value
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun writeToBd(context: Context, fileName: String, nameTable: String, viewModel: MainViewModel) {
        incrementTasksCompleted()
        viewModelScope.launch {

            channel.send {
                val dataList = withContext(Dispatchers.IO) {
                    getDataFile.invoke(context = context, fileName = fileName)
                }
                withContext(Dispatchers.IO) {
                    insertUseCase.invoke(contentValuesList = dataList, TABLE_NAME = nameTable, viewModel = viewModel)
                }
            }
        }
    }


    fun deleteDataFromBd(tableName: String, selectedColumn: String?, selectedValue: String?){
        viewModelScope.launch {
            deleteDataUseCase.invoke(tableName, selectedColumn, selectedValue)
        }
    }

    fun createTable(context: Context, fileName: String){
        createTable.invoke(getJsonRequest(context, fileName))
    }

    fun getJsonRequest(context: Context, fileName: String):String{
        _request.value = getRequest.invoke(context, fileName)
        Log.d("CREATE_TABLE", _request.value.toString())
        return _request.value.toString()
    }

    fun getListTableField(tableName: String) {
        viewModelScope.launch {
            _tableField.value = getListTableFieldsUseCase.invoke(tableName)
            _tableField.observeForever { item ->
                Log.d("List TableField" , "$item")
            }
        }
    }
}
