package com.saladevs.changelogclone.utils

import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
 *
 * Changelogs
 * SubscriptionExtensions
 *
 * Created on 03/07/2017
 * Copyright (c) 2017 SHAPE A/S. All rights reserved.
 *
 */

fun Subscription.addTo(subs: CompositeSubscription) {
    subs.add(this)
}