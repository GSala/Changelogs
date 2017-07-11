package com.saladevs.changelogclone

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate
import com.f2prateek.rx.preferences.Preference
import com.f2prateek.rx.preferences.RxSharedPreferences
import com.saladevs.changelogclone.model.ActivityChangelogStyle
import rx.Observable

object StylesManager {

    private val PREF_GLOBAL_THEME = "pref_global_theme"
    private val PREF_ACTIVITY_CHANGELOG_STYLE = "pref_activity_changelog_style"

    // Global theme
    private lateinit var globalThemePref: Preference<Int>
    private val defaultGlobalTheme = AppCompatDelegate.MODE_NIGHT_AUTO
    var globalTheme: Int = defaultGlobalTheme
        get() = globalThemePref.get()!!
        set(value) {
            field = value
            globalThemePref.set(value)
        }

    // Activity changelog style
    private lateinit var activityChangelogStylePref: Preference<String>
    private val defaultActivityChangelogStyle = ActivityChangelogStyle.SHORT
    lateinit var activityChangelogStyleObs: Observable<ActivityChangelogStyle>
    var activityChangelogStyle: ActivityChangelogStyle = defaultActivityChangelogStyle
        get() = ActivityChangelogStyle.valueOf(activityChangelogStylePref.get()!!)
        set(value) {
            field = value
            activityChangelogStylePref.set(value.name)
        }

    fun init(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val rxPreferences = RxSharedPreferences.create(sharedPreferences)
        globalThemePref = rxPreferences.getInteger(PREF_GLOBAL_THEME, defaultGlobalTheme)
        activityChangelogStylePref = rxPreferences.getString(PREF_ACTIVITY_CHANGELOG_STYLE, defaultActivityChangelogStyle.name)
        activityChangelogStyleObs = activityChangelogStylePref.asObservable().map { ActivityChangelogStyle.valueOf(it) }
    }
}