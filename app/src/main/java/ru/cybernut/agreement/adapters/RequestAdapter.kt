package ru.cybernut.agreement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.cybernut.agreement.data.Request

class RequestsAdapter(val itemLayoutId: Int, val onClickListener: OnClickListener? = null): ListAdapter<Request, RequestViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Request>() {
        override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        return RequestViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), itemLayoutId, parent, false))

    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = getItem(position)
        if (onClickListener != null) {
            holder.itemView.setOnClickListener{ onClickListener.onClick(request) }
        }
        holder.bind(request)
    }

    class OnClickListener(val clickListener: (request: Request) -> Unit) {
        fun onClick(request: Request) = clickListener(request)
    }
}
