package com.saladevs.changelogclone.ui.details;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.saladevs.changelogclone.R;
import com.saladevs.changelogclone.model.PackageUpdate;

import java.util.List;

import static android.service.notification.Condition.SCHEME;

public class DetailsFragment extends Fragment implements DetailsMvpView {

    private static final String PARAM_PACKAGE = "package_info";
    private static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=";

    private PackageInfo mPackageInfo;

    private DetailsPresenter mPresenter;

    private View mEmptyStateView;

    private RecyclerView mRecyclerView;
    private DetailsAdapter mAdapter;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance(PackageInfo pi) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARAM_PACKAGE, pi);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPackageInfo = getArguments().getParcelable(PARAM_PACKAGE);
            mPresenter = new DetailsPresenter(mPackageInfo.packageName);
        } else {
            throw new IllegalArgumentException("Must provide PackageInfo to DetailsFragment");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        mEmptyStateView = view.findViewById(R.id.emptyStateView);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DetailsAdapter();
        mRecyclerView.setAdapter(mAdapter);

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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_app:
                startPackageActivity(mPackageInfo.packageName);
                return true;
            case R.id.action_open_store:
                startPlayStoreActivity(mPackageInfo.packageName);
                return true;
            case R.id.action_open_info:
                startPackageInfoActivity(mPackageInfo.packageName);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startPlayStoreActivity(String packageName) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL + packageName)));
    }

    private void startPackageActivity(String packageName) {
        Intent i = getContext().getPackageManager().getLaunchIntentForPackage(packageName);
        if (i == null) {
            Snackbar.make(mRecyclerView, R.string.cant_open_app, Snackbar.LENGTH_SHORT).show();
        } else {
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
        }
    }

    private void startPackageInfoActivity(String packageName) {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(Uri.fromParts("package", packageName, null));
        startActivity(i);
    }

    @Override
    public void showEmptyState(boolean b) {
        mEmptyStateView.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showUpdates(List<PackageUpdate> updates) {
        mAdapter.setData(updates);
    }

}
