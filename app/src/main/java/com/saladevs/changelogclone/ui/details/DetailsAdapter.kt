package com.saladevs.changelogclone.ui.details

import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.util.SortedListAdapterCallback
import android.view.LayoutInflater
import android.view.ViewGroup
import com.saladevs.changelogclone.databinding.RowDetailsCardDetailedBinding
import com.saladevs.changelogclone.databinding.RowDetailsCardSimpleBinding
import com.saladevs.changelogclone.databinding.RowDetailsInstallDateBinding
import com.saladevs.changelogclone.model.PackageUpdate

class DetailsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataset = SortedList(PackageUpdate::class.java, object : SortedListAdapterCallback<PackageUpdate>(this) {
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

    private var viewTypes: List<Int> = emptyList()

    fun setData(updates: List<PackageUpdate>) {
        dataset.beginBatchedUpdates()
        dataset.addAll(updates)
        viewTypes = extractViewTypes(dataset)
        dataset.endBatchedUpdates()
    }

    var footer: String? = null
        set(value) {
            if (field == null) {
                field = value
                notifyItemInserted(dataset.size())
            } else {
                field = value
                notifyItemChanged(dataset.size())
            }
        }

    private fun extractViewTypes(updates: SortedList<PackageUpdate>): List<Int> {
        return MutableList(updates.size()) { updates.get(it) }
                .foldRight("-1".to(mutableListOf<Int>()), { update, acc ->
                    if (update.description == "No description available") {
                        acc.apply { second.add(TYPE_ERROR) }
                    } else if (update.description == acc.first) {
                        acc.apply { second.add(TYPE_SIMPLE) }
                    } else {
                        Pair(update.description, acc.second.apply { add(TYPE_DETAILED) })
                    }
                }).second.reversed()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_FOOTER -> InstallationDateViewHolder(RowDetailsInstallDateBinding.inflate(layoutInflater, parent, false))
            TYPE_SIMPLE, TYPE_ERROR -> SimpleViewHolder(RowDetailsCardSimpleBinding.inflate(layoutInflater, parent, false))
            TYPE_DETAILED -> DetailedViewHolder(RowDetailsCardDetailedBinding.inflate(layoutInflater, parent, false))
            else -> throw IllegalArgumentException("Wrong view type")
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Get elements from dataset
        val isTop = position == 0
        val isBot = false //position == dataset.size() - 1
        when (holder) {
            is InstallationDateViewHolder -> holder.bind(footer!!, isTop)
            is SimpleViewHolder -> {
                val update = dataset.get(position)
                holder.bind(update, isTop, isBot, holder.itemViewType == TYPE_ERROR)
            }
            is DetailedViewHolder -> {
                val update = dataset.get(position)
                val needsExtraSpacing = !isBot && getItemViewType(position + 1) == TYPE_DETAILED
                holder.bind(update, isTop, isBot, needsExtraSpacing)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {

        return dataset.size() + if (footer != null) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        if (footer != null && position == dataset.size()) return TYPE_FOOTER
        return viewTypes[position]
    }

    class InstallationDateViewHolder(val binding: RowDetailsInstallDateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(installationDate: String, top: Boolean) {
            binding.installationDate = installationDate
            binding.top = top
            binding.executePendingBindings()
        }
    }

    class SimpleViewHolder(val binding: RowDetailsCardSimpleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(update: PackageUpdate, top: Boolean, bottom: Boolean, error: Boolean) {
            binding.update = update
            binding.top = top
            binding.bottom = bottom
            binding.error = error
            binding.executePendingBindings()
        }
    }

    class DetailedViewHolder(val binding: RowDetailsCardDetailedBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(update: PackageUpdate, top: Boolean, bottom: Boolean, extraSpacing: Boolean) {
            binding.update = update
            binding.top = top
            binding.bottom = bottom
            binding.extraSpacing = extraSpacing
            binding.executePendingBindings()
        }
    }

    companion object {
        const val TYPE_FOOTER = 100
        const val TYPE_DETAILED = 0
        const val TYPE_SIMPLE = 1
        const val TYPE_ERROR = 2
    }

}
