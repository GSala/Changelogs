package com.saladevs.changelogclone.ui.details

import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding.widget.checkedChanges
import com.saladevs.changelogclone.App
import com.saladevs.changelogclone.AppManager
import com.saladevs.changelogclone.R
import com.saladevs.changelogclone.model.PackageUpdate
import com.saladevs.changelogclone.utils.addTo
import com.saladevs.changelogclone.utils.setDisabled
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

class DetailsFragment() : Fragment(), DetailsMvpView {

    private lateinit var mPackageInfo: PackageInfo

    private lateinit var mPresenter: DetailsPresenter

    private lateinit var mEmptyStateView: View
    private lateinit var mIcon: ImageView
    private lateinit var mIgnoreToggle: CheckBox
    private lateinit var mRecyclerView: RecyclerView
    private var mAdapter: DetailsAdapter = DetailsAdapter()

    private val subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && arguments.containsKey(PARAM_PACKAGE)) {
            mPackageInfo = arguments.getParcelable<PackageInfo>(PARAM_PACKAGE)
            mPresenter = DetailsPresenter(mPackageInfo.packageName)
        } else {
            throw IllegalArgumentException("Must provide PackageInfo to DetailsFragment")
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        mEmptyStateView = view.findViewById(R.id.emptyStateView)
        mRecyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        mIcon = view.findViewById(R.id.icon) as ImageView
        val label = view.findViewById(R.id.textPrimary) as TextView
        val subtitle = view.findViewById(R.id.textSecondary) as TextView
        mIgnoreToggle = view.findViewById(R.id.ignoreToggle) as CheckBox

        // Setup UI
        mIcon.setImageDrawable(AppManager.getAppIcon(mPackageInfo))
        label.text = AppManager.getAppLabel(mPackageInfo)
        subtitle.text = mPackageInfo.packageName

        mIgnoreToggle.checkedChanges()
                .skip(1)
                .distinctUntilChanged()
                .doOnNext { mIcon.setDisabled(it) }
                .subscribe { mPresenter.onIgnoreToggled(it) }
                .addTo(subscriptions)

        val mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPresenter.attachView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.detachView()
        App.getRefWatcher().watch(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_open_app -> {
                startPackageActivity(mPackageInfo.packageName)
                return true
            }
            R.id.action_open_store -> {
                startPlayStoreActivity(mPackageInfo.packageName)
                return true
            }
            R.id.action_open_info -> {
                startPackageInfoActivity(mPackageInfo.packageName)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startPlayStoreActivity(packageName: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL + packageName)))
    }

    private fun startPackageActivity(packageName: String) {
        val i = context.packageManager.getLaunchIntentForPackage(packageName)
        if (i == null) {
            Snackbar.make(mRecyclerView, R.string.cant_open_app, Snackbar.LENGTH_SHORT).show()
        } else {
            i.addCategory(Intent.CATEGORY_LAUNCHER)
            startActivity(i)
        }
    }

    private fun startPackageInfoActivity(packageName: String) {
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.data = Uri.fromParts("package", packageName, null)
        startActivity(i)
    }

    override fun showEmptyState(b: Boolean) {
        mEmptyStateView.visibility = if (b) View.VISIBLE else View.GONE
    }

    override fun showUpdates(updates: List<PackageUpdate>) {
        mAdapter.setData(updates)
    }

    override fun showInstallationDate(installationTime: Long, isSystemApp: Boolean) {
        if (isSystemApp) {
            mAdapter.footer = "Installed as system app"
        } else {
            mAdapter.footer = "Installed ${DateUtils.formatDateTime(App.getContext(), installationTime, 0)}"
        }
    }

    override fun setPackageIgnored(ignored: Boolean) {
        Timber.d(" setPackageIgnored - $ignored")
        mIgnoreToggle.isChecked = ignored
    }

    companion object {

        private val PARAM_PACKAGE = "package_info"
        private val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id="

        fun newInstance(pi: PackageInfo): DetailsFragment {
            val fragment = DetailsFragment()
            val bundle = Bundle()
            bundle.putParcelable(PARAM_PACKAGE, pi)
            fragment.arguments = bundle
            return fragment
        }
    }

}
