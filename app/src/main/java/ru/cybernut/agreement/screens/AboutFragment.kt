package ru.cybernut.agreement.screens


import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.BuildConfig
import ru.cybernut.agreement.R
import ru.cybernut.agreement.databinding.FragmentAboutBinding


class AboutFragment : Fragment() {

    private val TAG = "AboutFragment"
    //private val args: AboutFragmentArgs by navArgs()

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAboutBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val versionName: String = BuildConfig.VERSION_NAME
        val versionCode: Int = BuildConfig.VERSION_CODE

        binding.aboutTextView.text = resources.getString(R.string.version) + "\n" + versionName + " (" + versionCode + ")"

        return binding.root
    }
}

