package ru.cybernut.agreement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.cybernut.agreement.R
import ru.cybernut.agreement.data.Request
import java.util.*

class RequestsAdapter(@LayoutRes val itemLayoutId: Int, @LayoutRes val emptyLayoutId: Int, val bindingVariableId: Int?, val onClickListener: OnClickListener? = null): ListAdapter<Request, RequestsAdapter.RequestViewHolder>(DiffCallback) {

    private val additionalBindingVariables = Hashtable<Int, Any>()
    private var itemViewType: Int = 1

    companion object DiffCallback : DiffUtil.ItemCallback<Request>() {
        override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        return if (itemViewType == 0)
            RequestViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), emptyLayoutId, parent, false), null)
        else
            RequestViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), itemLayoutId, parent, false), bindingVariableId)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        if (itemViewType == 1) {
            val request = getItem(position)
            if (onClickListener != null) {
                holder.itemView.setOnClickListener { onClickListener.onClick(request) }
            }
            holder.bind(request)
        }
    }

    override fun submitList(list: MutableList<Request>?) {
        super.submitList(list)

    }

    override fun getItemViewType(position: Int): Int {
        if (currentList!= null && currentList.size > 0) {
            itemViewType = 1
            return super.getItemViewType(position)
        } else {
            itemViewType = 0
            return 0
        }
    }

    override fun getItemCount(): Int {
        if (currentList!= null && currentList.size > 0) {
            return currentList.size
        } else {
            return 1
        }
    }

    fun addBindingVariable(bindingId: Int, value: Any) {
        additionalBindingVariables[bindingId] = value
    }

    inner class RequestViewHolder(private var binding: ViewDataBinding, val bindingVariableId: Int?): RecyclerView.ViewHolder(binding.root) {
        fun<T> bind(variable: T) {
            if (bindingVariableId != null) {
                binding.setVariable(bindingVariableId, variable)
            }
            additionalBindingVariables.forEach { binding.setVariable(it.key, it.value) }
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (request: Request) -> Unit) {
        fun onClick(request: Request) = clickListener(request)
    }
}
