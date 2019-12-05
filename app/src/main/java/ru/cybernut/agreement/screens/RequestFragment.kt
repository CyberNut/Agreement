package ru.cybernut.agreement.screens


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import ru.cybernut.agreement.BR
import ru.cybernut.agreement.R
import ru.cybernut.agreement.adapters.PaymentRequestsAdapter
import ru.cybernut.agreement.adapters.RequestsAdapter
import ru.cybernut.agreement.databinding.FragmentRequestBinding
import ru.cybernut.agreement.viewmodels.RequestViewModel
import ru.cybernut.agreement.viewmodels.RequestViewModelFactory

class RequestFragment : Fragment() {

    private val TAG = "RequestFragment"
    private val args: RequestFragmentArgs by navArgs()
    private lateinit var binding: FragmentRequestBinding
    private val viewModel: RequestViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, RequestViewModelFactory(activity.application, args.request))
            .get(RequestViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRequestBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner(this)


        binding.recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.setHasFixedSize(true)

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)


        val adapter = RequestsAdapter(R.layout.fragment_request_item, BR.paymentRequest)
        adapter.addBindingVariable(BR.requestViewModel, viewModel)
        binding.recyclerView.adapter = adapter
        binding.viewModel = viewModel

        viewModel.requests.observe(this, Observer { requests ->
            requests?.let {
                adapter.submitList(it)
            }
        })

        viewModel.needShowToast.observe(this, Observer {
            if (it == true) {
                Toast.makeText(activity, "Test", Toast.LENGTH_SHORT).show()
                viewModel.onToastShowDone()
            }
        })

        return binding.root
    }
}
