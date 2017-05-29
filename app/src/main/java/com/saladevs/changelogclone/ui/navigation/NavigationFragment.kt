package com.saladevs.changelogclone.ui.navigation


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.saladevs.changelogclone.R
import com.saladevs.changelogclone.utils.getPlayStorePackages
import timber.log.Timber

class NavigationFragment() : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: NavigationAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_navigation, container, false)

        mRecyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        mAdapter = NavigationAdapter()

        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerView.adapter = mAdapter

        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val pm = context.packageManager

        val disabled = listOf("com.Slack")
        val navigationItems = pm.getPlayStorePackages()
                .map { it -> NavigationAdapter.NavigationItem(it, pm.getApplicationLabel(it.applicationInfo), pm.getApplicationIcon(it.packageName), !disabled.contains(it.packageName)) }
                .sortedBy { (_, label, _, _) -> label.toString().toLowerCase() }

        mAdapter.setData(navigationItems)

        mAdapter.setOnItemClickListener { view, pi ->
            Timber.d("Click on : ${pi.packageName}")
        }

        mAdapter.setOnItemLongClickListener { view, pi ->
            Timber.d("Long click on : ${pi.packageName}")
        }

    }
}
