package com.example.savushkin_practice_no2.Data.Database

object MyNameSQLite{
    const val COLUMN_KMC = "KMC"
    const val COLUMN_KRK = "KRK"
    const val COLUMN_KT = "KT"
    const val COLUMN_EMK = "EMK"
    const val COLUMN_PR = "PR"
    const val COLUMN_KTARA = "KTARA"
    const val COLUMN_GTIN = "GTIN"
    const val COLUMN_EMKPOD = "EMKPOD"

    const val GET_LIST_TABLE = "SELECT name FROM sqlite_master WHERE type='table' AND name NOT IN ('sqlite_sequence', 'android_metadata')"
    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "app_database.db"
    const val CREATE_TABLE = " CREATE TABLE IF NOT EXISTS NS_SEMK (id INTEGER PRIMARY KEY AUTOINCREMENT, KMC TEXT, KRK TEXT, KT TEXT, EMK TEXT, PR TEXT, KTARA TEXT, GTIN TEXT, EMKPOD TEXT)"

    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS NS_SEMK"
}