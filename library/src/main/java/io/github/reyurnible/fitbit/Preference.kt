package io.github.reyurnible.fitbit

import android.content.Context
import android.content.SharedPreferences

interface Preference {
    val context: Context
    val preferenceName: String
    val sharedPreference: SharedPreferences
        get() = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

    // Writer
    fun write(writer: (SharedPreferences.Editor) -> Unit) {
        val editor = sharedPreference.edit()
        writer(editor)
        editor.apply()
    }

    operator fun contains(key: String): Boolean = sharedPreference.contains(key)

    // Reader
    fun readString(key: String, default: String): String = sharedPreference.getString(key, default)
    fun readStringOrNull(key: String): String? = sharedPreference.getString(key, "").takeIf { it.isNotEmpty() }
    fun readInt(key: String, default: Int): Int = sharedPreference.getInt(key, default)
    fun readIntOrNull(key: String): Int? = sharedPreference.getInt(key, -1).takeIf { it != -1 }
    fun readLong(key: String, default: Long): Long = sharedPreference.getLong(key, default)
    fun readLongOrNull(key: String): Long? = sharedPreference.getLong(key, -1L).takeIf { it != -1L }
    fun readFloat(key: String, default: Float): Float = sharedPreference.getFloat(key, default)
    fun readBoolean(key: String, default: Boolean): Boolean = sharedPreference.getBoolean(key, default)
    fun readStringSet(key: String, default: MutableSet<String>): MutableSet<String> = sharedPreference.getStringSet(key, default)
    fun readAll(): MutableMap<String, *> = sharedPreference.all

    fun clear() {
        sharedPreference.edit().clear().apply()
    }
}

