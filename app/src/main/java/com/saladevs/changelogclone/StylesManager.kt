package com.saladevs.changelogclone

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate
import com.f2prateek.rx.preferences.Preference
import com.f2prateek.rx.preferences.RxSharedPreferences

/**
 *
 * Changelogs
 * StylesManager
 *
 * Created on 05/07/2017
 * Copyright (c) 2017 SHAPE A/S. All rights reserved.
 *
 */
object StylesManager {

    private val PREF_GLOBAL_THEME = "pref_global_theme"

    private lateinit var globalThemePref: Preference<Int>

    var globalTheme: Int
        get() = globalThemePref.get() ?: AppCompatDelegate.MODE_NIGHT_AUTO
        set(value) = globalThemePref.set(value)

    fun init(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val rxPreferences = RxSharedPreferences.create(sharedPreferences)
        globalThemePref = rxPreferences.getInteger(PREF_GLOBAL_THEME, AppCompatDelegate.MODE_NIGHT_AUTO)
    }
}