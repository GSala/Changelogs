package com.saladevs.changelogclone.ui.search

import android.content.pm.PackageInfo
import com.saladevs.changelogclone.ui.MvpView

/**
 *
 * Changelogs
 * SearchMvpView
 *
 * Created on 03/07/2017
 * Copyright (c) 2017 SHAPE A/S. All rights reserved.
 *
 */
interface SearchMvpView : MvpView {

    fun showSearchResults(results: SearchResult)
}