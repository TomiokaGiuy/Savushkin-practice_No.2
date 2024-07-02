package com.example.savushkin_practice_no2.Data.Util

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

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

    fun parserXML(xmlString: String?):List<ContentValues> {
        var javaParser = JavaParser()
        return javaParser.parce(xmlString)
    }

    fun parseGs1_128(barcode: String): Map<String, String> {

        val gs1Prefix = "]C1"
        val barcodeData = if (barcode.startsWith(gs1Prefix)) {
            barcode.substring(gs1Prefix.length)
        } else {
            barcode
        }

        val aiLengths = mapOf(
            "01" to 14,
            "02" to 14,
            "37" to null,
            "310" to 6,
            "311" to 6,
            "314" to 6,
            "30" to 8,
            "11" to 6,
            "10" to null,
            "21" to null
        )

        val result = mutableMapOf<String, String>()
        var position = 0

        while (position < barcodeData.length) {
            var ai: String? = null
            var length: Int? = null

            if (position + 4 <= barcodeData.length && aiLengths.containsKey(barcodeData.substring(position, position + 3))) {
                ai = barcodeData.substring(position, position + 3)
                length = aiLengths[barcodeData.substring(position, position + 3)]
                position += 4
            }
            else if (position + 2 <= barcodeData.length && aiLengths.containsKey(barcodeData.substring(position, position + 2))) {
                ai = barcodeData.substring(position, position + 2)
                length = aiLengths[ai]
                position += 2
            } else {
                throw IllegalArgumentException("Неизвестный AI в позиции $position")
            }

            val value: String

            if (length == null) {
                val endPosition = barcodeData.indexOf('\u001D', position)
                if (ai == "21") {
                    if (endPosition == -1) {
                        value = barcodeData.substring(position)
                        position = barcodeData.length
                    } else {
                        value = barcodeData.substring(position, endPosition)
                        position = endPosition
                    }
                } else {
                    value = if (endPosition == -1) {
                        barcodeData.substring(position)
                    } else {
                        barcodeData.substring(position, endPosition)
                    }
                    position += value.length + 1
                }
            } else {
                if (position + length > barcodeData.length) {
                    throw IllegalArgumentException("Недостаточно символов для AI $ai")
                }
                value = barcodeData.substring(position, position + length)
                position += length
            }

            if (ai == "21" && value.endsWith('\u001D')) {
                result[ai] = value.substring(0, value.length - 1)
            } else {
                result[ai] = value
            }

            if (position < barcodeData.length && barcodeData[position] == '\u001D') {
                position++
            }
        }
        return result
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