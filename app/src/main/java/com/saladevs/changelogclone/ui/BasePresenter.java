package com.saladevs.changelogclone.ui;

import android.support.annotation.Nullable;

public abstract class BasePresenter<T extends MvpView> {
    private T mMvpView;

    public void attachView(T mvpView) {
        mMvpView = mvpView;
    }

    public void detachView() {
        mMvpView = null;
    }

    private boolean isViewAttached() {
        return mMvpView != null;
    }

    @Nullable
    protected T getMvpView() {
        return mMvpView;
    }

    protected void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    private static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }
}
