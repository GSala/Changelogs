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
import com.saladevs.changelogclone.R
import com.saladevs.changelogclone.ui.details.DetailsActivity
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 *
 * Changelogs
 * SearchFragment
 *
 * Created on 03/07/2017
 * Copyright (c) 2017 SHAPE A/S. All rights reserved.
 *
 */
class SearchFragment() : Fragment(), SearchMvpView, SearchAdapter.OnItemClickListener {

    private lateinit var mPresenter: SearchPresenter

    private lateinit var mRootView: View
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: SearchAdapter

    private lateinit var mSearchView: SearchView

    private var savedStateQuery: CharSequence? = null

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

        savedStateQuery = savedInstanceState?.getCharSequence("testing", null)

        return mRootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mPresenter.attachView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)

        val menuItem = menu.findItem(R.id.action_search)
        mSearchView = MenuItemCompat.getActionView(menuItem) as SearchView

        RxMenuItemCompat.actionViewEvents(menuItem) { true }
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

        savedStateQuery?.let {
            menuItem.expandActionView()
            mSearchView.setQuery(savedStateQuery, true)
        }

        mRootView.clicks().subscribe {
            menuItem.collapseActionView()
        }

    }

    private fun showSearchList(b: Boolean) {
        mRootView.visibility = View.VISIBLE
        mRootView.animate().alpha(if (b) 1f else 0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (mRootView.alpha == 0f) mRootView.visibility = View.GONE
            }
        })
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putCharSequence("testing", mSearchView.query)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        mPresenter.detachView()
    }

    override fun onItemClick(result: PackageInfo) {
        DetailsActivity.startWith(context, result)

    }

    override fun showSearchResults(result: SearchResult) {
        Timber.d("Showing new results (size : ${result.appResults.size})")
        mAdapter.setSearchResult(result)
    }
}