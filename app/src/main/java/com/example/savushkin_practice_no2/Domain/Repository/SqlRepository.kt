package com.example.savushkin_practice_no2.Domain.Repository

import android.content.ContentValues
import androidx.lifecycle.LiveData
import com.example.savushkin_practice_no2.Presentation.MainViewModel

interface SqlRepository {

    suspend fun insertData(dataList: List<ContentValues>, TABLE_NAME: String, viewModel: MainViewModel)

    fun getData(
        tableName: String,
        selectedColumns: Map<String, String?>,
        listColumnsForReturn: List<String>?,
        viewModel: MainViewModel
    ): LiveData<List<ContentValues>>

    suspend fun delete(tableName: String, selectedColumn: String?, selectedValue: String?)

    fun createTable(CREATE_TABLE: String)

    suspend fun  getListTable():List<String>

    suspend fun getListTableFields(tableName: String): List<String>
}