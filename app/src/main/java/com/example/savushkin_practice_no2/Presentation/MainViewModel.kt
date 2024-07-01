package com.example.savushkin_practice_no2.Presentation

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savushkin_practice_no2.Domain.Models.ProductsData
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
import com.journeyapps.barcodescanner.ScanContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val dataRepository: DatabaseRepository,  private val fileRepository: ParserRepository) : ViewModel()  {

    var isScannerOpened by mutableStateOf(false)

    private val insertUseCase = InsertDataUseCase(dataRepository)
    private val getDataDbCase = GetDataUseCase(dataRepository)
    private val deleteDataUseCase = DeleteDataUseCase(dataRepository)
    private val createTable = CreateTableUseCase(dataRepository)
    private val getListTable = GetListTableDbUseCase(dataRepository)
    private val getListTableFieldsUseCase = GetListTableFieldsUseCase(dataRepository)

    private val getDataFile = GetDataFromFileUseCase(fileRepository)
    private val getRequest = GetJsonDataUseCase(fileRepository)

    private val _selectedColumns = mutableMapOf<String, String?>()
    val selectedColumns: Map<String, String?>
        get() = _selectedColumns.toMap()

    private val _selectedColumnsLiveData = MutableLiveData<Map<String, String?>>()
    val selectedColumnsLiveData: LiveData<Map<String, String?>>
        get() = _selectedColumnsLiveData

    var selectedTable by mutableStateOf("")

    private val _orientation = MutableLiveData("Vertical")
    val orientation: LiveData<String> = _orientation

    private val _request = MutableLiveData("none")
    val request: LiveData<String> =  _request

    private val _dataList = MutableLiveData<List<ContentValues>>()
    val dataList: LiveData<List<ContentValues>> get() = _dataList

    var CREATE_TABLE: String? = null

    private val _selectedColumnsReturn = mutableStateListOf<String>("SNM")
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

    private val _loadingTasksCompleted = MutableLiveData(0)
    val loadingTasksCompleted: LiveData<Int> get() = _loadingTasksCompleted

    var TestString: String = "]C102048102680121811123112510531125\u001D3718\u001D218\u001D"
    var bar_1: String = "]C10114810268048163310300750411231030107012\u001D210114"
    var bar_2: String = "]C102048125520072311123112510531125\u001D3718\u001D218\u001D"
    var bar_3: String = "]C10204814525000430112110241047102437\u001D211"
    var my_Bar: String = "]C10204810268010415\u001D37121234567890\u001D3101567890\u001D3113123456\u001D3142789012\u001D3098765432\u001D11071922\u001D1000123\u001D214567"
    // ]C10204810268010415\u001D37121234567890\u001D310n567890\u001D311n123456\u001D314n789012\u001D3098765432\u001D110719\u001D1000123\u001D214567
    var barcodeStr: String? = ""

    private val _ColumnsView = mutableStateListOf<String>()
    val ColumnsView: List<String> get()= _ColumnsView

    val value: ContentValues = ContentValues()
    private val _dataListProducts = MutableLiveData<List<ContentValues>>()
    val dataListProducts: LiveData<List<ContentValues>> get() = _dataListProducts

    var listReturn: List<String> = listOf("Name","Capacity", "Length", "Weight","Square","Containers on pallet","Quantity","Production date","Lot Number","Pallet Number","Volume",)
    var productsData: ProductsData = ProductsData()

    private val channel = Channel<suspend () -> Unit>(capacity = Channel.UNLIMITED)

    init {
        viewModelScope.launch {
            for (task in channel) {
                task()
            }
        }
    }
    fun incrementTasksCompleted() {

    }

    fun decreaseTasksCompleted() {
        _loadingTasksCompleted.postValue( _loadingTasksCompleted.value?.minus(1))
    }



    fun updateBarcode(contents: String?, viewModel: MainViewModel) {
        contents?.let { barcode ->
            barcodeStr = barcode
            Log.d("string BARCODE", "$barcodeStr")
            val parsedData = parseGs1_128(barcodeStr.toString())

            for ((key, value) in parsedData) {
                println("AI: $key, Value: $value")
            }
            takeDataNs_mc(viewModel,parsedData)
        }
    }



    fun parseGs1_128(barcode: String): Map<String, String> {

        val gs1Prefix = "]C1"
        val barcodeData = if (barcode.startsWith(gs1Prefix)) {
            barcode.substring(gs1Prefix.length)
        } else {
            barcode
        }

        val aiLengths = mapOf(
            "01" to 14,
            "02" to 14,
            "37" to null,
            "310" to 6,
            "311" to 6,
            "314" to 6,
            "30" to 8,
            "11" to 6,
            "10" to null,
            "21" to null
        )

        val result = mutableMapOf<String, String>()
        var position = 0

        while (position < barcodeData.length) {
            var ai: String? = null
            var length: Int? = null

            if (position + 4 <= barcodeData.length && aiLengths.containsKey(barcodeData.substring(position, position + 3))) {
                ai = barcodeData.substring(position, position + 3)
                length = aiLengths[barcodeData.substring(position, position + 3)]
                position += 4
            }
            else if (position + 2 <= barcodeData.length && aiLengths.containsKey(barcodeData.substring(position, position + 2))) {
                ai = barcodeData.substring(position, position + 2)
                length = aiLengths[ai]
                position += 2
            } else {
                throw IllegalArgumentException("Неизвестный AI в позиции $position")
            }

            val value: String

            if (length == null) {
                val endPosition = barcodeData.indexOf('\u001D', position)
                if (ai == "21") {
                    if (endPosition == -1) {
                        value = barcodeData.substring(position)
                        position = barcodeData.length
                    } else {
                        value = barcodeData.substring(position, endPosition)
                        position = endPosition
                    }
                } else {
                    value = if (endPosition == -1) {
                        barcodeData.substring(position)
                    } else {
                        barcodeData.substring(position, endPosition)
                    }
                    position += value.length + 1
                }
            } else {
                if (position + length > barcodeData.length) {
                    throw IllegalArgumentException("Недостаточно символов для AI $ai")
                }
                value = barcodeData.substring(position, position + length)
                position += length
            }

            if (ai == "21" && value.endsWith('\u001D')) {
                result[ai] = value.substring(0, value.length - 1)
            } else {
                result[ai] = value
            }

            if (position < barcodeData.length && barcodeData[position] == '\u001D') {
                position++
            }
        }
        setInfo(result)

        return result
    }
    fun setInfo(list: Map<String, String>){

        if (!list.get("310").isNullOrEmpty()){
            value.put("Weight", "${list.get("310")?.toInt()} kg")
        }
        if (!list.get("311").isNullOrEmpty()){
            value.put("Length","${list.get("311")?.toInt()} m" )
        }
        if (!list.get("314").isNullOrEmpty()){
            value.put("Square", "${list.get("314")?.toInt()} m^2")
        }
        if(list.get("30").isNullOrEmpty()){
            value.put("Capacity", list.get("37"))
        }else{
            value.put("Capacity", list.get("30"))
            value.put("Containers on pallet", list.get("37"))
        }

        val dateString = list["11"] ?: ""
        if (dateString.length == 6) {
            val year = dateString.substring(0, 2).toInt()
            val month = dateString.substring(2, 4).toInt()
            val day = dateString.substring(4, 6).toInt()
            val formattedDate = String.format("%02d.%02d.%02d", day, month, year)

            value.put("Production date", formattedDate)
        } else {
            value.put("Production date", "")
        }
        value.put("Lot Number", list.get("10"))
        value.put("Pallet Number", list.get("21"))

        Log.d("SetInfo", "$value")
    }
    fun takeGtinOfNs_semk(partBarcode: Int, selectedField: String, selectedTable: String, viewModel: MainViewModel, parsedData: Map<String, String>): LiveData<List<ContentValues>> {
        Log.d("takeGtinOfNs_semk", " Взятый GTIN ${productsData.GTIN}")
        _selectedColumns.clear()
        if(parsedData["01"].isNullOrEmpty()) {
            _selectedColumns[selectedField] = parsedData["02"]
        }else
        {
            _selectedColumns[selectedField] = parsedData["01"]
        }

        Log.d("takeGtinOfNs_semk", " Полученная колонка для вывода $_selectedColumns")

        return getDataDbCase.invoke(selectedTable, _selectedColumns, listOf("KMC", "EMK"), viewModel)
    }

    fun takeDataNs_mc(viewModel: MainViewModel, parsedData: Map<String, String>) {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                takeGtinOfNs_semk(1, "GTIN", "NS_SEMK", viewModel,parsedData)
            }

            data.observeForever { dataList ->
                dataList?.let { list ->
                    for (contentValues in list) {
                        Log.d("LiveDataContent", "ContentValues: $contentValues")
                    }
                }
            }
            val observer = Observer<List<ContentValues>> { dataList ->
                dataList?.let { list ->
                    list.forEach { contentValues ->
                        _selectedColumns.clear()
                        _selectedColumns["KMC"] = contentValues.getAsString("KMC")
                        if (!value.getAsString("Capacity").isNullOrEmpty()){
                            productsData.Volume = contentValues.getAsInteger("EMK") * 37
                            value.put("Volume", productsData.Volume)
                        }
                        val dataNs_mc = getDataDbCase.invoke("NS_MC", _selectedColumns, null, viewModel)

                        val innerObserver = Observer<List<ContentValues>> { dataListNs_mc ->
                            _dataList.postValue(dataListNs_mc)

                            dataListNs_mc?.let { listNs_mc ->
                                for (contentValuesNs_mc in listNs_mc) {
                                    productsData.SNM = contentValuesNs_mc.getAsString("SNM")
                                    if (!productsData.SNM.isNullOrEmpty() && _dataListProducts.value.isNullOrEmpty()){
                                        value.put("Name", productsData.SNM)

                                    }else{
                                        value.remove("Name")
                                    }

                                    Log.d("2 LiveDataContent", "2 ContentValues: $contentValuesNs_mc SNM ${productsData.SNM} Volumn ${productsData.Volume}")
                                }
                            }
                            Log.d("2 LiveDataContent _dataList", "_dataList: ${_dataList.value}")
                        }
                        dataNs_mc.observeForever(innerObserver)
                    }
                }
            }
            _dataListProducts.postValue(listOf(value))
            data.observeForever(observer)
        }
    }

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

    fun getDataFromBd(
        tableName: String,
        selectedColumns: Map<String, String?>,
        listColumnsForReturn: List<String>,
        viewModel: MainViewModel
    ) {
        if(listColumnsForReturn.isNotEmpty()){
            viewModelScope.launch {
                val data = getDataDbCase.invoke(tableName,selectedColumns, listColumnsForReturn, viewModel)
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
                                }
                                */
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
        //createTable.invoke(DELETE_Table)
        createTable.invoke(getJsonRequest(context, fileName))
        //createTable.createTable(DELETE_Table)
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
