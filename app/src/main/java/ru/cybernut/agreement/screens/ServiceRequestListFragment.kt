package ru.cybernut.agreement.screens

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.BR
import ru.cybernut.agreement.R
import ru.cybernut.agreement.adapters.RequestsAdapter
import ru.cybernut.agreement.databinding.FragmentServiceRequestListBinding
import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.viewmodels.ServiceRequestListViewModel


class ServiceRequestListFragment : Fragment() {

    //private val args: RequestListFragmentArgs by navArgs()
    private lateinit var binding: FragmentServiceRequestListBinding

    private val viewModel: ServiceRequestListViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this).get(ServiceRequestListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val navController = this.findNavController()
        navController.graph.startDestination = R.id.deliveryRequestListFragment
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.navigation_service)
        if (navController.graph.id != graph.id) {
            navController.graph = graph
        }

        try {
            val lc = AgreementApp.loginCredential
        } catch (ex: UninitializedPropertyAccessException ) {
            this.findNavController().navigate(R.id.loginActivity)
        }

        binding = FragmentServiceRequestListBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        val adapter = RequestsAdapter(R.layout.service_request_list_item, BR.serviceRequest, RequestsAdapter.OnClickListener{viewModel.showRequest(it)})
        binding.requestsList.layoutManager = LinearLayoutManager(activity)
        binding.requestsList.setHasFixedSize(true)
        binding.requestsList.adapter = adapter

        viewModel.requests.observe(this, Observer { requests ->
            requests?.let {
                adapter.submitList(it)
            }
            //Toast.makeText(activity, "Update done!", Toast.LENGTH_SHORT).show()
            binding.swipeRefresh.isRefreshing = false
        })

        viewModel.navigateToSelectedRequest.observe(this, Observer {
            if (null != it) {
                this.findNavController().navigate(
                    ServiceRequestListFragmentDirections.actionServiceRequestListFragmentToServiceRequestFragment(
                        it as ServiceRequest
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