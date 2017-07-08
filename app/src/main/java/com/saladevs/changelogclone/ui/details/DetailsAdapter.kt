package com.saladevs.changelogclone.ui.details

import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.util.SortedListAdapterCallback
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.saladevs.changelogclone.App
import com.saladevs.changelogclone.R
import com.saladevs.changelogclone.model.PackageUpdate

class DetailsAdapter : RecyclerView.Adapter<DetailsAdapter.ViewHolder>() {

    private val mDataset = SortedList(PackageUpdate::class.java, object : SortedListAdapterCallback<PackageUpdate>(this) {
        override fun compare(o1: PackageUpdate, o2: PackageUpdate): Int {
            return o2.date.compareTo(o1.date)
        }

        override fun areContentsTheSame(oldItem: PackageUpdate, newItem: PackageUpdate): Boolean {
            return oldItem.description == newItem.description
        }

        override fun areItemsTheSame(item1: PackageUpdate, item2: PackageUpdate): Boolean {
            return item1.id == item2.id
        }
    })


    fun setData(updates: List<PackageUpdate>) {
        mDataset.beginBatchedUpdates()
        mDataset.addAll(updates)
        mDataset.endBatchedUpdates()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsAdapter.ViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_details_card, parent, false)
        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get elements from dataset
        val update = mDataset.get(position)

        // Replace contents of the view
        holder.subtitle.text = update.version

        holder.title.text = DateUtils.formatDateTime(App.getContext(), update.date.time, 0)
        if (TextUtils.isEmpty(update.description)) {
            holder.support.visibility = View.GONE
        } else {
            holder.support.visibility = View.VISIBLE
            holder.support.text = update.description
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mDataset.size()
    }

    // Provide a reference to the views for each data item
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var title: TextView = v.findViewById(R.id.title) as TextView
        var subtitle: TextView = v.findViewById(R.id.subtitle) as TextView
        var support: TextView = v.findViewById(R.id.support) as TextView
    }

}
