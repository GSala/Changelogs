package com.saladevs.changelogclone

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.saladevs.changelogclone.model.PackageUpdate
import com.saladevs.changelogclone.network.ApiManager
import com.saladevs.changelogclone.utils.getPackageInfo
import io.realm.Realm
import rx.Observable
import timber.log.Timber
import java.util.*

class PackageService : IntentService("PackageService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)

            when (action){
                ACTION_FETCH_UPDATE -> handleActionFetchUpdate(packageName)
                ACTION_REMOVE_PACKAGE -> handleActionRemovePackage(packageName)
            }
        }
    }

    private fun handleActionRemovePackage(packageName: String) {
        val mRealm = Realm.getDefaultInstance()
        mRealm.executeTransaction { realm ->
            realm.where(PackageUpdate::class.java)
                    .equalTo("packageName", packageName)
                    .findAll()
                    .deleteAllFromRealm()
        }
        mRealm.close()
    }

    private fun handleActionFetchUpdate(packageName: String) {
        val packageInfo = packageName.getPackageInfo() ?: return

        val changelogObservable = ApiManager.getPlayStoreService().getChangelog(packageInfo.packageName)
                .map { it.changes.joinToString("\n") }
                .onErrorReturn { throwable ->
                    Timber.e(throwable)
                    getString(R.string.error_fetching_description)
                }

        Observable.zip(Observable.just(packageInfo), changelogObservable,
                { pi, c -> PackageUpdate(pi.packageName, pi.versionName, Date(pi.lastUpdateTime), c) })
                .subscribe({ this.saveUpdate(it) })
    }

    private fun saveUpdate(update: PackageUpdate) {
        val mRealm = Realm.getDefaultInstance()
        mRealm.executeTransaction { realm -> realm.copyToRealmOrUpdate(update) }
        mRealm.close()
    }

    companion object {

        private val ACTION_REMOVE_PACKAGE = "com.saladevs.changelogclone.action.REMOVE_PACKAGE"
        private val ACTION_FETCH_UPDATE = "com.saladevs.changelogclone.action.FETCH_UPDATE"

        private val EXTRA_PACKAGE_NAME = "com.saladevs.changelogclone.extra.PACKAGE_NAME"

        fun startActionRemovePackage(context: Context, packageName: String) {
            val intent = Intent(context, PackageService::class.java)
            intent.action = ACTION_REMOVE_PACKAGE
            intent.putExtra(EXTRA_PACKAGE_NAME, packageName)
            context.startService(intent)
        }

        fun startActionFetchUpdate(context: Context, packageName: String) {
            val intent = Intent(context, PackageService::class.java)
            intent.action = ACTION_FETCH_UPDATE
            intent.putExtra(EXTRA_PACKAGE_NAME, packageName)
            context.startService(intent)
        }
    }

}
