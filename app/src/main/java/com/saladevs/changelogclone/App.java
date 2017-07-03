package com.saladevs.changelogclone;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import jonathanfinerty.once.Once;
import timber.log.Timber;


public class App extends android.app.Application {

    private static App instance;
    private static RefWatcher refWatcher;

    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);
    }

    public App() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    public static RefWatcher getRefWatcher() {
        return refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        refWatcher = LeakCanary.install(this);

        Fabric.with(this, new Crashlytics());

        Timber.plant(new Timber.DebugTree());

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);

        Once.initialise(this);

        AppManager.INSTANCE.init(this);
    }
}