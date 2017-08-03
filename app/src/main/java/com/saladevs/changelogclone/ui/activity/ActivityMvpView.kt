package com.saladevs.changelogclone.ui.activity

import android.content.pm.PackageInfo

import com.saladevs.changelogclone.model.ActivityChangelogStyle
import com.saladevs.changelogclone.model.PackageUpdate
import com.saladevs.changelogclone.ui.MvpView

interface ActivityMvpView : MvpView {

    fun showUpdates(updates: List<PackageUpdate>)

    fun changeChangelogStyle(style: ActivityChangelogStyle)

    fun startDetailsActivity(packageInfo: PackageInfo)
}
