package com.saladevs.changelogclone.utils

import com.saladevs.changelogclone.R

fun getDetailsInstallLinkDrawable(top: Boolean): Int {
    return when {
        top -> R.drawable.ic_details_link_single_install_alt
        else -> R.drawable.ic_details_link_bot_install_alt
    }
}


fun getDetailsSimpleLinkDrawable(top: Boolean, bottom: Boolean, error: Boolean): Int {
    if (error) {
        return when {
            top && bottom -> R.drawable.ic_details_link_single_error
            top -> R.drawable.ic_details_link_top_error
            bottom -> R.drawable.ic_details_link_bot_error
            else -> R.drawable.ic_details_link_mid_error
        }
    } else {
        return when {
            top && bottom -> R.drawable.ic_details_link_single_small
            top -> R.drawable.ic_details_link_top_small
            bottom -> R.drawable.ic_details_link_bot_small
            else -> R.drawable.ic_details_link_mid_small
        }
    }
}

fun getDetailsDetailedLinkDrawable(top: Boolean, bottom: Boolean): Int {
    return when {
        top && bottom -> R.drawable.ic_details_link_single_big
        top -> R.drawable.ic_details_link_top_big
        bottom -> R.drawable.ic_details_link_bot_big
        else -> R.drawable.ic_details_link_mid_big
    }
}
