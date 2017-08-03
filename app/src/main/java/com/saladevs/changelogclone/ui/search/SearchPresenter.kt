package com.saladevs.changelogclone.ui.search

import com.saladevs.changelogclone.AppManager
import com.saladevs.changelogclone.ui.BasePresenter

class SearchPresenter : BasePresenter<SearchMvpView>() {

    fun onSearchQuery(query: CharSequence) {
        if (query.isBlank()) {
            mvpView?.showSearchResults(SearchResult())
        } else {
            val appResults = AppManager.getPlayStorePackages()
                    .filter { AppManager.getAppLabel(it).contains(query.trim(), true) }
                    .map { SearchResult.AppResult(it, AppManager.getAppLabel(it), AppManager.getAppIcon(it)) }
                    .sortedBy { it.label.toString().toLowerCase() }

            mvpView?.showSearchResults(SearchResult(appResults))
        }
    }
}