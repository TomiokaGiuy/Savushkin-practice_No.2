package com.example.savushkin_practice_no2.Domain.UseCase

import com.example.savushkin_practice_no2.Domain.Repository.DatabaseRepository

class CreateTableUseCase(private val dataRepository: DatabaseRepository) {
    fun invoke(CREATE_TABLE: String){
        dataRepository.createTable(CREATE_TABLE)
    }

}