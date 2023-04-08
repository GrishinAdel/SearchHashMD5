package com.adelvanchik.searchhashmd5.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [HashEntity::class], version = 1, exportSchema = true )
abstract class AppDatabase: RoomDatabase() {

    abstract fun HashListDao(): HashListDao

    companion object {

        private var INSTANCE: AppDatabase? = null
        private val LOCK = Any()
        private const val NAME_DB = "hash_db"

        fun getInstance(context: Context): AppDatabase {

            INSTANCE?.let {
                return it
            }

            synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }

                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    NAME_DB,
                ).build()
                INSTANCE = db
                return db

            }
        }
    }
}