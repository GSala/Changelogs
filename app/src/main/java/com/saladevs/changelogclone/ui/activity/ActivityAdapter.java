package com.saladevs.changelogclone.ui.activity;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saladevs.changelogclone.R;
import com.saladevs.changelogclone.model.PackageUpdate;
import com.saladevs.changelogclone.utils.PackageUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

class ActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int TYPE_UPDATE = 1;
    private static final int TYPE_HEADER = 2;

    static final int CHANGELOG_STYLE_BASIC = 0;
    static final int CHANGELOG_STYLE_SHORT = 1;
    static final int CHANGELOG_STYLE_FULL = 2;

    private OnItemClickListener onItemClickListener;

    private List<PackageUpdate> mDataset;
    private Map<Integer, String> mHeaders;

    private int mChangelogStyle;

    public void setData(List<PackageUpdate> updates) {
        mDataset = updates;
        mHeaders = extractHeaders(mDataset);
        notifyDataSetChanged();
    }

    void setChangelogStyle(int style) {
        mChangelogStyle = style;
        // Notify item changed for every PackageUpdate row
        Observable.range(0, getItemCount())
                .filter(i -> !mHeaders.containsKey(i))
                .subscribe(this::notifyItemChanged);
    }

    private Map<Integer, String> extractHeaders(List<PackageUpdate> updates) {
        Map<Integer, String> headers = new HashMap<>();
        for (int i = 0; i < updates.size(); i++) {
            PackageUpdate pu = updates.get(i);
            String header = getSectionTitle(pu);
            if (!headers.containsValue(header)) {
                headers.put(i + headers.size(), header);
            }
        }
        return headers;
    }

    private String getSectionTitle(PackageUpdate u) {
        return DateUtils.getRelativeTimeSpanString(u.getDate().getTime(),
                new Date().getTime(), DateUtils.DAY_IN_MILLIS, 0).toString();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                View header = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_activity_section, parent, false);
                return new HeaderViewHolder(header);
            case TYPE_UPDATE:
                View update = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_activity_card, parent, false);
                return new UpdateViewHolder(update);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_HEADER:
                ((HeaderViewHolder) holder).bindTo(mHeaders.get(position));
                break;
            case TYPE_UPDATE:
                ((UpdateViewHolder) holder).bindTo(mDataset.get(getDatasetPosition(position)), this, mChangelogStyle);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaders.containsKey(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_UPDATE;
        }
    }

    @Override
    public int getItemCount() {
        return getDataItemCount() + getHeaderCount();
    }

    private int getDataItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    private int getHeaderCount() {
        return mHeaders != null ? mHeaders.size() : 0;
    }

    private int getDatasetPosition(int adapterPosition) {
        int offset = 0;
        for (Integer i : mHeaders.keySet()) {
            if (adapterPosition > i) {
                offset++;
            }
        }
        return adapterPosition - offset;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (PackageInfo) v.getTag());
        }
    }

    void setOnFeedItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    interface OnItemClickListener {
        void onItemClick(View v, PackageInfo packageInfo);
    }

    // Provide a reference to the views for each data item
    private static class UpdateViewHolder extends RecyclerView.ViewHolder {

        View root;
        TextView primaryText;
        TextView secondaryText;
        public ImageView icon;
        TextView description;

        UpdateViewHolder(View v) {
            super(v);
            root = v.findViewById(R.id.root);
            primaryText = (TextView) v.findViewById(R.id.textPrimary);
            secondaryText = (TextView) v.findViewById(R.id.textSecondary);
            icon = (ImageView) v.findViewById(R.id.icon);
            description = (TextView) v.findViewById(R.id.description);

        }

        void bindTo(PackageUpdate update, View.OnClickListener listener, int changelogStyle) {
            PackageInfo packageInfo = PackageUtils.getPackageInfo(update.getPackageName());

            CharSequence appName = PackageUtils.getAppLabel(packageInfo);
            Drawable appIcon = PackageUtils.getAppIconDrawable(packageInfo);

            // Save position in tag and set onClickListener
            root.setTag(packageInfo);
            root.setOnClickListener(listener);

            // Replace contents of the view
            primaryText.setText(appName);
            secondaryText.setText(update.getVersion());
            icon.setImageDrawable(appIcon);
            description.setText(update.getDescription());


            if (changelogStyle == CHANGELOG_STYLE_BASIC || TextUtils.isEmpty(update.getDescription())) {
                description.setVisibility(View.GONE);
            } else if (changelogStyle == CHANGELOG_STYLE_SHORT){
                description.setMaxLines(3);
                description.setEllipsize(TextUtils.TruncateAt.END);
                description.setVisibility(View.VISIBLE);
            } else {
                description.setMaxLines(Integer.MAX_VALUE);
                description.setVisibility(View.VISIBLE);
            }
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView headerView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            headerView = (TextView) itemView;
        }

        void bindTo(String header) {
            headerView.setText(header);
        }

    }

}
