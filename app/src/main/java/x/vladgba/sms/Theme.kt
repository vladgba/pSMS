package x.vladgba.sms

import android.content.Context
import android.content.res.Configuration

/**
 * Day/night choice. [SYSTEM] leaves the device's uiMode alone, so values-night resources follow
 * the system setting; [DAY] and [NIGHT] force the night bit on every activity's base context.
 * [AMOLED] is night plus the "round" screen bit, which only exists to select values-round-night
 * (pure black colors); phones are never round, so nothing else keys off it.
 */
object Theme {
    const val SYSTEM = 0
    const val DAY = 1
    const val NIGHT = 2
    const val AMOLED = 3
    val NAMES = intArrayOf(R.string.theme_system, R.string.theme_day, R.string.theme_night, R.string.theme_amoled)

    private fun prefs(c: Context) = c.getSharedPreferences("pico", Context.MODE_PRIVATE)
    fun mode(c: Context) = prefs(c).getInt("theme", SYSTEM).takeIf { it in NAMES.indices } ?: SYSTEM
    fun setMode(c: Context, m: Int) = prefs(c).edit().putInt("theme", m).apply()

    /** Context for Activity.attachBaseContext with the chosen night mode applied. */
    fun wrap(base: Context): Context {
        val mode = mode(base)
        val night = when (mode) {
            DAY -> Configuration.UI_MODE_NIGHT_NO
            NIGHT, AMOLED -> Configuration.UI_MODE_NIGHT_YES
            else -> return base
        }
        val cfg = Configuration(base.resources.configuration)
        val round = if (mode == AMOLED) Configuration.SCREENLAYOUT_ROUND_YES else Configuration.SCREENLAYOUT_ROUND_NO
        cfg.screenLayout = (cfg.screenLayout and Configuration.SCREENLAYOUT_ROUND_MASK.inv()) or round
        cfg.uiMode = (cfg.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or night
        return base.createConfigurationContext(cfg)
    }
}
