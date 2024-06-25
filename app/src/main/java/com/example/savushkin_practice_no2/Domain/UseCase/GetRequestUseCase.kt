package com.example.savushkin_practice_no2.Domain.UseCase

import android.content.Context
import com.example.savushkin_practice_no2.Domain.Repository.ParserRepository

class GetRequestUseCase (private val repository: ParserRepository){
    fun invoke(context: Context, fileName: String): String {
        return repository.readRequest(context, fileName)?.DATA ?: "error"
    }

}