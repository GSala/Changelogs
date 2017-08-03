package com.saladevs.changelogclone.ui.activity

import android.content.pm.PackageInfo
import com.saladevs.changelogclone.AppManager
import com.saladevs.changelogclone.StylesManager
import com.saladevs.changelogclone.model.ActivityChangelogStyle
import com.saladevs.changelogclone.model.PackageUpdate
import com.saladevs.changelogclone.ui.BasePresenter
import com.saladevs.changelogclone.utils.addTo
import io.realm.Sort
import rx.Observable
import timber.log.Timber

internal class ActivityPresenter : BasePresenter<ActivityFragment>() {

    override fun attachView(mvpView: ActivityFragment) {
        super.attachView(mvpView)

        // Subscribe to Style SharedPreferences changes
        StylesManager.activityChangelogStyleObs
                .subscribe { style -> mvpView.changeChangelogStyle(style!!) }
                .addTo(subscriptions)

        val realmObs = realm.where(PackageUpdate::class.java)
                .findAllSortedAsync("date", Sort.DESCENDING)
                .asObservable()

        val ignoredObs = AppManager.getIgnoredAppsObservable()

        Observable.combineLatest(realmObs, ignoredObs,
                { results, ignored -> results.filter { !ignored.contains(it.packageName) } })
                .subscribe({ mvpView.showUpdates(it) }, { Timber.e(it) })
                .addTo(subscriptions)
    }

    fun onItemClicked(packageInfo: PackageInfo) {
        mvpView?.startDetailsActivity(packageInfo)
    }

    fun onChangelogStyleSelected(style: ActivityChangelogStyle): Boolean {
        StylesManager.activityChangelogStyle = style
        return true
    }
}
