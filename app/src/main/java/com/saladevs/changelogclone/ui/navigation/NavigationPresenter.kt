package com.saladevs.changelogclone.ui.navigation

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences.Preference
import com.f2prateek.rx.preferences.RxSharedPreferences
import com.saladevs.changelogclone.App
import com.saladevs.changelogclone.ui.BasePresenter
import com.saladevs.changelogclone.utils.getPlayStorePackages
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription

class NavigationPresenter : BasePresenter<NavigationMvpView>() {

    companion object {
        val PREF_DISABLED_PACKAGES = "disabled_packages"
    }

    val mPackageManager: PackageManager = App.getContext().packageManager
    val mDisabledPackages: Preference<Set<String>>
    val mSubscriptions = CompositeSubscription()

    init {
        val prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext())
        val rxPrefs = RxSharedPreferences.create(prefs)
        mDisabledPackages = rxPrefs.getStringSet(PREF_DISABLED_PACKAGES)
    }

    override fun attachView(mvpView: NavigationMvpView?) {
        super.attachView(mvpView)

        mSubscriptions.add(mDisabledPackages.asObservable()
                .map { set ->
                    mPackageManager.getPlayStorePackages()
                            .map {
                                NavigationAdapter.NavigationItem(
                                        it,
                                        mPackageManager.getApplicationLabel(it.applicationInfo),
                                        mPackageManager.getApplicationIcon(it.packageName),
                                        !set.contains(it.packageName))
                            }
                            .sortedBy { it.label.toString().toLowerCase() }
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ packages -> getMvpView()?.showNavigationItems(packages) }))

    }

    override fun detachView() {
        super.detachView()

        mSubscriptions.unsubscribe()
    }

    fun onItemClicked(packageInfo: PackageInfo) {
        mvpView?.startDetailsActivity(packageInfo)
    }

    fun onItemLongClicked(packageInfo: PackageInfo) {
        val set = mDisabledPackages.get() ?: emptySet()
        if (set.contains(packageInfo.packageName)) {
            mDisabledPackages.set(set.minus(packageInfo.packageName))
        } else {
            mDisabledPackages.set(set.plus(packageInfo.packageName))
        }
    }

}