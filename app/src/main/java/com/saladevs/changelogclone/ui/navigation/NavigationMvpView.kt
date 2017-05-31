package com.saladevs.changelogclone.ui.navigation

import android.content.pm.PackageInfo
import com.saladevs.changelogclone.ui.MvpView

interface NavigationMvpView : MvpView {

    fun showNavigationItems(items: List<NavigationAdapter.NavigationItem>)

    fun startDetailsActivity(packageInfo: PackageInfo)

}