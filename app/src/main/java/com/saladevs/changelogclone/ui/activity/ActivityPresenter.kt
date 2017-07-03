package com.saladevs.changelogclone.ui.activity

import android.content.pm.PackageInfo
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences.Preference
import com.f2prateek.rx.preferences.RxSharedPreferences
import com.saladevs.changelogclone.App
import com.saladevs.changelogclone.AppManager
import com.saladevs.changelogclone.model.PackageUpdate
import com.saladevs.changelogclone.ui.BasePresenter
import io.realm.Realm
import io.realm.Sort
import rx.Observable
import rx.subscriptions.CompositeSubscription

internal class ActivityPresenter : BasePresenter<ActivityFragment>() {

    private val mRealm: Realm
    private val mSubscriptions = CompositeSubscription()
    private val mChangelogStylePref: Preference<Int>

    init {
        mRealm = Realm.getDefaultInstance()
        val prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext())
        val rxPrefs = RxSharedPreferences.create(prefs)
        mChangelogStylePref = rxPrefs.getInteger(PREF_CHANGELOG_STYLE)
    }

    override fun attachView(mvpView: ActivityFragment) {
        super.attachView(mvpView)

        // Subscribe to Style SharedPreferences changes
        mSubscriptions.add(mChangelogStylePref.asObservable()
                .subscribe { style -> getMvpView()?.changeChangelogStyle(style!!) })

        val realmObs = mRealm.where(PackageUpdate::class.java)
                .findAllSortedAsync("date", Sort.DESCENDING)
                .asObservable()

        val ignoredObs = AppManager.getIgnoredAppsObservable()

        mSubscriptions.add(
                Observable.combineLatest(realmObs, ignoredObs,
                        { results, ignored -> results.filter { !ignored.contains(it.packageName) } })
                        .subscribe { updates ->
                            if (updates.isNotEmpty()) {
                                getMvpView()?.showEmptyState(false)
                                getMvpView()?.showUpdates(updates)
                            } else {
                                getMvpView()?.showEmptyState(true)
                            }
                        })
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
        mRealm.close()
    }

    fun onItemClicked(packageInfo: PackageInfo) {
        mvpView?.startDetailsActivity(packageInfo)
    }

    fun onChangelogStyleSelected(style: Int) {
        mChangelogStylePref.set(style)
    }

    companion object {
        private val PREF_CHANGELOG_STYLE = "prefChangelogStyle"
    }
}
