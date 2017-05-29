package com.saladevs.changelogclone.utils

import android.content.pm.PackageInfo
import android.content.pm.PackageManager

private const val PACKAGE_ANDROID_VENDING = "com.android.vending"

fun PackageInfo.isInstalledFromGooglePlay(pm: PackageManager): Boolean {
    try {
        val installerPackage = pm.getInstallerPackageName(packageName)
        return installerPackage != null && installerPackage == PACKAGE_ANDROID_VENDING
    } catch (e: IllegalArgumentException) {
        return false
    }
}

fun PackageManager.getPlayStorePackages(): List<PackageInfo> {
    return this.getInstalledPackages(0).filter { it -> it.isInstalledFromGooglePlay(this) }
}