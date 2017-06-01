package com.saladevs.changelogclone.ui.navigation

import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.support.v7.util.DiffUtil
import android.support.v7.util.ListUpdateCallback
import android.support.v7.widget.RecyclerView
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

    private var mDataset = emptyList<NavigationItem>()//SortedList<NavigationItem>

    fun setData(updates: List<NavigationItem>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return mDataset.size
            }

            override fun getNewListSize(): Int {
                return updates.size
            }

            override fun areItemsTheSame(old: Int, new: Int): Boolean {
                return mDataset[old].tag.packageName == updates[new].tag.packageName
            }

            override fun areContentsTheSame(old: Int, new: Int): Boolean {
                return mDataset[old].enabled == updates[new].enabled
            }
        })
        mDataset = updates
        diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onChanged(position: Int, count: Int, payload: Any?) {
                notifyItemChanged(position + 1, payload)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition + 1, toPosition + 1)
            }

            override fun onInserted(position: Int, count: Int) {
                notifyItemInserted(position + 1)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRemoved(position + 1)
            }

        })

        notifyItemChanged(0, Any())
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        when (viewType) {
            TYPE_HEADER -> {
                val header = LayoutInflater.from(parent.context)
                        .inflate(R.layout.header_navigation, parent, false)
                return NavigationHeaderViewHolder(header)
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
            TYPE_HEADER -> bindHeaderViewHolder(holder as NavigationHeaderViewHolder)
            TYPE_ITEM -> bindItemViewHolder(holder as NavigationItemViewHolder, position)
        }
    }

    private fun bindHeaderViewHolder(holder: NavigationHeaderViewHolder) {
        holder.primaryText.text = "${mDataset.size} installed"
        holder.primaryText.visibility = if (mDataset.isEmpty()) View.GONE else View.VISIBLE

        val ignoredCount = mDataset.count { !it.enabled }
        holder.secondaryText.text = "$ignoredCount ignored"
        holder.secondaryText.visibility = if (mDataset.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun bindItemViewHolder(holder: NavigationItemViewHolder, position: Int) {
        val item = mDataset[position - 1]

        holder.itemView.tag = item.tag
        holder.itemView.setOnClickListener(this)
        holder.itemView.setOnLongClickListener(this)

        // Replace contents of the view
        holder.icon.setImageDrawable(item.icon)
        holder.icon.setDisabled(!item.enabled)

        holder.label.text = item.label
        holder.label.isEnabled = item.enabled

    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return mDataset.size + 1
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
        internal val icon: ImageView = v.findViewById(R.id.icon) as ImageView
        internal val label: TextView = v.findViewById(R.id.label) as TextView
    }

    private class NavigationHeaderViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
        internal val primaryText = v.findViewById(R.id.primaryText) as TextView
        internal val secondaryText = v.findViewById(R.id.secondaryText) as TextView
    }

}