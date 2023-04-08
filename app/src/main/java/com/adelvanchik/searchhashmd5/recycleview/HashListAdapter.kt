package com.adelvanchik.searchhashmd5.recycleview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adelvanchik.searchhashmd5.data.HashEntity
import com.adelvanchik.searchhashmd5.R

class HashListAdapter: ListAdapter<HashEntity,HashListAdapter.HashViewHolder>(HashItemDifUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HashViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.hash_item_pattern,
            parent,
            false
        )
        return HashViewHolder(view)
    }

    override fun onBindViewHolder(holder: HashViewHolder, position: Int) {
        val hashEntity = getItem(position)
        val hash = hashEntity.hash
        holder.hashTV.text = hash
    }

    class HashViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val hashTV: TextView = view.findViewById(R.id.hash_item_tv)
    }
}
