package com.example.savushkin_practice_no2.Domain.UseCase

import com.example.savushkin_practice_no2.Domain.Repository.DatabaseRepository

class GetListTableDbUseCase(private val dataRepository: DatabaseRepository) {
    suspend fun invoke(): List<String>{
        return dataRepository.getListTable()
    }
}