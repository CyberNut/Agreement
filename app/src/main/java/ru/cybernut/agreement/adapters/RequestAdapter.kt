package ru.cybernut.agreement.adapters

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.cybernut.agreement.data.Request
import java.util.*

class RequestsAdapter(@LayoutRes val itemLayoutId: Int, val bindingVariableId: Int, val onClickListener: OnClickListener? = null): ListAdapter<Request, RequestsAdapter.RequestViewHolder>(DiffCallback) {

    private val additionalBindingVariables = Hashtable<Int, Any>()
    var tracker: SelectionTracker<Request>? = null

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
        tracker?.let {
            holder.bind(request, it.isSelected(request))
        }

        if (onClickListener != null) {
            holder.itemView.setOnClickListener{
                if (tracker == null || tracker?.hasSelection() == false) {
                    onClickListener.onClick(request)
                }
            }
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

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Request> =
            object: ItemDetailsLookup.ItemDetails<Request>() {
                override fun getSelectionKey(): Request? {
                    return getItem(adapterPosition) as Request?
                }

                override fun getPosition(): Int {
                    return adapterPosition
                }
            }
    }

    class OnClickListener(val clickListener: (request: Request) -> Unit) {
        fun onClick(request: Request) = clickListener(request)
    }

    inner class RequestKeyProvider: ItemKeyProvider<Request>(ItemKeyProvider.SCOPE_MAPPED){
        override fun getKey(position: Int) = getItem(position)
        override fun getPosition(key: Request)  = currentList.indexOf(key)
    }

    inner class MyItemDetailsLookup(private val recyclerView: RecyclerView): ItemDetailsLookup<Request>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Request>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            if(view != null) {
                return (recyclerView.getChildViewHolder(view) as RequestsAdapter.RequestViewHolder).getItemDetails()
            }
            return null
        }
    }
}
