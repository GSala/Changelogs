package com.saladevs.changelogclone.ui.details

import android.content.pm.ApplicationInfo
import com.saladevs.changelogclone.AppManager
import com.saladevs.changelogclone.model.PackageUpdate
import com.saladevs.changelogclone.ui.BasePresenter
import com.saladevs.changelogclone.utils.addTo
import io.realm.Sort
import timber.log.Timber


internal class DetailsPresenter(private val mPackageName: String) : BasePresenter<DetailsMvpView>() {

    override fun attachView(mvpView: DetailsMvpView) {
        super.attachView(mvpView)

        AppManager.getIgnoredAppsObservable()
                .map { it.contains(mPackageName) }
                .distinctUntilChanged()
                .subscribe({ mvpView.setPackageIgnored(it) }, { Timber.e(it) })
                .addTo(subscriptions)

        realm.where(PackageUpdate::class.java)
                .equalTo("packageName", mPackageName)
                .findAllSorted("date", Sort.DESCENDING)
                .asObservable()
                .subscribe({ mvpView.showUpdates(it) }, { Timber.e(it) })
                .addTo(subscriptions)

        AppManager.getPackageInfo(mPackageName)?.let {
            val isSystemApp = it.applicationInfo.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) > 0
            mvpView.showInstallationDate(it.firstInstallTime, isSystemApp)
        }

    }

    fun onIgnoreToggled() {
        AppManager.toggleAppIgnored(mPackageName)
    }
}
