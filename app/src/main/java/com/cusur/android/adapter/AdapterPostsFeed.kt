package com.cusur.android.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cusur.android.R
import com.cusur.android.dataclass.Publication
import kotlinx.android.synthetic.main.feed_item.view.*

class AdapterPostsFeed(var publications: MutableList<Publication>) : RecyclerView.Adapter<AdapterPostsFeed.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPostsFeed.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: AdapterPostsFeed.ViewHolder, position: Int) {
        holder.bindItems(publications[position])
    }

    override fun getItemCount(): Int {
        return publications.size
    }

    fun add(publication: Publication) {
        publications.add(0, publication)
        notifyItemInserted(0)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(publication: Publication) {
            // itemView.ivPostPicture
            itemView.tvDateCreated.text = publication.dateCreated.toString()
            itemView.tvPostComment.text = publication.comment
        }
    }
}