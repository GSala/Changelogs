package com.saladevs.changelogclone.utils

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.saladevs.changelogclone.App

private const val PACKAGE_ANDROID_VENDING = "com.android.vending"

private val packageManager: PackageManager by lazy { App.getContext().packageManager }

fun String.isInstalledFromGooglePlay(): Boolean {
    return try {
        packageManager.getInstallerPackageName(this) == PACKAGE_ANDROID_VENDING
    } catch (e: IllegalArgumentException) {
        false
    }
}

fun PackageManager.getPlayStorePackages(): List<PackageInfo> {
    return this.getInstalledPackages(0)
            .filter { it.packageName.isInstalledFromGooglePlay() }
}

fun String.getPackageInfo(): PackageInfo? {
    return try {
        packageManager.getPackageInfo(this, 0)
    } catch (nnfe: PackageManager.NameNotFoundException) {
        null
    }
}

fun PackageInfo.getLabel(): CharSequence {
    return packageManager.getApplicationLabel(this.applicationInfo)
}

fun PackageInfo.getIcon(): Drawable {
    return packageManager.getApplicationIcon(this.applicationInfo)
}