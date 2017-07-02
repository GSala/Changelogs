package com.saladevs.changelogclone.ui.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import com.jakewharton.rxbinding.support.v4.view.RxMenuItemCompat
import com.jakewharton.rxbinding.view.MenuItemActionViewEvent
import com.saladevs.changelogclone.R


/**
 *
 * Changelogs
 * SearchFragment
 *
 * Created on 03/07/2017
 * Copyright (c) 2017 SHAPE A/S. All rights reserved.
 *
 */
class SearchFragment() : Fragment(), SearchMvpView {

    private lateinit var mPresenter: SearchPresenter

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true);
        mPresenter = SearchPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        mRecyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        mAdapter = SearchAdapter()

        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        // TODO mRecyclerView.adapter = mAdapter

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mPresenter.attachView(this)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView

        RxMenuItemCompat.actionViewEvents(searchItem) { true }
                .subscribe {
                    when (it.kind()) {
                        MenuItemActionViewEvent.Kind.EXPAND -> mRecyclerView.setBackgroundColor(0x80000000.toInt())
                        MenuItemActionViewEvent.Kind.COLLAPSE -> mRecyclerView.setBackgroundResource(android.R.color.transparent)
                    }
                }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroyView() {
        super.onDestroyView()

        mPresenter.detachView()
    }
}