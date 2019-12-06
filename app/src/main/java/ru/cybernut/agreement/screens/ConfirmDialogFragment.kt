package ru.cybernut.agreement.screens

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.cybernut.agreement.R
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.databinding.FragmentConfirmDialogBinding

class ConfirmDialogFragment : AppCompatDialogFragment() {
    private val args: ConfirmDialogFragmentArgs by navArgs()
    private lateinit var binding: FragmentConfirmDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentConfirmDialogBinding.inflate(inflater, container, false)

        binding.confirmButton.setOnClickListener {

        }

        binding.cancelButton.setOnClickListener {

        }

        return binding.root
    }
}
