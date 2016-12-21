package com.saladevs.changelogclone.ui.activity;


import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saladevs.changelogclone.R;
import com.saladevs.changelogclone.model.PackageUpdate;
import com.saladevs.changelogclone.ui.details.DetailsActivity;

import java.util.List;

import static com.saladevs.changelogclone.R.id.recyclerView;

public class ActivityFragment extends Fragment implements ActivityMvpView, ActivityAdapter.OnItemClickListener {

    private ActivityPresenter mPresenter;

    private View mEmptyStateView;

    private RecyclerView mRecyclerView;
    private ActivityAdapter mAdapter;

    public ActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new ActivityPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        mEmptyStateView = view.findViewById(R.id.emptyStateView);
        mRecyclerView = (RecyclerView) view.findViewById(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new ActivityAdapter();
        mAdapter.setOnFeedItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        mRecyclerView.addItemDecoration(decoration);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.attachView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mPresenter.detachView();
    }


    @Override
    public void onItemClick(View v, PackageInfo packageInfo) {
        mPresenter.onItemClicked(packageInfo);
    }

    @Override
    public void showEmptyState(boolean b) {
        mEmptyStateView.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showUpdates(List<PackageUpdate> updates) {
        mAdapter.setData(updates);
    }

    @Override
    public void startDetailsActivity(PackageInfo packageInfo) {
        DetailsActivity.startWith(getActivity(), packageInfo);
    }

}
