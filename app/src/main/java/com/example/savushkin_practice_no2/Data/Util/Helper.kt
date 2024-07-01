package com.example.savushkin_practice_no2.Data.Util

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.savushkin_practice_no2.Domain.Models.DataSQL
import com.example.savushkin_practice_no2.Domain.Models.NS_SEMK
import com.example.savushkin_practice_no2.Domain.Models.Root
import com.google.gson.GsonBuilder
import org.xml.sax.SAXException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.xml.parsers.ParserConfigurationException

class Helper {

    fun readFile(context: Context, fileName: String ): String? {
        try {
            context.assets.open(fileName).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    BufferedReader(reader).use { bufferedReader ->
                        val stringBuilder = StringBuilder()
                        var line: String?
                        while (bufferedReader.readLine().also { line = it } != null) {
                            stringBuilder.append(line)
                        }
                        Log.d("File", stringBuilder.toString())
                        return stringBuilder.toString()
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("JsonManager", "Error reading JSON from assets", e)
            return null
        }
    }
    fun parserXML(context: Context, fileName: String):List<ContentValues> {
        val buf: List<ContentValues> = listOf()
        var java = Java()
        return java.parce(context.assets.open(fileName))
    }



    inline fun <reified T> parseJSON(jsonString: String): T? {
        return try {
            val gson = GsonBuilder().create()
            val request = gson.fromJson(jsonString, T::class.java)
            request
        } catch (e: Exception) {
            Log.e("JsonManager", "Error PARSING", e)
            null
        }
    }

}