package com.saladevs.changelogclone.ui.search

import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import com.saladevs.changelogclone.model.PackageUpdate

data class SearchResult(
        val appResults: List<AppResult> = emptyList(),
        val changelogResults: List<ChangelogResult> = emptyList()) {

    data class AppResult(val tag: PackageInfo, val label: CharSequence, val icon: Drawable)

    data class ChangelogResult(val tag: PackageUpdate, val label: CharSequence, val icon: Drawable)
}