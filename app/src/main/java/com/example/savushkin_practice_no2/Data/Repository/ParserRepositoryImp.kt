package com.example.savushkin_practice_no2.Data.Repository

import android.content.ContentValues
import android.content.Context
import com.example.savushkin_practice_no2.Data.Util.Helper
import com.example.savushkin_practice_no2.Domain.Models.DataSQL
import com.example.savushkin_practice_no2.Domain.Models.NS_SEMK
import com.example.savushkin_practice_no2.Domain.Repository.ParserRepository

class ParserRepositoryImp(): ParserRepository {
    override fun parserXML(context: Context, fileName: String):  List<ContentValues>  {
        var helper = Helper()
        return helper.parserXML(context, fileName)
    }

    override fun readParserFile(context: Context, fileName: String): String? {
        var helper = Helper()
        return helper.readFile(context, fileName)
    }

    override fun parseJSON(context: Context, fileName: String):  List<NS_SEMK>? {
        var helper = Helper()
        return helper.parseJSON(readParserFile(context, fileName)!!)
    }

    override fun readRequest(context: Context, fileName: String): DataSQL? {
        val helper = Helper()
        return helper.parseJsonRequest(readParserFile(context, fileName)!!)
    }
}