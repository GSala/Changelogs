package com.saladevs.changelogclone.ui.search

import android.content.pm.PackageInfo
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.saladevs.changelogclone.R

/**
 *
 * Changelogs
 * SearchAdapter
 *
 * Created on 03/07/2017
 * Copyright (c) 2017 SHAPE A/S. All rights reserved.
 *
 */
class SearchAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(result: PackageInfo)
    }

    private var apps: List<SearchResult.AppResult> = emptyList()
    private var changelogs: List<SearchResult.ChangelogResult> = emptyList()

    fun setSearchResult(result: SearchResult) {
        apps = result.appResults
        changelogs = result.changelogResults
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        when (viewType) {
            TYPE_HEADER -> {
                val header = LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_search_section, parent, false)
                return HeaderViewHolder(header)
            }
            TYPE_APP -> {
                val appResult = LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_search_app_result, parent, false)
                return AppResultViewHolder(appResult)
            }
            TYPE_CHANGELOG -> TODO("Not implemented yet")
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_HEADER -> bindHeaderViewHolder(holder as HeaderViewHolder, position)
            TYPE_APP -> bindAppViewHolder(holder as AppResultViewHolder, position)
            TYPE_CHANGELOG -> bindChangelogViewHolder(holder as ChangelogViewHolder, position)
        }
    }

    private fun bindHeaderViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.text.text = if (position == 0) "Apps" else "Changelogs"
    }

    private fun bindAppViewHolder(holder: AppResultViewHolder, position: Int) {
        val item = apps.get(position - 1)

        holder.itemView.tag = item.tag
        holder.itemView.setOnClickListener { listener.onItemClick(apps.get(holder.adapterPosition - 1).tag) }

        // Replace contents of the view
        holder.icon.setImageDrawable(item.icon)
        holder.label.text = item.label

    }

    private fun bindChangelogViewHolder(holder: ChangelogViewHolder, position: Int) {
        TODO("Not implemented yet")
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            else -> TYPE_APP
        }
    }

    override fun getItemCount(): Int {
        return if (apps.isEmpty()) 0 else 1 + apps.size
    }

    private class HeaderViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
        internal val text: TextView = v as TextView
    }

    private class AppResultViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
        internal val icon: ImageView = v.findViewById(R.id.icon) as ImageView
        internal val label: TextView = v.findViewById(R.id.label) as TextView
    }

    private class ChangelogViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
        internal val icon: ImageView = v.findViewById(R.id.icon) as ImageView
        internal val label: TextView = v.findViewById(R.id.label) as TextView
    }

    companion object {
        val TYPE_HEADER = 1
        val TYPE_APP = 2
        val TYPE_CHANGELOG = 3
    }
}