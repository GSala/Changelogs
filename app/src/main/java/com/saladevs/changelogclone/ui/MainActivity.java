package com.saladevs.changelogclone.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.saladevs.changelogclone.BuildConfig;
import com.saladevs.changelogclone.R;
import com.saladevs.changelogclone.ui.details.DetailsActivity;
import com.saladevs.changelogclone.utils.PackageUtils;

import org.javatuples.Triplet;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Base_TranslucentSystemBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setUpToolbar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        setUpNavigationView(mNavigationView);

    }

    private void setUpToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpNavigationView(NavigationView navigationView) {
        navigationView.setItemIconTintList(null);
        Menu mNavigationMenu = navigationView.getMenu();
        PackageUtils.getPackageList()
                .map(pi -> new Triplet<>(pi, PackageUtils.getAppLabel(pi), PackageUtils.getAppIconDrawable(pi)))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pi -> {
                    if (mNavigationMenu != null) {
                        addNavigationItem(mNavigationMenu, pi.getValue0(), pi.getValue1(), pi.getValue2());
                    }
                });
    }

    private void addNavigationItem(Menu menu, PackageInfo pi, CharSequence label, Drawable icon) {
        menu.add(label)
                .setIcon(icon)
                .setOnMenuItemClickListener(menuItem -> {
                    DetailsActivity.startWith(MainActivity.this, pi);
                    return true;
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(mNavigationView);
                return true;
            case R.id.action_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "saladevs@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, String.format("[Changes v%s] Feedback", BuildConfig.VERSION_NAME));
                startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
