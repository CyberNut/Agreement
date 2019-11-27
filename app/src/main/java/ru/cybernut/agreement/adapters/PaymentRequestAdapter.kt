package ru.cybernut.agreement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.cybernut.agreement.databinding.PaymentRequestListItemBinding
import ru.cybernut.agreement.db.PaymentRequest

class PaymentRequestsAdapter( val onClickListener: OnClickListener ) :
    ListAdapter<PaymentRequest, PaymentRequestsAdapter.PaymentRequestViewHolder>(DiffCallback) {

    class PaymentRequestViewHolder(private var binding: PaymentRequestListItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(request: PaymentRequest) {
            binding.request = request
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [MarsProperty]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<PaymentRequest>() {
        override fun areItemsTheSame(oldItem: PaymentRequest, newItem: PaymentRequest): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PaymentRequest, newItem: PaymentRequest): Boolean {
            return oldItem.uuid == newItem.uuid
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PaymentRequestViewHolder {
        return PaymentRequestViewHolder(PaymentRequestListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: PaymentRequestViewHolder, position: Int) {
        val request = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(request)
        }
        holder.bind(request)
    }

    class OnClickListener(val clickListener: (request: PaymentRequest) -> Unit) {
        fun onClick(request: PaymentRequest) = clickListener(request)
    }
}
