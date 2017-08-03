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
import android.widget.ImageView
import android.widget.TextView
import com.saladevs.changelogclone.App
import com.saladevs.changelogclone.AppManager
import com.saladevs.changelogclone.R
import com.saladevs.changelogclone.model.PackageUpdate
import com.saladevs.changelogclone.utils.setDisabled

class DetailsFragment() : Fragment(), DetailsMvpView {

    private lateinit var mPackageInfo: PackageInfo

    private lateinit var presenter: DetailsPresenter

    private lateinit var icon: ImageView
    private lateinit var recyclerView: RecyclerView
    private var adapter: DetailsAdapter = DetailsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && arguments.containsKey(PARAM_PACKAGE)) {
            mPackageInfo = arguments.getParcelable<PackageInfo>(PARAM_PACKAGE)
            presenter = DetailsPresenter(mPackageInfo.packageName)
        } else {
            throw IllegalArgumentException("Must provide PackageInfo to DetailsFragment")
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)

        recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        icon = view.findViewById(R.id.icon) as ImageView
        val label = view.findViewById(R.id.textPrimary) as TextView
        val subtitle = view.findViewById(R.id.textSecondary) as TextView

        // Setup UI
        icon.setImageDrawable(AppManager.getAppIcon(mPackageInfo))
        label.text = AppManager.getAppLabel(mPackageInfo)
        subtitle.text = mPackageInfo.packageName

        val mLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
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
            R.id.action_toggle_ignore -> {
                presenter.onIgnoreToggled()
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
            Snackbar.make(recyclerView, R.string.cant_open_app, Snackbar.LENGTH_SHORT).show()
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

    override fun showUpdates(updates: List<PackageUpdate>) {
        adapter.setData(updates)
    }

    override fun showInstallationDate(installationTime: Long, isSystemApp: Boolean) {
        if (isSystemApp) {
            adapter.footer = "Installed as system app"
        } else {
            adapter.footer = "Installed ${DateUtils.formatDateTime(App.getContext(), installationTime, 0)}"
        }
    }

    override fun setPackageIgnored(ignored: Boolean) {
        icon.setDisabled(ignored)
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
