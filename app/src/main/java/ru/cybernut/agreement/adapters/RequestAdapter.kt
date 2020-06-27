package ru.cybernut.agreement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.cybernut.agreement.data.Request
import java.util.*

class RequestsAdapter(@LayoutRes val itemLayoutId: Int, val bindingVariableId: Int, val onClickListener: OnClickListener? = null): ListAdapter<Request, RequestsAdapter.RequestViewHolder>(DiffCallback) {

    private val additionalBindingVariables = Hashtable<Int, Any>()
    var tracker: SelectionTracker<String>? = null

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
        tracker?.let {
            holder.bind(request, it.isSelected(request.uuid))
        }
    }


    fun addBindingVariable(bindingId: Int, value: Any) {
        additionalBindingVariables[bindingId] = value
    }

    inner class RequestViewHolder(private var binding: ViewDataBinding, val bindingVariableId: Int): RecyclerView.ViewHolder(binding.root) {
        fun<T> bind(variable: T, isActivated: Boolean = false) {
            binding.setVariable(bindingVariableId, variable)
            additionalBindingVariables.forEach { binding.setVariable(it.key, it.value) }
            binding.executePendingBindings()
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object: ItemDetailsLookup.ItemDetails<String>() {
                override fun getSelectionKey(): String? {
                    return getItem(adapterPosition).uuid
                }

                override fun getPosition(): Int {
                    return adapterPosition
                }
            }
    }

    class OnClickListener(val clickListener: (request: Request) -> Unit) {
        fun onClick(request: Request) = clickListener(request)
    }
}
