package com.saladevs.changelogclone.ui.details

import com.saladevs.changelogclone.model.PackageUpdate
import com.saladevs.changelogclone.ui.MvpView

interface DetailsMvpView : MvpView {

    fun showUpdates(updates: List<PackageUpdate>)

    fun showInstallationDate(installationTime: Long, isSystemApp: Boolean)

    fun setPackageIgnored(ignored: Boolean)

}
