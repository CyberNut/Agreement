package ru.cybernut.agreement.screens


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.cybernut.agreement.R
import ru.cybernut.agreement.databinding.FragmentAboutBinding
import ru.cybernut.agreement.databinding.FragmentRequestBinding
import ru.cybernut.agreement.viewmodels.RequestViewModel
import ru.cybernut.agreement.viewmodels.RequestViewModelFactory

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

        return binding.root
    }
}

