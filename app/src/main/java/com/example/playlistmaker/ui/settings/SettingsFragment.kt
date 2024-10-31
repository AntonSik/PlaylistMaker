package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import com.example.playlistmaker.ui.root.BottomNavBarShower
import com.example.playlistmaker.utils.App
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? BottomNavBarShower)?.showNavbar()
        binding.themeSwitcher.isChecked = viewModel.getTheme()


        if ((requireActivity().applicationContext as App).darkTheme) {
            binding.themeSwitcher.setChecked(true)
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.changeTheme(checked)

        }

        binding.share.setOnClickListener {

            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.putExtra(Intent.EXTRA_TEXT, viewModel.shareApp())
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