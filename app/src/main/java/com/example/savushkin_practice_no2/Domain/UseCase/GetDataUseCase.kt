package com.example.savushkin_practice_no2.Domain.UseCase

import android.content.ContentValues
import androidx.lifecycle.LiveData
import com.example.savushkin_practice_no2.Domain.Repository.DatabaseRepository
import com.example.savushkin_practice_no2.Presentation.MainViewModel

class GetDataUseCase(private val dataRepository: DatabaseRepository) {
    fun invoke(
        tableName: String,
        selectedColumns: Map<String, String?>,
        listColumnsForReturn: List<String>,viewModel: MainViewModel
    ): LiveData<List<ContentValues>> {
        return dataRepository.getDataDb(tableName, selectedColumns, listColumnsForReturn, viewModel)
    }
}