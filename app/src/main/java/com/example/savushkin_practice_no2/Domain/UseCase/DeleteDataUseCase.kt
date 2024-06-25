package com.example.savushkin_practice_no2.Domain.UseCase

import com.example.savushkin_practice_no2.Domain.Repository.DatabaseRepository

class DeleteDataUseCase(private val dataRepository: DatabaseRepository) {
    suspend fun invoke(tableName: String, selectedColumn: String?, selectedValue: String?) {
        dataRepository.deleteDataDb(tableName, selectedColumn, selectedValue)
    }
}