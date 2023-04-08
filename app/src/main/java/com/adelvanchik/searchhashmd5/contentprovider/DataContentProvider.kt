package com.adelvanchik.searchhashmd5.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.adelvanchik.searchhashmd5.data.AppDatabase
import com.adelvanchik.searchhashmd5.data.HashEntity
import com.adelvanchik.searchhashmd5.data.HashListDao
import kotlinx.coroutines.*

class DataContentProvider: ContentProvider() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var hashListDao: HashListDao
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI("com.adelvanchik.searchhashmd5","insertHash", INSERT_HASH_CODE)
        addURI("com.adelvanchik.searchhashmd5","checkHash", IS_HAVE_HASH_CODE)
    }

    companion object {
        private const val KEY_FOR_HASH = HashEntity.HASH_ID
        private const val KEY_FOR_HASH_VALUE = "hash_key"
        private const val KEY_FOR_HASH_BOOLEAN = "hash_boolean"
        private const val INSERT_HASH_CODE = 12
        private const val IS_HAVE_HASH_CODE = 13
    }

    override fun onCreate(): Boolean {
        hashListDao = AppDatabase.getInstance(context!!).HashListDao()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {
        when(uriMatcher.match(uri)) {
            IS_HAVE_HASH_CODE -> {
                val value = uri.getQueryParameter(KEY_FOR_HASH_VALUE)
                val isHaveHash = runBlocking { isHaveHashAsync(value!!) }
                val cursor = MatrixCursor(arrayOf(KEY_FOR_HASH_BOOLEAN))
                val intBooleanForCursor = if (isHaveHash) 1 else 0
                cursor.addRow(arrayOf(intBooleanForCursor))
                return cursor
            }
            else -> return null
        }
    }

    private suspend fun isHaveHashAsync(value: String): Boolean {
        return withContext(Dispatchers.IO) {
            hashListDao.isHaveHash(value!!)
        }
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when(uriMatcher.match(uri)) {
            INSERT_HASH_CODE -> {
                scope.launch {
                    val hash = values!!.getAsString(KEY_FOR_HASH)
                    val hashEntity = HashEntity(hash)
                    hashListDao.insertHash(hashEntity)
                }

            }
        }

        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        TODO("Not yet implemented")
    }
}