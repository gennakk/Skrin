package com.rod.skrin.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener

import com.rod.skrin.R
import com.rod.skrin.adapters.ChatAdapter
import com.rod.skrin.extensions.toast
import com.rod.skrin.models.Message
import com.rod.skrin.models.TotalMessagesEvent
import com.rod.skrin.utils.RxBus
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatFragment : Fragment() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private lateinit var _view:View
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var chatDBRef: CollectionReference
    private lateinit var adapter: ChatAdapter
    private val messageList: ArrayList<Message> = ArrayList()

    private var chatSubsctiption : ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _view =  inflater.inflate(R.layout.fragment_chat, container, false)

        setUpChatDB()
        setUpCurrentUser()
        setUpRecyclerView()
        setUpChatBtn()

        suscribeToChatMessages()


        return _view
    }

    private fun setUpChatDB(){
        chatDBRef = store.collection("chat")

    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!

    }

    private fun setUpRecyclerView(){
        val layoutManager = LinearLayoutManager(context)
        adapter = ChatAdapter(messageList,currentUser.uid)

        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()
        _view.recyclerView.adapter = adapter
    }

    private fun setUpChatBtn(){
        _view.buttonSend.setOnClickListener{
            val messageText = editTextMessage.text.toString()

            if(messageText.isNotEmpty()){
                val photo = currentUser.photoUrl?.let{
                    currentUser.photoUrl.toString()
                } ?: run{
                    ""
                }
                val message = Message(currentUser.uid,messageText,photo, Date())

                //Guardar mensaje en firebase
                saveMessage(message)

                //vaciar edit text
                _view.editTextMessage.setText("")


            }

        }
    }

    private fun saveMessage(message:Message){
        val newMessage = HashMap<String,Any>()

        newMessage["authorId"] = message.authorId
        newMessage["message"] = message.message
        newMessage["profileImageURL"] = message.profileImageURL
        newMessage["sentAt"] = message.sentAt

        chatDBRef.add(newMessage)
            .addOnCompleteListener{
                activity!!.toast("Message added!!")
            }
            .addOnFailureListener{
                activity!!.toast("Message error, try again!!")
            }
    }

    private fun suscribeToChatMessages(){
        chatSubsctiption = chatDBRef
            .orderBy("sentAt", Query.Direction.ASCENDING)
            .addSnapshotListener(object: java.util.EventListener,EventListener<QuerySnapshot>{
            override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {

                exception?:let {
                    activity!!.toast("Exception!!")
                    return
                }

                snapshot?.let {

                    messageList.clear()

                    val messages = it.toObjects(Message::class.java)

                    messageList.addAll(messages.asReversed())

                    adapter.notifyDataSetChanged()

                    _view.recyclerView.smoothScrollToPosition(messageList.size)

                    RxBus.publish(TotalMessagesEvent(messageList.size))

                    adapter.notifyDataSetChanged()

                }

            }


        })
    }

    override fun onDestroy() {
        chatSubsctiption?.remove()
        super.onDestroy()
    }

}
