package com.app.message_app_kotlin.bottomnav.ui.messages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.message_app_kotlin.R
import com.app.message_app_kotlin.bottomnav.ui.chat.Chat
import com.google.firebase.auth.FirebaseAuth

class MessageFragment : Fragment(), MessagesAdapter.OnItemClickListener {

    private lateinit var messageViewModel: MessageViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessagesAdapter
    private lateinit var list: MutableList<Message>
    private lateinit var uid: String
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        messageViewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_messages, container, false)

        recyclerView = root.findViewById(R.id.messageRecycler)

        list = mutableListOf()

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            uid = mAuth.currentUser!!.uid
        }

        // retrieve all messages for user
        retrieveMessages(uid)

        for (i in 1..5) {
            val item = Message(
                    "uid$i", "M", "Michael", "Hamm$i", "username$i","This is a test message", "2 hours ago", 5
            )
            list.add(item)
        }

        adapter = MessagesAdapter(list, this)
        recyclerView.layoutManager = LinearLayoutManager(parentFragment?.context)
        recyclerView.adapter = adapter
        return root
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(context, Chat::class.java)
        intent.putExtra("firstName", list[position].firstName)
        intent.putExtra("lastName", list[position].lastName)
        intent.putExtra("uid", list[position].uid)
        intent.putExtra("username", list[position].userName)
        startActivity(intent)
    }

    private fun retrieveMessages(uid: String) {
    }
}