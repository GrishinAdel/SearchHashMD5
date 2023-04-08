package com.adelvanchik.server

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HashSearchService() : Service() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder {
        return object : IHashSearchService.Stub() {
            override fun searchHash(hash: String): Boolean {
                if (getBooleanValueAboutHaveHashInList(hash)) {
                    return true
                } else {
                    insertHash(hash)
                    return false
                }
            }
        }
    }

    private fun insertHash(hash: String) {
        scope.launch {
            contentResolver.insert(
                Uri.parse(URI_INSERT),
                ContentValues().apply {
                    put(HASH_ID, hash)
                })
        }
    }

    fun getBooleanValueAboutHaveHashInList(hash: String): Boolean {
        val uri_check = Uri.parse(URI_IS_HAVE_HASH)
            .buildUpon()
            .appendQueryParameter(KEY_FOR_HASH_VALUE, hash)
            .build()
        val cursor = contentResolver.query(uri_check,
            null, null, null, null, null)
        var isHave = false
        if (cursor != null && cursor.moveToFirst()) {
            isHave = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FOR_HASH_BOOLEAN)) == 1
        }
        cursor?.close()
        return isHave
    }

    companion object {
        private const val HASH_ID = "hash"
        private const val KEY_FOR_HASH_VALUE = "hash_key"
        private const val KEY_FOR_HASH_BOOLEAN = "hash_boolean"
        private const val URI_INSERT = "content://com.adelvanchik.searchhashmd5/insertHash"
        private const val URI_IS_HAVE_HASH = "content://com.adelvanchik.searchhashmd5/checkHash"
    }
}