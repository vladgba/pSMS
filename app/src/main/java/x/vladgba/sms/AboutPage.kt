package x.vladgba.sms

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import x.vladgba.sms.pui.PAboutInfo
import x.vladgba.sms.pui.showPAbout

/** About tab: version, day/night theme, default-app status, source code and privacy policy. */
class AboutPage(a: SettingsActivity) : Page(a, R.layout.page_about) {
    private val rowDefault = findViewById<TextView>(R.id.rowDefault)
    private val rowTheme = findViewById<TextView>(R.id.rowTheme)

    init {
        val ver = try {
            packageManager.getPackageInfo(packageName, 0).versionName ?: ""
        } catch (_: Exception) {
            ""
        }
        findViewById<TextView>(R.id.version).text = getString(R.string.version_n, ver)
        rowTheme.text = SpannableStringBuilder(getString(R.string.theme)).append('\n').also {
            val start = it.length
            it.append(getString(Theme.NAMES[Theme.mode(this)]))
            it.setSpan(RelativeSizeSpan(0.8f), start, it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        rowTheme.setOnClickListener { pickTheme() }
        rowDefault.setOnClickListener { Ui.requestDefault(a) }
        findViewById<View>(R.id.rowSource).setOnClickListener { a.showPAbout(PAboutInfo(repo = "pSMS", privacy = true)) }
        findViewById<View>(R.id.rowPrivacy).setOnClickListener { open(PRIVACY) }
    }

    override fun onShow() {
        rowDefault.visibility = if (Sms.isDefault(this)) View.GONE else View.VISIBLE
    }

    /** Day / Night / AMOLED / Follow system; the screen is recreated to apply the choice. */
    private fun pickTheme() {
        val names = Theme.NAMES.map { getString(it) }.toTypedArray()
        AlertDialog.Builder(this).setTitle(R.string.theme)
            .setSingleChoiceItems(names, Theme.mode(this)) { d, which ->
                d.dismiss()
                if (which != Theme.mode(this)) {
                    Theme.setMode(this, which)
                    a.recreate()
                }
            }
            .show()
    }

    /** Opens a link in the user's browser (picoSMS itself has no internet access). */
    private fun open(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {
            Toast.makeText(this, url, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val PRIVACY = "https://github.com/vladgba/pSMS/blob/main/PRIVACY.md"
    }
}
