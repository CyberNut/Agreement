package ru.cybernut.agreement.screens

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import org.koin.core.qualifier.named
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.BR
import ru.cybernut.agreement.R
import ru.cybernut.agreement.adapters.RequestsAdapter
import ru.cybernut.agreement.databinding.FragmentServiceRequestListBinding
import ru.cybernut.agreement.db.ServiceRequest
import ru.cybernut.agreement.utils.MIN_SEARCH_QUERY_LENGHT
import ru.cybernut.agreement.viewmodels.ListViewModel

class ServiceRequestListFragment : Fragment(), KoinComponent {

    private lateinit var binding: FragmentServiceRequestListBinding

    private val viewModel: ListViewModel<ServiceRequest> by viewModel(named("service"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
        binding.requestsList.itemAnimator = null
        binding.requestsList.adapter = adapter

        viewModel.requests.observe(viewLifecycleOwner) { requests ->
            requests?.let {
                adapter.submitList(it)
            }
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.navigateToSelectedRequest.observe(viewLifecycleOwner)  {
            if (null != it) {
                this.findNavController().navigate(
                    ServiceRequestListFragmentDirections.actionServiceRequestListFragmentToServiceRequestFragment(
                        it as ServiceRequest
                    )
                )
                viewModel.navigateToSelectedRequestComplete()
            }
        }

        initSwipeToRefresh()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
        var searchMenuItem = menu.findItem(R.id.action_search)
        var searchView = searchMenuItem.actionView as SearchView
        searchView.setQuery(viewModel.filter.value, true)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.trim().length >= MIN_SEARCH_QUERY_LENGHT) {
                    viewModel.setFilter(query.trim())
                } else {
                    viewModel.setFilter("")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.trim().length >= MIN_SEARCH_QUERY_LENGHT) {
                    viewModel.setFilter(newText.trim())
                } else {
                    viewModel.setFilter("")
                }
                return true
            }
        } )
    }

    private fun initSwipeToRefresh() {
        binding.swipeRefresh.isRefreshing = true
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.forceUpdateRequests()
        }
    }

}