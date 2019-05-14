package com.nicklasslagbrand.placeholder.feature.main.message

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.domain.model.Message
import com.nicklasslagbrand.placeholder.extension.inflate
import com.nicklasslagbrand.placeholder.extension.loadImageWithFitCenterTransform
import kotlinx.android.synthetic.main.item_message.view.*

class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var clickListener: (Message) -> Unit = {}

    private var items = listOf<Message>()

    fun setItems(newItems: List<Message>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MessageViewHolder(parent.inflate(R.layout.item_message))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        (holder as MessageViewHolder).bind(item)
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message) {

            itemView.apply {
                tvMessageTitle.text = message.title
                tvMessageDescription.text = message.description
                ivMessageImage.loadImageWithFitCenterTransform(resources.getDrawable(R.drawable.ic_message))
                setOnClickListener {
                    clickListener(message)
                }
            }
        }
    }
}
