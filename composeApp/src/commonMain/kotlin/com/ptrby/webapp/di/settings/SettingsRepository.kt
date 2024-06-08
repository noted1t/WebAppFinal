package com.ptrby.webapp.di.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class SettingsRepository(private val settings: Settings) {
    fun addValue(key: String, value: String) {
        settings[key] = value
    }

    fun removeValue(key: String) {
        settings.remove(key)
    }

    fun getValue(key: String): String? {
        return settings[key]
    }

    fun contains(key: String) : Boolean {
        return settings.contains(key)
    }

    fun updateValue(key: String, value: String) {
        settings[key] = value
    }
}