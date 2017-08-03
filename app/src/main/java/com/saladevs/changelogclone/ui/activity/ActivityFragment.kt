package com.saladevs.changelogclone.ui.activity


import android.content.pm.PackageInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.saladevs.changelogclone.App
import com.saladevs.changelogclone.R
import com.saladevs.changelogclone.R.id.recyclerView
import com.saladevs.changelogclone.model.ActivityChangelogStyle
import com.saladevs.changelogclone.model.ActivityChangelogStyle.*
import com.saladevs.changelogclone.model.PackageUpdate
import com.saladevs.changelogclone.ui.details.DetailsActivity
import timber.log.Timber

class ActivityFragment : Fragment(), ActivityMvpView, ActivityAdapter.OnItemClickListener {

    private val mPresenter = ActivityPresenter()

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Timber.d("-- onCraeteView --")
        val view = inflater.inflate(R.layout.fragment_activity, container, false)
        mRecyclerView = view.findViewById(recyclerView) as RecyclerView

        val mLayoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.setHasFixedSize(true)

        val itemAnimator = ActivityItemAnimator()
        mRecyclerView.itemAnimator = itemAnimator

        mAdapter = ActivityAdapter()
        mAdapter.setOnFeedItemClickListener(this)
        mRecyclerView.adapter = mAdapter

        val decoration = DividerItemDecoration(mRecyclerView.context,
                DividerItemDecoration.VERTICAL)
        decoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider))
        mRecyclerView.addItemDecoration(decoration)


        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("-- onViewCreated --")
        mPresenter.attachView(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("-- onDestroyView --")
        mPresenter.detachView()
        App.getRefWatcher().watch(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_activity, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_changelog_style_basic -> mPresenter.onChangelogStyleSelected(BASIC)
            R.id.action_changelog_style_short -> mPresenter.onChangelogStyleSelected(SHORT)
            R.id.action_changelog_style_full -> mPresenter.onChangelogStyleSelected(FULL)
            else -> false
        }
    }

    override fun onItemClick(v: View, packageInfo: PackageInfo) {
        mPresenter.onItemClicked(packageInfo)
    }

    override fun showUpdates(updates: List<PackageUpdate>) {
        mAdapter.setData(updates)
    }

    override fun changeChangelogStyle(style: ActivityChangelogStyle) {
        mAdapter.changelogStyle = style
    }

    override fun startDetailsActivity(packageInfo: PackageInfo) {
        DetailsActivity.startWith(activity, packageInfo)
    }

}
