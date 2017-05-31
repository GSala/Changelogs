package com.saladevs.changelogclone.ui.navigation

import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.util.SortedListAdapterCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.saladevs.changelogclone.R
import com.saladevs.changelogclone.utils.setDisabled


class NavigationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener, View.OnLongClickListener {

    data class NavigationItem(val tag: PackageInfo, val label: CharSequence, val icon: Drawable, val enabled: Boolean)

    private val TYPE_ITEM = 1
    private val TYPE_HEADER = 2

    private var onItemClickListener: ((View, PackageInfo) -> Unit)? = null
    private var onItemLongClickListener: ((View, PackageInfo) -> Unit)? = null

    private var mDataset: SortedList<NavigationItem>

    init {
        mDataset = SortedList(NavigationItem::class.java, object : SortedListAdapterCallback<NavigationItem>(this) {
            override fun compare(o1: NavigationItem, o2: NavigationItem): Int {
                return o1.label.toString().compareTo(o2.label.toString(), true)
            }

            override fun areContentsTheSame(oldItem: NavigationItem, newItem: NavigationItem): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(item1: NavigationItem, item2: NavigationItem): Boolean {
                return item1.tag.packageName == item2.tag.packageName
            }
        })
    }

    fun setData(updates: List<NavigationItem>) {
        mDataset.beginBatchedUpdates()
        mDataset.addAll(updates)
        mDataset.endBatchedUpdates()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        when (viewType) {
            TYPE_HEADER -> {
                val header = LayoutInflater.from(parent.context)
                        .inflate(R.layout.header_navigation, parent, false)
                return HeaderViewHolder(header)
            }
            TYPE_ITEM -> {
                val update = LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_navigation_item, parent, false)
                return NavigationItemViewHolder(update)
            }
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_HEADER -> {
            }
            TYPE_ITEM -> bindUpdateViewHolder(holder as NavigationItemViewHolder, position)
        }
    }

    private fun bindUpdateViewHolder(holder: NavigationItemViewHolder, position: Int) {
        val item = mDataset[position - 1]

        holder.root.tag = item.tag
        holder.root.setOnClickListener(this)
        holder.root.setOnLongClickListener(this)

        // Replace contents of the view
        holder.icon.setImageDrawable(item.icon)
        holder.icon.setDisabled(!item.enabled)

        holder.label.setText(item.label)
        holder.label.isEnabled = item.enabled

    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return mDataset.size() + 1
    }

    override fun onClick(v: View) {
        onItemClickListener?.invoke(v, v.tag as PackageInfo)
    }

    override fun onLongClick(v: View): Boolean {
        onItemLongClickListener?.invoke(v, v.tag as PackageInfo)
        return true
    }

    fun setOnItemClickListener(onItemClickListener: (View, PackageInfo) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: (View, PackageInfo) -> Unit) {
        this.onItemLongClickListener = onItemLongClickListener
    }

    // Provide a reference to the views for each data item
    private class NavigationItemViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {

        internal var root: View
        internal var icon: ImageView
        internal var label: TextView

        init {
            root = v.findViewById(R.id.root)
            icon = v.findViewById(R.id.icon) as ImageView
            label = v.findViewById(R.id.label) as TextView
        }
    }

    private class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v)

}