package com.example.savushkin_practice_no2.Domain.Repository

import android.content.ContentValues
import android.content.Context
import com.example.savushkin_practice_no2.Domain.Models.DataSQL
import com.example.savushkin_practice_no2.Domain.Models.NS_SEMK

interface ParserRepository {
    fun parserXML(context: Context, fileName: String): List<ContentValues>
    fun readParserFile(context: Context, fileName: String): String?
    fun parseJSON(context: Context, fileName: String): DataSQL?
}
