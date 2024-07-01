package com.example.savushkin_practice_no2.Data.Repository

import android.content.ContentValues
import androidx.lifecycle.LiveData
import com.example.savushkin_practice_no2.Domain.Repository.DatabaseRepository
import com.example.savushkin_practice_no2.Domain.Repository.SqlRepository
import com.example.savushkin_practice_no2.Presentation.MainViewModel

class DatabaseRepositoryImp(private val sqlRepository: SqlRepository): DatabaseRepository {

    override suspend fun insertDataToDb(contentValuesList: List<ContentValues>, TABLE_NAME: String, viewModel: MainViewModel) {

        return sqlRepository.insertData(dataList = contentValuesList,TABLE_NAME = TABLE_NAME,viewModel = viewModel)
    }


    override fun getDataDb(
        tableName: String,
        selectedColumns: Map<String, String?>,
        listColumnsForReturn: List<String>?,
        viewModel: MainViewModel
    ): LiveData<List<ContentValues>> {

        return sqlRepository.getData(tableName,selectedColumns,listColumnsForReturn,viewModel)

    }

    override suspend fun deleteDataDb(tableName: String, selectedColumn: String?, selectedValue: String?) {
        return sqlRepository.delete(tableName, selectedColumn, selectedValue)
    }

    override suspend fun getListTable(): List<String> {
        return sqlRepository.getListTable()
    }


    override fun createTable(CREATE_TABLE: String) {
        sqlRepository.createTable(CREATE_TABLE)
    }

    override suspend fun getListTableFields(tableName: String): List<String> {
        return sqlRepository.getListTableFields(tableName)
    }
}