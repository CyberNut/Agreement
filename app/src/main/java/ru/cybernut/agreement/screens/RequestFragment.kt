package ru.cybernut.agreement.screens


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import org.koin.core.parameter.parametersOf
import ru.cybernut.agreement.R
import ru.cybernut.agreement.databinding.FragmentRequestBinding
import ru.cybernut.agreement.utils.hideKeyboard
import ru.cybernut.agreement.viewmodels.PaymentRequestViewModel

class RequestFragment : Fragment(), KoinComponent {

    private val TAG = "RequestFragment"
    private val args: RequestFragmentArgs by navArgs()

    private lateinit var binding: FragmentRequestBinding
    private val viewModel: PaymentRequestViewModel by inject { parametersOf(args.request)}

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

        binding.approvalButton.setOnClickListener { handleThisRequest(true, binding.approvalCommentary.text.toString()) }
        binding.declineButton.setOnClickListener { handleThisRequest(false, binding.approvalCommentary.text.toString()) }

        this.hideKeyboard()
        return binding.root
    }

    private fun handleThisRequest(approve: Boolean, comment: String) {

        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setMessage(getString(R.string.dialog_confirm_approve))
            .setCancelable(false)
            .setPositiveButton(getText(R.string.confirm), DialogInterface.OnClickListener {
                    dialog, id -> viewModel.handleRequest(approve, comment)
                    //Toast.makeText(activity,"Ok", Toast.LENGTH_SHORT).show()
            })
            .setNegativeButton(getText(R.string.cancel), DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle(getText(R.string.app_name))
        alert.show()
    }
}

