package cl.ryodantech.amigodocente.utils

import android.content.Context

object ThemeManager {
    private const val PREFS_NAME = "amigo_docente_prefs"
    private const val KEY_DARK_MODE = "dark_mode"

    fun isDarkMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Por defecto, sigue la configuración del sistema
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(context: Context, isDark: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_MODE, isDark).apply()
    }
}