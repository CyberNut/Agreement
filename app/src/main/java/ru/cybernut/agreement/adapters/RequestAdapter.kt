package ru.cybernut.agreement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.cybernut.agreement.data.Request
import java.util.*

class RequestsAdapter(@LayoutRes val itemLayoutId: Int, val bindingVariableId: Int, val onClickListener: OnClickListener? = null): ListAdapter<Request, RequestsAdapter.RequestViewHolder>(DiffCallback) {

    private val additionalBindingVariables = Hashtable<Int, Any>()

    companion object DiffCallback : DiffUtil.ItemCallback<Request>() {
        override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        return RequestViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), itemLayoutId, parent, false), bindingVariableId)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = getItem(position)
        if (onClickListener != null) {
            holder.itemView.setOnClickListener{ onClickListener.onClick(request) }
        }
        holder.bind(request)
    }

    fun addBindingVariable(bindingId: Int, value: Any) {
        additionalBindingVariables[bindingId] = value
    }

    inner class RequestViewHolder(private var binding: ViewDataBinding, val bindingVariableId: Int): RecyclerView.ViewHolder(binding.root) {
        fun<T> bind(variable: T) {
            binding.setVariable(bindingVariableId, variable)
            additionalBindingVariables.forEach { binding.setVariable(it.key, it.value) }
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (request: Request) -> Unit) {
        fun onClick(request: Request) = clickListener(request)
    }
}
