package com.saladevs.changelogclone.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.saladevs.changelogclone.AppManager
import com.saladevs.changelogclone.BuildConfig
import com.saladevs.changelogclone.PackageService
import com.saladevs.changelogclone.R
import jonathanfinerty.once.Once


class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_TranslucentSystemBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mToolbar = findViewById(R.id.toolbar) as Toolbar
        setUpToolbar(mToolbar)

        mDrawerLayout = findViewById(R.id.drawerLayout) as DrawerLayout
        mNavigationView = findViewById(R.id.navFragment)

        if (!Once.beenDone(Once.THIS_APP_INSTALL, FIRST_TIME_FETCHING)) {
            AppManager.getPlayStorePackages()
                    .sortedBy { it.lastUpdateTime }
                    .forEach { PackageService.startActionFetchUpdate(this, it.packageName) }
            Snackbar.make(mToolbar, "Loading latest changes", Snackbar.LENGTH_LONG).show()
            Once.markDone(FIRST_TIME_FETCHING)
        }

        if (!Once.beenDone(Once.THIS_APP_VERSION, SHOW_CHANGELOG)) {
            Once.markDone(SHOW_CHANGELOG)
            AlertDialog.Builder(this)
                    .setView(R.layout.dialog_changelog)
                    .create().show()
        }
    }

    private fun setUpToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(mNavigationView)
                return true
            }
            R.id.action_feedback -> {
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "saladevs@gmail.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Changes v${BuildConfig.VERSION_NAME}] Feedback")
                startActivity(Intent.createChooser(emailIntent, "Send Feedback"))
                return true
            }
            R.id.action_about -> {
                showAboutDialog()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAboutDialog() {
        val aboutView = layoutInflater.inflate(R.layout.dialog_about, null);
        aboutView.findViewById(R.id.privacy).setOnClickListener { showPrivacyPolicy() }
        AlertDialog.Builder(this)
                .setView(aboutView)
                .create().show()
    }

    private fun showPrivacyPolicy() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.BASE_URL + PATH_PRIVACY))
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private val FIRST_TIME_FETCHING = "firstTimeFetching"
        private val SHOW_CHANGELOG = "showChangelog"
        private val PATH_PRIVACY = "privacy.html"
    }
}

