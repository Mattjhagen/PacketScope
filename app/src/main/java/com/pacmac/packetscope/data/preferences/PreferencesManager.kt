package com.pacmac.packetscope.data.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "packetscope_prefs",
        Context.MODE_PRIVATE
    )

    var hasSeenOnboarding: Boolean
        get() = prefs.getBoolean(KEY_HAS_SEEN_ONBOARDING, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_SEEN_ONBOARDING, value).apply()

    var hasSeenRatingSheet: Boolean
        get() = prefs.getBoolean(KEY_HAS_SEEN_RATING, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_SEEN_RATING, value).apply()

    var hasSeenNotificationSheet: Boolean
        get() = prefs.getBoolean(KEY_HAS_SEEN_NOTIFICATION, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_SEEN_NOTIFICATION, value).apply()

    var checkFrequency: String?
        get() = prefs.getString(KEY_CHECK_FREQUENCY, null)
        set(value) = prefs.edit().putString(KEY_CHECK_FREQUENCY, value).apply()

    var experienceLevel: String?
        get() = prefs.getString(KEY_EXPERIENCE_LEVEL, null)
        set(value) = prefs.edit().putString(KEY_EXPERIENCE_LEVEL, value).apply()

    var useCase: String?
        get() = prefs.getString(KEY_USE_CASE, null)
        set(value) = prefs.edit().putString(KEY_USE_CASE, value).apply()

    var referralSources: Set<String>
        get() = prefs.getStringSet(KEY_REFERRAL_SOURCES, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_REFERRAL_SOURCES, value).apply()

    companion object {
        private const val KEY_HAS_SEEN_ONBOARDING = "has_seen_onboarding"
        private const val KEY_HAS_SEEN_RATING = "has_seen_rating"
        private const val KEY_HAS_SEEN_NOTIFICATION = "has_seen_notification"
        private const val KEY_CHECK_FREQUENCY = "check_frequency"
        private const val KEY_EXPERIENCE_LEVEL = "experience_level"
        private const val KEY_USE_CASE = "use_case"
        private const val KEY_REFERRAL_SOURCES = "referral_sources"
    }
}
