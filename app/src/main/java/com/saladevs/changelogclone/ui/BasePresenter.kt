package com.saladevs.changelogclone.ui

import io.realm.Realm
import rx.subscriptions.CompositeSubscription

abstract class BasePresenter<T : MvpView> {

    protected val realm = Realm.getDefaultInstance()
    protected val subscriptions = CompositeSubscription()

    protected var mvpView: T? = null
        private set

    open fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    open fun detachView() {
        subscriptions.clear()
        realm.close()
        mvpView = null
    }

    private val isViewAttached: Boolean
        get() = mvpView != null

    protected fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    private class MvpViewNotAttachedException : RuntimeException("Please call Presenter.attachView(MvpView) before" + " requesting data to the Presenter")
}
