package com.example.savushkin_practice_no2.Domain.UseCase

import android.content.Context
import com.example.savushkin_practice_no2.Domain.Repository.ParserRepository

class GetJsonDataUseCase (private val repository: ParserRepository){
    fun invoke(context: Context, fileName: String): String {
        return repository.parseJSON(context, fileName)?.DATA ?: "error"
    }
}