package com.saladevs.changelogclone.ui.navigation

import android.content.pm.PackageInfo
import com.saladevs.changelogclone.AppManager
import com.saladevs.changelogclone.ui.BasePresenter
import com.saladevs.changelogclone.utils.addTo
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class NavigationPresenter : BasePresenter<NavigationMvpView>() {

    override fun attachView(mvpView: NavigationMvpView) {
        super.attachView(mvpView)

        AppManager.getIgnoredAppsObservable()
                .map { set ->
                    AppManager.getPlayStorePackages()
                            .map {
                                NavigationAdapter.NavigationItem(
                                        it,
                                        AppManager.getAppLabel(it),
                                        AppManager.getAppIcon(it),
                                        !set.contains(it.packageName))
                            }
                            .sortedBy { it.label.toString().toLowerCase() }
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ packages -> mvpView.showNavigationItems(packages) })
                .addTo(subscriptions)

    }

    fun onItemClicked(packageInfo: PackageInfo) {
        mvpView?.startDetailsActivity(packageInfo)
    }

    fun onItemLongClicked(packageInfo: PackageInfo) {
        AppManager.toggleAppIgnored(packageInfo.packageName)
    }

}