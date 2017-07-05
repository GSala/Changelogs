package com.saladevs.changelogclone.ui.details

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.saladevs.changelogclone.R

class DetailsActivity : AppCompatActivity() {

    private lateinit var mPackageInfo: PackageInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get packageInfo from Intent bundle
        mPackageInfo = intent.extras?.getParcelable(PARAM_PACKAGE)
                ?: throw IllegalStateException("Use DetailsActivity static method to construct an intent")

        // Put fragment with the same bundle that the Activity received
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = DetailsFragment.newInstance(mPackageInfo)
        transaction.replace(R.id.container, fragment).commit()


    }

    companion object {

        private val PARAM_PACKAGE = "package_info"

        fun startWith(context: Context, packageInfo: PackageInfo) {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(PARAM_PACKAGE, packageInfo)
            context.startActivity(intent)
        }
    }
}
