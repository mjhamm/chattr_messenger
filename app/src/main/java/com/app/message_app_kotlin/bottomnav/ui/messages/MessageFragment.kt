package com.app.message_app_kotlin.bottomnav.ui.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.message_app_kotlin.R
import com.app.message_app_kotlin.bottomnav.ui.chat.Chat
import com.app.message_app_kotlin.bottomnav.ui.viewmodels.FriendViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MessageFragment : Fragment(), MessagesAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessagesAdapter
    private lateinit var list: MutableList<Message>
    private lateinit var uid: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var groupList: MutableList<String>
    private lateinit var messageLoading: ContentLoadingProgressBar

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val model = activity?.run {
//            ViewModelProviders.of(this).get(FriendViewModel::class.java)
//        } ?: throw Exception("Invalid Activity")
//
//        model.data.observe(viewLifecycleOwner, { t ->
//            val item = Message(
//                "12345", "M", "Michael", "Hamm", "MHAMM",t!!, "2 hours ago", 5
//            )
//            list.add(0, item)
//            adapter = MessagesAdapter(list, this)
//            recyclerView.adapter = adapter
//        })
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_messages, container, false)

        recyclerView = root.findViewById(R.id.messageRecycler)
        messageLoading = root.findViewById(R.id.messagesLoading)
        val messageSearchView: SearchView = root.findViewById(R.id.messages_searchView)

        list = mutableListOf()
        groupList = mutableListOf()

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            uid = mAuth.currentUser!!.uid
        }

        messageLoading.show()

        // retrieve all messages for user
        retrieveGroups(uid)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                view?.clearFocus()
            }
        })

        messageSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                adapter.filter(p0.toString())
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter(p0.toString())
                return true
            }
        })

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

    private fun retrieveGroups(uid: String) {

        messageLoading.show()

        FirebaseFirestore.getInstance().collection("users")
            .document(uid)
            .collection("groups")
            .get()
            .addOnSuccessListener { groups ->
                for (group in groups) {
                    val groupId = group.id
                    groupList.add(groupId)
                }

                if (groupList.isNotEmpty()) {
                    retrieveMessages(groupList)
                } else {
                    messageLoading.hide()
                }
            }
    }

    private fun retrieveMessages(groupList: MutableList<String>) {

        var remainingQueries = groupList.size

        for (group in groupList) {
            FirebaseFirestore.getInstance().collection("group")
                .document(group)
                .get()
                .addOnSuccessListener { messageItem ->
                    val recentMap = messageItem.get("recentMessage") as Map<*, *>
                    var recentMessage = ""

                    for (item in recentMap) {
                        when (item.key) {
                            "messageText" -> {
                                recentMessage = item.value.toString()
                            }
                        }
                    }

                    val item = Message(
                        "uid",
                        "MH",
                        "Michael",
                        "Hamm",
                        "Username",
                        recentMessage,
                        "2 HOURS AGO",
                        4
                    )
                    list.add(item)

                    if (--remainingQueries == 0) {

                        messageLoading.hide()

                        adapter = MessagesAdapter(list, this)
                        recyclerView.layoutManager = LinearLayoutManager(parentFragment?.context)
                        recyclerView.adapter = adapter
                    }

                }
        }

    }

}
