package com.adelvanchik.searchhashmd5.recycleview

import androidx.recyclerview.widget.DiffUtil
import com.adelvanchik.searchhashmd5.data.HashEntity

class HashItemDifUtilCallback: DiffUtil.ItemCallback<HashEntity>()  {
    override fun areItemsTheSame(oldItem: HashEntity, newItem: HashEntity): Boolean {
        return oldItem.hash == newItem.hash
    }

    override fun areContentsTheSame(oldItem: HashEntity, newItem: HashEntity): Boolean {
        return oldItem == newItem
    }
}