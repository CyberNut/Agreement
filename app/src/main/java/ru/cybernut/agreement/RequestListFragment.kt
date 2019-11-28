package ru.cybernut.agreement


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import ru.cybernut.agreement.adapters.PaymentRequestsAdapter
import ru.cybernut.agreement.databinding.FragmentRequestListBinding
import ru.cybernut.agreement.viewmodels.RequestListViewModel

class RequestListFragment : Fragment() {

    private lateinit var binding: FragmentRequestListBinding
    private val viewModel: RequestListViewModel by lazy {
        ViewModelProviders.of(this).get(RequestListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRequestListBinding.inflate(inflater, container, false)

        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        val adapter = PaymentRequestsAdapter(PaymentRequestsAdapter.OnClickListener{ viewModel.showPaymentRequest(it)})
        binding.requestsList.adapter = adapter

        viewModel.requests.observe(this, Observer { requests ->
            requests?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToSelectedRequest.observe(this, Observer {
            if (null != it) {
                this.findNavController().navigate(RequestListFragmentDirections.actionRequestListFragmentToRequestFragment(it))
                viewModel.navigateToSelectedRequestComplete()
            }
        })

        return binding.root
    }
}
