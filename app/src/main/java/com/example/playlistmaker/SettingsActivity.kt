package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.HistoryPrefs.Companion.SHARED_PREFERENCES_HISTORY
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    companion object {
        private const val KEY_THEME_SWITCHER = "key for switcher"

    }

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var image: ImageView
    private lateinit var shareField: TextView
    private lateinit var supportField: TextView
    private lateinit var termsField: TextView
    private lateinit var themeSwitcher: SwitchMaterial
    private var darkTheme = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPrefs = getSharedPreferences(SHARED_PREFERENCES_HISTORY, MODE_PRIVATE)
        image = findViewById(R.id.arrow)
        shareField = findViewById(R.id.share)
        supportField = findViewById(R.id.support)
        termsField = findViewById(R.id.terms)
        themeSwitcher = findViewById(R.id.themeSwitcher)

        image.setOnClickListener {
            finish()
        }

        themeSwitcher.isChecked = sharedPrefs.getBoolean(
            KEY_THEME_SWITCHER,
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        )
        switchTheme(themeSwitcher.isChecked)
        themeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
            sharedPrefs.edit().putBoolean(KEY_THEME_SWITCHER, isChecked).apply()
            switchTheme(isChecked)

        }
        shareField.setOnClickListener {

            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareMessage))
            shareIntent.setType("*/*")
            startActivity(shareIntent)
        }
        supportField.setOnClickListener {
            val supIntent = Intent(Intent.ACTION_SENDTO)
            supIntent.data = Uri.parse("mailto:")
            supIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.myEmail)))
            supIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.themeofMessage))
            supIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message))
            startActivity(supIntent)
        }
        termsField.setOnClickListener {
            val url = Uri.parse(getString(R.string.urlOfferta))
            val offerIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(offerIntent)
        }

    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}