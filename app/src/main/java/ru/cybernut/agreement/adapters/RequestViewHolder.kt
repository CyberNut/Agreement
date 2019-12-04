package ru.cybernut.agreement.adapters

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import ru.cybernut.agreement.BR
import ru.cybernut.agreement.data.Request

open class RequestViewHolder(private var binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
    open fun bind(request: Request) {
        binding.setVariable(BR.request, request)
        binding.executePendingBindings()
    }
}
