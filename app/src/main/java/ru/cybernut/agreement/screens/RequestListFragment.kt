package ru.cybernut.agreement.screens


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.BR
import ru.cybernut.agreement.R
import ru.cybernut.agreement.adapters.RequestsAdapter
import ru.cybernut.agreement.databinding.FragmentRequestListBinding
import ru.cybernut.agreement.db.PaymentRequest
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

        try {
            val lc = AgreementApp.loginCredential
        } catch (ex: UninitializedPropertyAccessException ) {
            this.findNavController().navigate(RequestListFragmentDirections.actionRequestListFragmentToLoginFragment())
        }

        binding = FragmentRequestListBinding.inflate(inflater, container, false)

        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        //val adapter = RequestsAdapter(R.layout.payment_request_list_item, BR.request, RequestsAdapter.OnClickListener{viewModel.showPaymentRequest(it)})
        val adapter = RequestsAdapter(R.layout.service_request_list_item, BR.serviceRequest, RequestsAdapter.OnClickListener{viewModel.showPaymentRequest(it)})
        binding.requestsList.layoutManager = LinearLayoutManager(activity)
        binding.requestsList.setHasFixedSize(true)
        binding.requestsList.adapter = adapter

//        viewModel.requests.observe(this, Observer { requests ->
//            requests?.let {
//                adapter.submitList(it)
//            }
//            //Toast.makeText(activity, "Update done!", Toast.LENGTH_SHORT).show()
//            binding.swipeRefresh.isRefreshing = false
//        })

        viewModel.serviceRequests.observe(this, Observer { requests ->
            requests?.let {
                adapter.submitList(it)
            }
            //Toast.makeText(activity, "Update done!", Toast.LENGTH_SHORT).show()
            binding.swipeRefresh.isRefreshing = false
        })

        viewModel.navigateToSelectedRequest.observe(this, Observer {
            if (null != it) {
                this.findNavController().navigate(
                    RequestListFragmentDirections.actionRequestListFragmentToRequestFragment(
                        it as PaymentRequest
                    )
                )
                viewModel.navigateToSelectedRequestComplete()
            }
        })

        initSwipeToRefresh()

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
            view!!.findNavController())
                || super.onOptionsItemSelected(item)
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
