package com.saladevs.changelogclone.ui.search

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.pm.PackageInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import com.jakewharton.rxbinding.support.v4.view.RxMenuItemCompat
import com.jakewharton.rxbinding.support.v7.widget.queryTextChanges
import com.jakewharton.rxbinding.view.MenuItemActionViewEvent
import com.jakewharton.rxbinding.view.clicks
import com.saladevs.changelogclone.App
import com.saladevs.changelogclone.R
import com.saladevs.changelogclone.ui.details.DetailsActivity
import com.saladevs.changelogclone.utils.addTo
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import java.util.concurrent.TimeUnit


class SearchFragment() : Fragment(), SearchMvpView, SearchAdapter.OnItemClickListener {

    private lateinit var mPresenter: SearchPresenter

    private val mSubscriptions = CompositeSubscription()

    private lateinit var mRootView: View
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: SearchAdapter

    private lateinit var mMenuItem: MenuItem
    private lateinit var mSearchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true);
        mPresenter = SearchPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.fragment_search, container, false)

        mRecyclerView = mRootView.findViewById(R.id.recyclerView) as RecyclerView
        mAdapter = SearchAdapter(this)

        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerView.adapter = mAdapter

        return mRootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mPresenter.attachView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)

        mMenuItem = menu.findItem(R.id.action_search)
        mSearchView = MenuItemCompat.getActionView(mMenuItem) as SearchView

        RxMenuItemCompat.actionViewEvents(mMenuItem) { true }
                .subscribe {
                    when (it.kind()) {
                        MenuItemActionViewEvent.Kind.EXPAND -> showSearchList(true)
                        MenuItemActionViewEvent.Kind.COLLAPSE -> showSearchList(false)
                    }
                }

        mSearchView.queryTextChanges()
                .throttleLast(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { mPresenter.onSearchQuery(it) }
                .addTo(mSubscriptions)

        mRootView.clicks()
                .subscribe { mMenuItem.collapseActionView() }
                .addTo(mSubscriptions)

    }

    private fun showSearchList(b: Boolean) {
        mRootView.visibility = View.VISIBLE
        mRootView.animate().alpha(if (b) 1f else 0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (mRootView.alpha == 0f) mRootView.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscriptions.unsubscribe()
        mPresenter.detachView()
        App.getRefWatcher().watch(this)
    }

    override fun onItemClick(result: PackageInfo) {
        mMenuItem.collapseActionView()
        DetailsActivity.startWith(context, result)

    }

    override fun showSearchResults(result: SearchResult) {
        Timber.d("Showing new results (size : ${result.appResults.size})")
        mAdapter.setSearchResult(result)
    }
}