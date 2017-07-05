package com.saladevs.changelogclone.ui.search

import com.saladevs.changelogclone.ui.MvpView

interface SearchMvpView : MvpView {

    fun showSearchResults(results: SearchResult)
}