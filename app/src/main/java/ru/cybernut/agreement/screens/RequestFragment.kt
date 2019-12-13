package ru.cybernut.agreement.screens


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.R
import ru.cybernut.agreement.databinding.FragmentRequestBinding
import ru.cybernut.agreement.viewmodels.RequestViewModel
import ru.cybernut.agreement.viewmodels.RequestViewModelFactory

class RequestFragment : Fragment() {

    private val TAG = "RequestFragment"
    private val args: RequestFragmentArgs by navArgs()

    private lateinit var binding: FragmentRequestBinding
    private val viewModel: RequestViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, RequestViewModelFactory(activity.application, args.request))
            .get(RequestViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRequestBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.needShowToast.observe(this, Observer {
            if (it == true) {
                //Toast.makeText(activity, "Test", Toast.LENGTH_SHORT).show()
                findNavController().navigate(RequestFragmentDirections.actionRequestFragmentToRequestListFragment())
                viewModel.onToastShowDone()
            }
        })

        binding.declineButton.setOnClickListener { handleThisRequest(false) }

        return binding.root
    }

    private fun handleThisRequest(approve: Boolean) {

        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setMessage(getString(R.string.dialog_confirm_approve))
            .setCancelable(false)
            .setPositiveButton(getText(R.string.confirm), DialogInterface.OnClickListener {
                    dialog, id -> viewModel.handleRequest(approve)
                    Toast.makeText(activity,"Ok " + AgreementApp.loginCredential.userName, Toast.LENGTH_SHORT).show()
            })
            .setNegativeButton(getText(R.string.cancel), DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle(getText(R.string.app_name))
        alert.show()
    }
}

