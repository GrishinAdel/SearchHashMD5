package com.adelvanchik.searchhashmd5.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.adelvanchik.searchhashmd5.data.AppDatabase
import com.adelvanchik.searchhashmd5.data.HashEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExportImportViewModel(application: Application) : AndroidViewModel(application) {
    private val hashListDao = AppDatabase.getInstance(application).HashListDao()

    private val _hashListLiveData = hashListDao.getHashList()
    val hashListLiveData: LiveData<List<HashEntity>>
        get() = _hashListLiveData

    fun clearList() {
        viewModelScope.launch(Dispatchers.IO) {
            hashListDao.deleteAll()
        }
    }
    fun addHashList(list: List<HashEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            hashListDao.insertAll(list)
        }

    }
}