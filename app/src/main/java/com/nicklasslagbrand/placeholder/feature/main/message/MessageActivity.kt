package com.nicklasslagbrand.placeholder.feature.main.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.nicklasslagbrand.placeholder.R
import com.nicklasslagbrand.placeholder.data.viewmodel.ConsumableEvent
import com.nicklasslagbrand.placeholder.data.viewmodel.MessageViewModel
import com.nicklasslagbrand.placeholder.domain.model.Message
import com.nicklasslagbrand.placeholder.extension.observe
import com.nicklasslagbrand.placeholder.extension.visible
import com.nicklasslagbrand.placeholder.feature.base.BaseActivity
import kotlinx.android.synthetic.main.activity_messages.*
import org.koin.android.viewmodel.ext.android.viewModel

class MessageActivity : BaseActivity() {
    private val viewModel: MessageViewModel by viewModel()
    private var messageAdapter: MessageAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        initViews()

        subscribeToLiveData()

    }


    override fun onResume() {
        super.onResume()

        viewModel.initialize()
    }

    private fun subscribeToLiveData() {
        observe(viewModel.eventLiveData) {
            handleEvents(it)
        }
        observe(viewModel.failure, ::handleFailure)
    }

    private fun handleEvents(event: ConsumableEvent<MessageViewModel.Event>) {
        event.handleIfNotConsumed {
            return@handleIfNotConsumed when (it) {
                is MessageViewModel.Event.ShowMessage -> {
                    handleMessages(it.messages)
                    true
                }
                is MessageViewModel.Event.ShowLoading -> {
                    pbMessagesProgressBar.visibility = View.VISIBLE
                    true
                }
                is MessageViewModel.Event.HideLoading -> {
                    pbMessagesProgressBar.visibility = View.GONE
                    true
                }
            }
        }
    }

    private fun initViews() {
        messageAdapter = MessageAdapter().apply {
            clickListener = {
                //TODO: load message details screen
            }
        }
        rvMessageList.adapter = messageAdapter

    }

    private fun handleMessages(messages: List<Message>){
        rvMessageList.visible()
        messageAdapter?.setItems(messages)
    }


    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MessageActivity::class.java))
        }
    }

}
