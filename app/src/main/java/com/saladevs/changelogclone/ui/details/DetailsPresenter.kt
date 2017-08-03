package com.saladevs.changelogclone.ui.details

import android.content.pm.ApplicationInfo
import com.saladevs.changelogclone.AppManager
import com.saladevs.changelogclone.model.PackageUpdate
import com.saladevs.changelogclone.ui.BasePresenter
import com.saladevs.changelogclone.utils.addTo
import io.realm.Realm
import io.realm.Sort
import rx.subscriptions.CompositeSubscription


internal class DetailsPresenter(private val mPackageName: String) : BasePresenter<DetailsMvpView>() {

    private val mRealm = Realm.getDefaultInstance()
    val mSubscriptions = CompositeSubscription()

    override fun attachView(mvpView: DetailsMvpView) {
        super.attachView(mvpView)

        AppManager.getIgnoredAppsObservable()
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

        AppManager.getPackageInfo(mPackageName)?.let {
            val isSystemApp = it.applicationInfo.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) > 0
            mvpView.showInstallationDate(it.firstInstallTime, isSystemApp)
        }

    }

    override fun detachView() {
        super.detachView()

        mSubscriptions.unsubscribe()
        mRealm.close()
    }

    fun onIgnoreToggled(checked: Boolean) {
        AppManager.setAppIgnored(mPackageName, checked)
    }

}
