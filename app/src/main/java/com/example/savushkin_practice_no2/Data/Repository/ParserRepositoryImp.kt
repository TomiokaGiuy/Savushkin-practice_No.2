package com.example.savushkin_practice_no2.Data.Repository

import android.content.ContentValues
import android.content.Context
import com.example.savushkin_practice_no2.Data.Util.Helper
import com.example.savushkin_practice_no2.Domain.Models.DataSQL
import com.example.savushkin_practice_no2.Domain.Repository.ParserRepository

class ParserRepositoryImp(): ParserRepository {
    override fun parserXML(context: Context, fileName: String):  List<ContentValues>  {
        var helper = Helper()
        return helper.parserXML(helper.readFile(context = context, fileName = fileName))
    }

    override fun readParserFile(context: Context, fileName: String): String? {
        var helper = Helper()
        return helper.readFile(context = context, fileName = fileName)
    }

    override fun parseJSON(context: Context, fileName: String): DataSQL? {
        var helper = Helper()
        return helper.parseJSON(readParserFile(context = context, fileName = fileName)!!)
    }

    override fun parseGS1_128(barcode: String): Map<String, String>{
        var helper = Helper()
        return helper.parseGs1_128(barcode)
    }
}