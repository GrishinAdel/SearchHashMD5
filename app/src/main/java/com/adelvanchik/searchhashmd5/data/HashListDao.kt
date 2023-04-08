package com.adelvanchik.searchhashmd5.data


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HashListDao {

    @Query("SELECT * FROM ${HashEntity.TABLE_NAME}")
    fun getHashList(): LiveData<List<HashEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHash(hash: HashEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(hash : List<HashEntity>)

    @Query("DELETE FROM ${HashEntity.TABLE_NAME}")
    fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM ${HashEntity.TABLE_NAME} WHERE hash=:hashValue)")
    fun isHaveHash(hashValue: String): Boolean

}