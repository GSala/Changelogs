package com.saladevs.changelogclone.utils

import rx.Subscription
import rx.subscriptions.CompositeSubscription

fun Subscription.addTo(subs: CompositeSubscription) {
    subs.add(this)
}