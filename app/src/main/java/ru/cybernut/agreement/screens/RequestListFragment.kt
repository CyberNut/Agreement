package ru.cybernut.agreement.screens


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import ru.cybernut.agreement.screens.RequestListFragmentDirections
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
            binding.swipeRefresh.isRefreshing = false
        })

        viewModel.navigateToSelectedRequest.observe(this, Observer {
            if (null != it) {
                this.findNavController().navigate(
                    RequestListFragmentDirections.actionRequestListFragmentToRequestFragment(
                        it
                    )
                )
                viewModel.navigateToSelectedRequestComplete()
            }
        })

        initSwipeToRefresh()

        return binding.root
    }

    private fun initSwipeToRefresh() {
//        model.refreshState.observe(this, Observer {
//            swipe_refresh.isRefreshing = it == NetworkState.LOADING
//        })
        binding.swipeRefresh.isRefreshing = true
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.updatePaymentRequests()
        }
    }

}
