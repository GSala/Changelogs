package com.saladevs.changelogclone.ui.details;

import com.saladevs.changelogclone.model.PackageUpdate;
import com.saladevs.changelogclone.ui.BasePresenter;

import io.realm.Realm;
import io.realm.Sort;
import rx.Subscription;

class DetailsPresenter extends BasePresenter<DetailsMvpView> {

    private Realm mRealm;
    private Subscription mSubscription;
    private String mPackageName;

    DetailsPresenter(String packageName) {
        mRealm = Realm.getDefaultInstance();
        mPackageName = packageName;
    }

    @Override
    protected void attachView(DetailsMvpView mvpView) {
        super.attachView(mvpView);

        mSubscription = mRealm.where(PackageUpdate.class)
                .equalTo("packageName", mPackageName)
                .findAllSorted("date", Sort.DESCENDING)
                .asObservable()
                .subscribe(updates -> {
                    if (updates.size() > 0) {
                        getMvpView().showEmptyState(false);
                        getMvpView().showUpdates(updates);
                    } else {
                        getMvpView().showEmptyState(true);
                    }
                });
    }

    @Override
    protected void detachView() {
        super.detachView();

        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        mRealm.close();
    }
}
