package com.saladevs.changelogclone.ui.search

import com.saladevs.changelogclone.App
import com.saladevs.changelogclone.ui.BasePresenter
import com.saladevs.changelogclone.utils.getIcon
import com.saladevs.changelogclone.utils.getLabel
import com.saladevs.changelogclone.utils.getPlayStorePackages

/**
 *
 * Changelogs
 * SearchPresenter
 *
 * Created on 03/07/2017
 * Copyright (c) 2017 SHAPE A/S. All rights reserved.
 *
 */
class SearchPresenter : BasePresenter<SearchMvpView>() {

    override fun attachView(mvpView: SearchMvpView?) {
        super.attachView(mvpView)


    }

    override fun detachView() {
        super.detachView()
    }

    fun onSearchQuery(query: CharSequence) {
        if (query.isBlank()) {
            mvpView?.showSearchResults(SearchResult())
        } else {
            val appResults = App.getContext().packageManager
                    .getPlayStorePackages()
                    .filter { it.getLabel().contains(query.trim(), true) }
                    .map { SearchResult.AppResult(it, it.getLabel(), it.getIcon()) }
                    .sortedBy { it.label.toString().toLowerCase() }

            mvpView?.showSearchResults(SearchResult(appResults))
        }
    }
}