package ru.cybernut.agreement.screens


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.cybernut.agreement.R
import ru.cybernut.agreement.databinding.FragmentServiceRequestBinding
import ru.cybernut.agreement.utils.hideKeyboard
import ru.cybernut.agreement.viewmodels.ServiceRequestViewModel
import ru.cybernut.agreement.viewmodels.ServiceRequestViewModelFactory

class ServiceRequestFragment : Fragment() {

    private val TAG = "ServiceRequestFragment"
    private val args: ServiceRequestFragmentArgs by navArgs()

    private lateinit var binding: FragmentServiceRequestBinding
    private val viewModel: ServiceRequestViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, ServiceRequestViewModelFactory(args.request))
            .get(ServiceRequestViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentServiceRequestBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.needShowToast.observe(this, Observer {
            if (it == true) {
                //Toast.makeText(activity, "Test", Toast.LENGTH_SHORT).show()
                findNavController().navigate(ServiceRequestFragmentDirections.actionServiceRequestFragmentToServiceRequestListFragment())
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

