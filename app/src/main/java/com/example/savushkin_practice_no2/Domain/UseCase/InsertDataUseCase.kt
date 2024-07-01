package com.example.savushkin_practice_no2.Domain.UseCase

import android.content.ContentValues
import com.example.savushkin_practice_no2.Domain.Repository.DatabaseRepository
import com.example.savushkin_practice_no2.Presentation.MainViewModel

class InsertDataUseCase(private val dataRepository: DatabaseRepository) {
    suspend fun invoke(contentValuesList: List<ContentValues>, TABLE_NAME: String, viewModel: MainViewModel) {
        dataRepository.insertDataToDb(contentValuesList = contentValuesList, TABLE_NAME = TABLE_NAME, viewModel =  viewModel)
    }
}