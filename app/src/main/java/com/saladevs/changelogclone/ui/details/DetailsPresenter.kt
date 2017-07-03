package com.saladevs.changelogclone.ui.details

import android.preference.PreferenceManager
import com.f2prateek.rx.preferences.Preference
import com.f2prateek.rx.preferences.RxSharedPreferences
import com.saladevs.changelogclone.App
import com.saladevs.changelogclone.model.PackageUpdate
import com.saladevs.changelogclone.ui.BasePresenter
import com.saladevs.changelogclone.ui.navigation.NavigationPresenter
import com.saladevs.changelogclone.utils.addTo
import io.realm.Realm
import io.realm.Sort
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

internal class DetailsPresenter(private val mPackageName: String) : BasePresenter<DetailsMvpView>() {

    private val mRealm = Realm.getDefaultInstance()
    val mDisabledPackages: Preference<Set<String>>
    val mSubscriptions = CompositeSubscription()

    init {
        val prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext())
        val rxPrefs = RxSharedPreferences.create(prefs)
        mDisabledPackages = rxPrefs.getStringSet(NavigationPresenter.PREF_DISABLED_PACKAGES)
    }

    override fun attachView(mvpView: DetailsMvpView) {
        super.attachView(mvpView)

        mDisabledPackages.asObservable()
                .map { it.contains(mPackageName) }
                .distinctUntilChanged()
                .subscribe { getMvpView()?.setPackageIgnored(it) }
                .addTo(mSubscriptions)

        mRealm.where(PackageUpdate::class.java)
                .equalTo("packageName", mPackageName)
                .findAllSorted("date", Sort.DESCENDING)
                .asObservable()
                .subscribe { updates ->
                    if (updates.size > 0) {
                        getMvpView()?.showEmptyState(false)
                        getMvpView()?.showUpdates(updates)
                    } else {
                        getMvpView()?.showEmptyState(true)
                    }
                }
                .addTo(mSubscriptions)
    }

    override fun detachView() {
        super.detachView()

        mSubscriptions.unsubscribe()
        mRealm.close()
    }

    fun onIgnoreToggled(checked: Boolean) {
        Timber.d(" ignoreToggled - $checked ")
        val set = mDisabledPackages.get() ?: emptySet()
        if (checked) {
            mDisabledPackages.set(set.plus(mPackageName))
        } else {
            mDisabledPackages.set(set.minus(mPackageName))
        }
    }

}
