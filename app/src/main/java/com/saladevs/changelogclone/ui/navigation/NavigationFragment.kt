package com.saladevs.changelogclone.ui.navigation


import android.content.pm.PackageInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saladevs.changelogclone.R
import com.saladevs.changelogclone.ui.details.DetailsActivity
import timber.log.Timber

class NavigationFragment() : Fragment(), NavigationMvpView {

    private lateinit var mPresenter: NavigationPresenter

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: NavigationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPresenter = NavigationPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_navigation, container, false)

        mRecyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        mAdapter = NavigationAdapter()

        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerView.adapter = mAdapter

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mPresenter.attachView(this)

        mAdapter.setOnItemClickListener { view, pi ->
            Timber.d("Click on : ${pi.packageName}")
            mPresenter.onItemClicked(pi)
        }

        mAdapter.setOnItemLongClickListener { view, pi ->
            Timber.d("Long click on : ${pi.packageName}")
            mPresenter.onItemLongClicked(pi)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        mPresenter.detachView()
    }

    override fun showNavigationItems(items: List<NavigationAdapter.NavigationItem>) {
        mAdapter.setData(items);
    }

    override fun startDetailsActivity(packageInfo: PackageInfo) {
        DetailsActivity.startWith(context, packageInfo)
    }
}
