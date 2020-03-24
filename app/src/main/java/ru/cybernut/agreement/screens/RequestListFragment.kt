package ru.cybernut.agreement.screens


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.BR
import ru.cybernut.agreement.R
import ru.cybernut.agreement.adapters.RequestsAdapter
import ru.cybernut.agreement.databinding.FragmentRequestListBinding
import ru.cybernut.agreement.db.PaymentRequest
import ru.cybernut.agreement.utils.MIN_SEARCH_QUERY_LENGHT
import ru.cybernut.agreement.viewmodels.PaymentRequestListViewModel

class RequestListFragment : Fragment() {

    private val TAG = "RequestListFragment"
    //private val args: RequestListFragmentArgs by navArgs()
    private lateinit var binding: FragmentRequestListBinding
    private lateinit var menu: Menu

    private val viewModel: PaymentRequestListViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this).get(PaymentRequestListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try {
            val lc = AgreementApp.loginCredential
        } catch (ex: UninitializedPropertyAccessException ) {
            this.findNavController().navigate(R.id.loginActivity)
        }

        binding = FragmentRequestListBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        val adapter = RequestsAdapter(R.layout.payment_request_list_item, BR.request, RequestsAdapter.OnClickListener{viewModel.showRequest(it)})
        binding.requestsList.layoutManager = LinearLayoutManager(activity)
        binding.requestsList.setHasFixedSize(true)
        binding.requestsList.itemAnimator = null
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
        this.menu = menu
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
            clearFilter()
            viewModel.updateRequests()
        }
    }

    private fun clearFilter() {
        if(::menu.isInitialized) {
            var searchMenuItem = menu?.findItem(R.id.action_search)
            if (searchMenuItem != null) {
                var searchView = searchMenuItem?.actionView as SearchView
                searchView?.setQuery("", false)
                searchView?.clearFocus()
                searchMenuItem.collapseActionView()
            }
        }
    }
}
