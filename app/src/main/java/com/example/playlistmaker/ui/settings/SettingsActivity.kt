package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import com.example.playlistmaker.utils.App

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        binding.themeSwitcher.isChecked = viewModel.getTheme()

        binding.arrow.setOnClickListener {
            finish()
        }

        if ((applicationContext as App).darkTheme) {
            binding.themeSwitcher.setChecked(true)
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.changeTheme(checked)

        }

        binding.share.setOnClickListener {

            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.putExtra(Intent.EXTRA_TEXT,viewModel.shareApp())
            shareIntent.setType("*/*")
            startActivity(shareIntent)
        }
        binding.support.setOnClickListener {
            val supIntent = Intent(Intent.ACTION_SENDTO)
            supIntent.data = Uri.parse("mailto:")
            supIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(viewModel.writeToSupport().recipient))
            supIntent.putExtra(Intent.EXTRA_SUBJECT, viewModel.writeToSupport().subject)
            supIntent.putExtra(Intent.EXTRA_TEXT, viewModel.writeToSupport().text)
            startActivity(supIntent)
        }
        binding.terms.setOnClickListener {
            val url = Uri.parse(viewModel.openTerms())
            val offerIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(offerIntent)

        }

    }

}