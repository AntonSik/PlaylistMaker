package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var image : ImageView
    private lateinit var shareField: TextView
    private lateinit var supportField : TextView
    private lateinit var termsField: TextView
    private lateinit var themeSwitcher: SwitchMaterial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

         image = findViewById(R.id.arrow)
         shareField = findViewById(R.id.share)
         supportField = findViewById(R.id.support)
         termsField = findViewById(R.id.terms)
        themeSwitcher = findViewById(R.id.themeSwitcher)

        image.setOnClickListener {
            finish()
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
        termsField.setOnClickListener{
            val url = Uri.parse(getString(R.string.urlOfferta))
            val offerIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(offerIntent)
        }

        themeSwitcher.setOnCheckedChangeListener {switcher, checked ->

                val isChecked = themeSwitcher.isChecked

                (applicationContext as App).switchTheme(checked)
            }




    }
}