package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val image = findViewById<ImageView>(R.id.arrow)
        val shareField = findViewById<TextView>(R.id.share)
        val supportField = findViewById<TextView>(R.id.support)
        val termsField = findViewById<TextView>(R.id.terms)

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
    }
}