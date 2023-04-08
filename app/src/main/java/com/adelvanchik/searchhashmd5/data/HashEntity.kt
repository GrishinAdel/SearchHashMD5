package com.adelvanchik.searchhashmd5.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = HashEntity.TABLE_NAME)
data class HashEntity(
    @PrimaryKey
    val hash: String
    ) {
    companion object {
        const val TABLE_NAME = "table_name"
        const val HASH_ID = "hash"
    }
}
