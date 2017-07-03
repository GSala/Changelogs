package com.saladevs.changelogclone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class PackageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val packageName = intent.data.encodedSchemeSpecificPart

        if (intent.action == Intent.ACTION_PACKAGE_ADDED) {
            // Package installed

        } else if (intent.action == Intent.ACTION_PACKAGE_REPLACED) {
            // Package updated
            Timber.d("Package updated - %s", packageName)
            if (AppManager.isAppFromGooglePlay(packageName)) {
                PackageService.startActionFetchUpdate(context, packageName)
            }

        } else if (intent.action == Intent.ACTION_PACKAGE_FULLY_REMOVED) {
            // Package uninstalled
            Timber.d("Package uninstalled - %s", packageName)
            PackageService.startActionRemovePackage(context, packageName)
        }
    }
}
