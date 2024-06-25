package com.example.savushkin_practice_no2.Domain.UseCase

import com.example.savushkin_practice_no2.Domain.Repository.DatabaseRepository

class GetListTableFieldsUseCase(private val dataRepository: DatabaseRepository) {
    suspend fun invoke(tableName: String): List<String>{
        return dataRepository.getListTableFields(tableName)
    }
}