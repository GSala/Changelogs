package com.saladevs.changelogclone.ui.navigation

import android.content.pm.PackageInfo
import com.saladevs.changelogclone.AppManager
import com.saladevs.changelogclone.ui.BasePresenter
import com.saladevs.changelogclone.utils.addTo
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class NavigationPresenter : BasePresenter<NavigationMvpView>() {

    val mSubscriptions = CompositeSubscription()

    override fun attachView(mvpView: NavigationMvpView?) {
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
                .subscribe({ packages -> getMvpView()?.showNavigationItems(packages) })
                .addTo(mSubscriptions)

    }

    override fun detachView() {
        super.detachView()

        mSubscriptions.unsubscribe()
    }

    fun onItemClicked(packageInfo: PackageInfo) {
        mvpView?.startDetailsActivity(packageInfo)
    }

    fun onItemLongClicked(packageInfo: PackageInfo) {
        AppManager.toggleAppIgnored(packageInfo.packageName)
    }

}