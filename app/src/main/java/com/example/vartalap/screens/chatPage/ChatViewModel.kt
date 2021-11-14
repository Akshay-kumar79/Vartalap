package com.example.vartalap.screens.chatPage

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vartalap.models.ChatMessage
import com.example.vartalap.models.User
import com.example.vartalap.utils.Constants
import com.example.vartalap.utils.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatViewModel(private val receiverUser: User, application: Application) : AndroidViewModel(application) {

    private val preferenceManager = PreferenceManager(application)
    private val database = FirebaseFirestore.getInstance()

    //properties
    val inputMessage = MutableLiveData<String>()

    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _chatMessages = MutableLiveData<List<ChatMessage>>()
    val chatMessages: LiveData<List<ChatMessage>>
        get() = _chatMessages

    //utils
    private val _visibleScreen = MutableLiveData<String>()
    val visibleScreen: LiveData<String>
    get() = _visibleScreen

    var messageInsertMode = Constants.MODE_SET_DATA
    var conversionId: String? = null

    private val list: MutableList<ChatMessage> = ArrayList()

    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null)
            return@EventListener
        if (value != null) {
            val count = list.size
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val chatMessage = ChatMessage(
                        documentChange.document.getString(Constants.KEY_SENDER_ID) ?: "",
                        documentChange.document.getString(Constants.KEY_RECEIVER_ID) ?: "",
                        documentChange.document.getString(Constants.KEY_MESSAGE) ?: "",
                        getReadableDateTime(documentChange.document.getDate(Constants.KEY_TIMESTAMP) ?: Date()),
                        documentChange.document.getDate(Constants.KEY_TIMESTAMP) ?: Date(),
                        "","",""
                    )
                    list.add(chatMessage)
                }
            }
            list.sortWith(compareBy { it.date })
            if (count == 0){
                messageInsertMode = Constants.MODE_SET_DATA
                _chatMessages.value = list
            }else{
                messageInsertMode = Constants.MODE_INSERT_DATA
                _chatMessages.value = list
            }
        }
        _visibleScreen.value = Constants.SCREEN_MAIN_CONTENT
        if (conversionId == null){
            checkForConversion()
        }
    }

    private val conversionOnCompleteListener = OnCompleteListener<QuerySnapshot>{ task ->
        if(task.isSuccessful && task.result != null && task.result!!.documents.size > 0){
            val documentSnapshot = task.result!!.documents[0]
            conversionId = documentSnapshot.id
        }
    }

    init {
        _visibleScreen.value = Constants.SCREEN_LOADING
        inputMessage.value = ""
        _name.value = receiverUser.name
        listenMessages()
    }

    private fun listenMessages(){

        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
            .addSnapshotListener(eventListener)

        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)

    }

    private fun checkForConversion(){
        if(list.size != 0){
            checkForConversionRemotely(preferenceManager.getString(Constants.KEY_USER_ID)!!, receiverUser.id )
            checkForConversionRemotely(receiverUser.id, preferenceManager.getString(Constants.KEY_USER_ID)!!)
        }
    }

    private fun addConversion(conversion: HashMap<String, Any>){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .add(conversion)
            .addOnSuccessListener { conversionId = it.id }
    }

    private fun updateConversion(message: String){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId!!)
            .update(Constants.KEY_LAST_MASSAGE, message, Constants.KEY_TIMESTAMP, Date())
    }

    private fun checkForConversionRemotely(senderID: String, receiverId: String){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, senderID)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
            .get()
            .addOnCompleteListener(conversionOnCompleteListener)
    }

    fun onSendMessageClick() {
        if (inputMessage.value == "" || inputMessage.value == null) {
            showToast("Enter message")
            return
        }
        senMessage()
    }

    private fun senMessage() {
        val message = HashMap<String, Any>()
        message[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_USER_ID)!!
        message[Constants.KEY_RECEIVER_ID] = receiverUser.id
        message[Constants.KEY_MESSAGE] = inputMessage.value!!
        message[Constants.KEY_TIMESTAMP] = Date()
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)

        if(conversionId != null){
            updateConversion(inputMessage.value!!)
        }else{
            val conversion = HashMap<String, Any>()
            conversion[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_USER_ID)!!
            conversion[Constants.KEY_SENDER_NAME] = preferenceManager.getString(Constants.KEY_NAME)!!
            conversion[Constants.KEY_SENDER_IMAGE] = preferenceManager.getString(Constants.KEY_IMAGE)!!
            conversion[Constants.KEY_RECEIVER_ID] = receiverUser.id
            conversion[Constants.KEY_RECEIVER_NAME] = receiverUser.name
            conversion[Constants.KEY_RECEIVER_IMAGE] = receiverUser.image
            conversion[Constants.KEY_LAST_MASSAGE] = inputMessage.value!!
            conversion[Constants.KEY_TIMESTAMP] = Date()
            addConversion(conversion)
        }

        inputMessage.value = ""
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getReadableDateTime(date: Date): String = SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)

}