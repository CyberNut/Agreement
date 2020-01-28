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
import androidx.navigation.fragment.navArgs
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.BR
import ru.cybernut.agreement.R
import ru.cybernut.agreement.adapters.RequestsAdapter
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.databinding.FragmentRequestListBinding
import ru.cybernut.agreement.db.AgreementsDatabase
import ru.cybernut.agreement.db.BaseRequestDao
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.utils.RequestType
import ru.cybernut.agreement.viewmodels.PaymentRequestListViewModel
import ru.cybernut.agreement.viewmodels.PaymentRequestListViewModelFactory
import ru.cybernut.agreement.viewmodels.RequestListViewModel
import ru.cybernut.agreement.viewmodels.RequestListViewModelFactory

class RequestListFragment : Fragment() {

    private val args: RequestListFragmentArgs by navArgs()
    private lateinit var binding: FragmentRequestListBinding

    private val viewModel: RequestListViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, RequestListViewModelFactory(activity.application, args.requestType))
            .get(RequestListViewModel::class.java)
    }

    private var requestListLayoutId: Int = 0
    private var requestBindingId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try {
            val lc = AgreementApp.loginCredential
        } catch (ex: UninitializedPropertyAccessException ) {
            this.findNavController().navigate(RequestListFragmentDirections.actionRequestListFragmentToLoginFragment())
        }

        //viewModel = ViewModelProviders.of(this, RequestListViewModelFactory(activity!!.application, args.requestType)).get(RequestListViewModel::class.java)

        binding = FragmentRequestListBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        when (args.requestType) {
            RequestType.MONEY -> {
                requestListLayoutId = R.layout.payment_request_list_item
                requestBindingId = BR.request
            }
            RequestType.SERVICE -> {
                requestListLayoutId = R.layout.service_request_list_item
                requestBindingId = BR.serviceRequest
            }
            RequestType.DELIVERY -> {
                requestListLayoutId = R.layout.payment_request_list_item
                requestBindingId = BR.request
            }
        }

        val adapter = RequestsAdapter(requestListLayoutId, requestBindingId, RequestsAdapter.OnClickListener{viewModel.showRequest(it)})
        binding.requestsList.layoutManager = LinearLayoutManager(activity)
        binding.requestsList.setHasFixedSize(true)
        binding.requestsList.adapter = adapter

//        viewModel.getRequests().observe(this, Observer { requests ->
//                        requests?.let {
//                adapter.submitList(it)
//            }
//            //Toast.makeText(activity, "Update done!", Toast.LENGTH_SHORT).show()
//            binding.swipeRefresh.isRefreshing = false
//        })
        viewModel.paymentRequests.observe(this, Observer { requests ->
            requests?.let {
                adapter.submitList(it)
            }
            //Toast.makeText(activity, "Update done!", Toast.LENGTH_SHORT).show()
            binding.swipeRefresh.isRefreshing = false
        })

//        when (args.requestType) {
//            RequestType.MONEY -> {
//                viewModel.requests.observe(this, Observer { requests ->
//                    requests?.let {
//                        adapter.submitList(it)
//                    }
//                    //Toast.makeText(activity, "Update done!", Toast.LENGTH_SHORT).show()
//                    binding.swipeRefresh.isRefreshing = false
//                })
//
//            }
//            RequestType.SERVICE -> {
//                viewModel.serviceRequests.observe(this, Observer { requests ->
//                    requests?.let {
//                        adapter.submitList(it)
//                    }
//                    //Toast.makeText(activity, "Update done!", Toast.LENGTH_SHORT).show()
//                    binding.swipeRefresh.isRefreshing = false
//                })
//            }
//            RequestType.DELIVERY -> {
//            }
//        }

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
            viewModel.updateRequests()
        }
    }

}
