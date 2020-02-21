package ru.cybernut.agreement.screens


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

