package ru.cybernut.agreement.screens


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import ru.cybernut.agreement.R
import ru.cybernut.agreement.databinding.FragmentRequestBinding
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.utils.ApprovalType
import ru.cybernut.agreement.utils.hideKeyboard
import ru.cybernut.agreement.viewmodels.RequestViewModel

class RequestFragment : Fragment() {

    private val args: RequestFragmentArgs by navArgs()

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: FragmentRequestBinding
    private val viewModel: RequestViewModel<PaymentRequest> by viewModel(named("payment")) { parametersOf(args.request)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRequestBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        firebaseAnalytics = Firebase.analytics

        binding.request = viewModel.request

        viewModel.approveResult.observe(viewLifecycleOwner) {
            when(it) {
                ApprovalType.APPROVE, ApprovalType.DECLINE -> {
                    firebaseAnalytics.logEvent("approve_decline_request") {
                        param("type", "payment")
                        param("number", args.request.number)
                    }
                    Toast.makeText(activity, getString(R.string.success_approve_decline_toast_message, if(it == ApprovalType.APPROVE) getString(
                                            R.string.approved) else getString(R.string.declined)), Toast.LENGTH_SHORT).show()
                    findNavController().navigate(RequestFragmentDirections.actionRequestFragmentToRequestListFragment())
                    viewModel.onApproveRequestDone()
                }
                ApprovalType.ERROR -> {
                    Toast.makeText(activity, getString(R.string.approve_decline_error_toast), Toast.LENGTH_LONG).show()
                    viewModel.onApproveRequestDone()
                }
            }
        }

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
            })
            .setNegativeButton(getText(R.string.cancel), DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle(getText(R.string.app_name))
        alert.show()
    }
}