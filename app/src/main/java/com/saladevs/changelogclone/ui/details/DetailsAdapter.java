package com.saladevs.changelogclone.ui.details;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saladevs.changelogclone.App;
import com.saladevs.changelogclone.R;
import com.saladevs.changelogclone.model.PackageUpdate;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> implements View.OnClickListener {

    public static final String TAG = ".DetailsAdapter";

    private OnItemClickListener onItemClickListener;

    private SortedList<PackageUpdate> mDataset;

    public DetailsAdapter() {
        mDataset = new SortedList<>(PackageUpdate.class, new SortedListAdapterCallback<PackageUpdate>(this) {
            @Override
            public int compare(PackageUpdate o1, PackageUpdate o2) {
                return o2.getDate().compareTo(o1.getDate());
            }

            @Override
            public boolean areContentsTheSame(PackageUpdate oldItem, PackageUpdate newItem) {
                return oldItem.getDescription().equals(newItem.getDescription());
            }

            @Override
            public boolean areItemsTheSame(PackageUpdate item1, PackageUpdate item2) {
                return item1.getId().equals(item2.getId());
            }
        });
    }

    public void setData(List<PackageUpdate> updates) {
        mDataset.beginBatchedUpdates();
        mDataset.addAll(updates);
        mDataset.endBatchedUpdates();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_details_card, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get elements from dataset
        PackageUpdate update = mDataset.get(position);

        // Save position in tag and set onClickListener
        holder.root.setTag(position);
        holder.root.setOnClickListener(this);

        // Replace contents of the view
        if (update.getVersion() != null) {
            holder.subtitle.setText(update.getVersion());
        }
        holder.title.setText(DateUtils.formatDateTime(App.getContext(), update.getDate().getTime(), 0));
        if (TextUtils.isEmpty(update.getDescription())) {
            holder.support.setVisibility(View.GONE);
        } else {
            holder.support.setVisibility(View.VISIBLE);
            holder.support.setText(update.getDescription());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public PackageUpdate getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public void setOnFeedItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public TextView title;
        public TextView subtitle;
        public TextView support;

        public ViewHolder(View v) {
            super(v);
            root = v.findViewById(R.id.root);
            title = (TextView) v.findViewById(R.id.title);
            subtitle = (TextView) v.findViewById(R.id.subtitle);
            support = (TextView) v.findViewById(R.id.support);
        }
    }

}
