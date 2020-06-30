package ru.cybernut.agreement.screens

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import org.koin.core.qualifier.named
import ru.cybernut.agreement.AgreementApp
import ru.cybernut.agreement.BR
import ru.cybernut.agreement.R
import ru.cybernut.agreement.adapters.ActionModeController
import ru.cybernut.agreement.adapters.RequestsAdapter
import ru.cybernut.agreement.data.Request
import ru.cybernut.agreement.databinding.FragmentDeliveryRequestListBinding
import ru.cybernut.agreement.db.DeliveryRequest
import ru.cybernut.agreement.utils.ApprovalType
import ru.cybernut.agreement.utils.MIN_SEARCH_QUERY_LENGHT
import ru.cybernut.agreement.viewmodels.RequestListViewModel

class DeliveryRequestListFragment : Fragment(), KoinComponent, ActionModeController.Approvable {

    private lateinit var binding: FragmentDeliveryRequestListBinding
    private lateinit var menu: Menu
    private var actionMode: ActionMode? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val viewModel: RequestListViewModel<DeliveryRequest> by viewModel(named("delivery"))
    private lateinit var tracker: SelectionTracker<Request>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try {
            val lc = AgreementApp.loginCredential
        } catch (ex: UninitializedPropertyAccessException ) {
            this.findNavController().navigate(R.id.loginActivity)
        }

        firebaseAnalytics = Firebase.analytics

        binding = FragmentDeliveryRequestListBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        val adapter = RequestsAdapter(R.layout.delivery_request_list_item, BR.deliveryRequest, RequestsAdapter.OnClickListener{viewModel.showRequest(it)})
        binding.requestsList.layoutManager = LinearLayoutManager(activity)
        binding.requestsList.setHasFixedSize(true)
        binding.requestsList.itemAnimator = null
        binding.requestsList.adapter = adapter

        tracker = SelectionTracker.Builder<Request>(
            "mySelection",
            binding.requestsList,
            adapter.RequestKeyProvider(),
            adapter.MyItemDetailsLookup(binding.requestsList),
            StorageStrategy.createParcelableStorage(Request::class.java)
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        tracker.addObserver(object : SelectionTracker.SelectionObserver<Request>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                if (tracker.hasSelection() && actionMode == null) {
                    actionMode = (activity as AppCompatActivity)?.startSupportActionMode(
                        ActionModeController(tracker, this@DeliveryRequestListFragment)
                    )
                    setSelectedTitle(tracker.selection.size())
                } else if (!tracker.hasSelection()) {
                    actionMode?.finish()
                    actionMode = null
                } else {
                    setSelectedTitle(tracker.selection.size())
                }
            }
        })
        adapter.tracker = tracker

        viewModel.requests.observe(viewLifecycleOwner) { requests ->
            requests?.let {
                adapter.submitList(it)
            }
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.navigateToSelectedRequest.observe(viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(
                    DeliveryRequestListFragmentDirections.actionDeliveryRequestListFragmentToDeliveryRequestFragment(
                        it as DeliveryRequest
                    )
                )
                viewModel.navigateToSelectedRequestComplete()
            }
        }

        viewModel.approveResult.observe(viewLifecycleOwner) {
            when(it) {
                ApprovalType.APPROVE, ApprovalType.DECLINE -> {
                    firebaseAnalytics.logEvent("approve_decline_request") {
                        param("type", "payment")
                    }
                    Toast.makeText(activity, getString(R.string.success_approve_decline_toast_message, if(it == ApprovalType.APPROVE) getString(
                        R.string.approved) else getString(R.string.declined)), Toast.LENGTH_SHORT).show()
                    viewModel.onApproveRequestDone()
                }
                ApprovalType.ERROR -> {
                    Toast.makeText(activity, getString(R.string.approve_decline_error_toast), Toast.LENGTH_LONG).show()
                    viewModel.onApproveRequestDone()
                }
            }
        }

        initSwipeToRefresh()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setSelectedTitle(selected: Int) {
        actionMode?.title = getString(R.string.selected) + " $selected"
    }


    override fun ApproveSelected() {
        if (tracker.hasSelection()) {
            val listOfRequest = mutableListOf<String>()
            tracker.selection.forEach { listOfRequest.add(it.uuid) }
            if (listOfRequest.size > 0) {
                handleThisRequest(listOfRequest)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        actionMode?.finish()
        actionMode = null
    }

    private fun handleThisRequest(requestIds: List<String>) {

        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setMessage(getString(R.string.dialog_confirm_approve))
            .setCancelable(false)
            .setPositiveButton(getText(R.string.confirm), DialogInterface.OnClickListener {
                    dialog, id -> viewModel.approveSelected(requestIds)
            })
            .setNegativeButton(getText(R.string.cancel), DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle(getText(R.string.app_name))
        alert.show()
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
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            clearFilter()
            viewModel.forceUpdateRequests()
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