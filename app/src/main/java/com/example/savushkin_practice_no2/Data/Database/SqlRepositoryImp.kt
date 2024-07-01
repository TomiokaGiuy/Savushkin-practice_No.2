package com.example.savushkin_practice_no2.Data.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.savushkin_practice_no2.Domain.Repository.SqlRepository
import com.example.savushkin_practice_no2.Presentation.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class SqlRepositoryImp(val context: Context): SqlRepository {

    private val dbHelper = SqlHelper(context)
    var db: SQLiteDatabase? = null
    private var openCounter = 0


    private val dbMutex = Mutex()


    override suspend fun insertData(
        dataList: List<ContentValues>,
        TABLE_NAME: String,
        viewModel: MainViewModel
    ) {
        dbMutex.withLock {
            openDB()
            var count = 0
            try {
                db?.beginTransaction()
                dataList.forEachIndexed { index, values ->
                    try {
                        count++
                        val progress = (count.toFloat() / dataList.size.toFloat()) * 100f
                        viewModel.updateValueLoading(progress.toInt())
                        Log.d(
                            "insertData",
                            "$count/${dataList.size} progress $progress inserted $values"
                        )
                        db?.insertOrThrow(TABLE_NAME, null, values)
                    } catch (e: Exception) {
                        Log.e("DatabaseError", "Error while inserting data", e)
                    }
                }
                db?.setTransactionSuccessful()
            } finally {
                db?.endTransaction()
                closeDb()
                viewModel.decreaseTasksCompleted()
            }
        }
    }

    override fun getData(
        tableName: String,
        selectedColumns: Map<String, String?>,
        listColumnsForReturn: List<String>?,
        viewModel: MainViewModel
    ): LiveData<List<ContentValues>> {
        val liveData = MutableLiveData<List<ContentValues>>()
        openDB()
        viewModel.setLoading(true)

        CoroutineScope(Dispatchers.IO).launch {
            val dataList = mutableListOf<ContentValues>()
            var cursor: Cursor? = null
            try {
                val columns = if (listColumnsForReturn.isNullOrEmpty()) null else listColumnsForReturn.toTypedArray()
                val selection: String?
                val selectionArgs: Array<String>?

                if (selectedColumns.isNotEmpty()) {
                    val selectionBuilder = StringBuilder()
                    val selectionArgsList = mutableListOf<String>()

                    for ((key, value) in selectedColumns) {
                        if (selectionBuilder.isNotEmpty()) {
                            selectionBuilder.append(" AND ")
                        }
                        selectionBuilder.append("$key=?")
                        value?.let {
                            selectionArgsList.add(it)
                        }
                    }

                    selection = selectionBuilder.toString()
                    selectionArgs = selectionArgsList.toTypedArray()
                } else {
                    selection = null
                    selectionArgs = null
                }

                cursor = dbHelper.readableDatabase.query(
                    tableName,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
                )

                while (cursor.moveToNext()) {
                    val values = ContentValues()
                    for (i in 0 until cursor.columnCount) {
                        when (cursor.getType(i)) {
                            Cursor.FIELD_TYPE_INTEGER -> values.put(cursor.getColumnName(i), cursor.getInt(i))
                            Cursor.FIELD_TYPE_FLOAT -> values.put(cursor.getColumnName(i), cursor.getFloat(i))
                            Cursor.FIELD_TYPE_STRING -> values.put(cursor.getColumnName(i), cursor.getString(i))
                            Cursor.FIELD_TYPE_BLOB -> values.put(cursor.getColumnName(i), cursor.getBlob(i))
                            Cursor.FIELD_TYPE_NULL -> values.putNull(cursor.getColumnName(i))
                        }
                    }
                    dataList.add(values)
                }
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } finally {
                cursor?.close()
                closeDb()
            }

            withContext(Dispatchers.Main) {
                liveData.value = dataList
                viewModel.setLoading(false)
            }
        }
        return liveData
    }



    override suspend fun delete(tableName: String, selectedColumn: String?, selectedValue: String?) {
        openDB()
        try {
            val db = dbHelper.writableDatabase
            if (selectedColumn == null || selectedValue == null) {
                db.delete(tableName, null, null)
                Log.d("SqlRepositoryImp", "Deleted all rows from $tableName")
            } else {
                val whereClause = "$selectedColumn = ?"
                val whereArgs = arrayOf(selectedValue)
                db.delete(tableName, whereClause, whereArgs)
                Log.d("SqlRepositoryImp", "Deleted rows from $tableName where $selectedColumn = $selectedValue")
            }
        } catch (e: SQLiteException) {
            Log.e("SqlRepositoryImp", "Error deleting rows from $tableName", e)
        } finally {
            closeDb()
        }
    }

    override fun createTable(CREATE_TABLE: String) {
        openDB()
        db?.execSQL(CREATE_TABLE)
        Log.d("SqlRepositoryImp", "Table created successfully")
        closeDb()
    }


    override suspend fun getListTableFields(tableName: String): List<String> {
        openDB()
        val fieldNames = mutableListOf<String>()

        db!!.rawQuery("PRAGMA table_info($tableName)", null).use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")

            while (cursor.moveToNext()) {
                if (nameIndex != -1) {
                    val name = cursor.getString(nameIndex)
                    if (name!="id"){
                        fieldNames.add(name)
                    }
                } else {
                    throw IllegalStateException("Column not found ")
                }
            }
        }
        closeDb()
        return fieldNames
    }

    override suspend fun getListTable(): List<String> {
        openDB()
        val cursor = db!!.rawQuery(MyNameSQLite.GET_LIST_TABLE, null)
        val tableNames = mutableListOf<String>()

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                tableNames.add(cursor.getString(0))
                cursor.moveToNext()
            }
        }

        cursor.close()
        closeDb()
        return tableNames
    }


    private fun openDB() {
        if (db == null || !db!!.isOpen) {
            db = dbHelper.writableDatabase
        }
        openCounter++
    }

    private fun closeDb() {
        openCounter--
        if (openCounter == 0) {
            dbHelper.close()
            db = null
        }
    }
}