package com.example.savushkin_practice_no2.Domain.UseCase

import android.content.ContentValues
import android.content.Context
import com.example.savushkin_practice_no2.Domain.Repository.ParserRepository

class GetDataFromFileUseCase(private val repository: ParserRepository) {
    fun invoke(context: Context, fileName: String): List<ContentValues>  {
        return repository.parserXML(context, fileName)
    }
}