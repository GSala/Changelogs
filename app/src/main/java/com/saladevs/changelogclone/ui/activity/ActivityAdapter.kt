package com.saladevs.changelogclone.ui.activity

import android.content.pm.PackageInfo
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.saladevs.changelogclone.AppManager
import com.saladevs.changelogclone.R
import com.saladevs.changelogclone.StylesManager
import com.saladevs.changelogclone.model.ActivityChangelogStyle
import com.saladevs.changelogclone.model.ActivityChangelogStyle.BASIC
import com.saladevs.changelogclone.model.ActivityChangelogStyle.SHORT
import com.saladevs.changelogclone.model.PackageUpdate
import java.util.*

internal class ActivityAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    private var onItemClickListener: OnItemClickListener? = null

    private var mDataset: List<PackageUpdate> = emptyList()
    private var mHeaders: Map<Int, String> = emptyMap()


    fun setData(updates: List<PackageUpdate>) {
        mDataset = updates
        mHeaders = extractHeaders(mDataset)
        notifyDataSetChanged()
    }

    var changelogStyle: ActivityChangelogStyle = StylesManager.activityChangelogStyle
        set(value) {
            field = value
            // Notify item changed for every PackageUpdate row
            (0..itemCount).filter { !mHeaders.containsKey(it) }
                    .forEach { notifyItemChanged(it, value) }
        }

    private fun extractHeaders(updates: List<PackageUpdate>): Map<Int, String> {
        val headers = HashMap<Int, String>()
        updates.map { getSectionTitle(it) }
                .withIndex()
                .distinctBy { it.value }
                .forEach { headers.put(it.index + headers.size, it.value) }

        return headers
    }

    private fun getSectionTitle(u: PackageUpdate): String {
        return DateUtils.getRelativeTimeSpanString(u.date.time,
                Date().time, DateUtils.DAY_IN_MILLIS, 0).toString()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        when (viewType) {
            TYPE_HEADER -> {
                val header = LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_activity_section, parent, false)
                return HeaderViewHolder(header)
            }
            TYPE_UPDATE -> {
                val update = LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_activity_card, parent, false)
                return UpdateViewHolder(update)
            }
        }
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindViewHolder(holder, position, emptyList<Any>())
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<*>) {
        when (holder.itemViewType) {
            TYPE_HEADER -> (holder as HeaderViewHolder).bindTo(mHeaders[position] ?: "")
            TYPE_UPDATE -> bindUpdateViewHolder(holder as UpdateViewHolder, position, payloads)
        }
    }

    private fun bindUpdateViewHolder(holder: UpdateViewHolder, position: Int, payloads: List<*>) {
        val update = mDataset[getDatasetPosition(position)]

        if (payloads.isEmpty()) {
            val packageInfo = AppManager.getPackageInfo(update.packageName)

            if (packageInfo == null) {
                emptyViewHolder(holder)
                return
            }
            // Save position in tag and set onClickListener
            holder.root.tag = packageInfo
            holder.root.setOnClickListener(this)

            // Replace contents of the view
            holder.primaryText.text = AppManager.getAppLabel(packageInfo)
            holder.secondaryText.text = update.version
            holder.icon.setImageDrawable(AppManager.getAppIcon(packageInfo))
            holder.description.text = update.description
        }


        if (changelogStyle == BASIC || TextUtils.isEmpty(update.description)) {
            holder.description.visibility = View.GONE
        } else if (changelogStyle == SHORT) {
            holder.description.maxLines = 3
            holder.description.visibility = View.VISIBLE
        } else {
            holder.description.maxLines = Integer.MAX_VALUE
            holder.description.visibility = View.VISIBLE
        }
    }

    private fun emptyViewHolder(holder: UpdateViewHolder) {
        holder.primaryText.text = ""
        holder.secondaryText.text = ""
        holder.icon.setImageDrawable(null)
        holder.description.text = ""

    }

    override fun getItemViewType(position: Int): Int {
        return if (mHeaders.containsKey(position)) TYPE_HEADER
        else TYPE_UPDATE
    }

    override fun getItemCount(): Int {
        return mDataset.size + mHeaders.size
    }

    private fun getDatasetPosition(adapterPosition: Int): Int {
        val offset = mHeaders.keys.count { adapterPosition > it }
        return adapterPosition - offset
    }

    override fun onClick(v: View) {
        if (onItemClickListener != null) {
            onItemClickListener!!.onItemClick(v, v.tag as PackageInfo)
        }
    }

    fun setOnFeedItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    internal interface OnItemClickListener {
        fun onItemClick(v: View, packageInfo: PackageInfo)
    }

    // Provide a reference to the views for each data item
    private class UpdateViewHolder internal constructor(v: View) : RecyclerView.ViewHolder(v) {
        internal var root: View = v.findViewById(R.id.root)
        internal var primaryText: TextView = v.findViewById(R.id.textPrimary) as TextView
        internal var secondaryText: TextView = v.findViewById(R.id.textSecondary) as TextView
        internal var icon: ImageView = v.findViewById(R.id.icon) as ImageView
        internal var description: TextView = v.findViewById(R.id.description) as TextView
    }

    private class HeaderViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var headerView: TextView = itemView as TextView

        internal fun bindTo(header: String) {
            headerView.text = header
        }

    }

    companion object {
        private val TYPE_UPDATE = 1
        private val TYPE_HEADER = 2
    }

}
