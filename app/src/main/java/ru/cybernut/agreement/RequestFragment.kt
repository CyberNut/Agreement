package ru.cybernut.agreement


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
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

        binding.viewModel = viewModel

        return binding.root
    }
}
