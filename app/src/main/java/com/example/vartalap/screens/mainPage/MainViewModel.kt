package com.example.vartalap.screens.mainPage

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vartalap.models.ChatMessage
import com.example.vartalap.utils.Constants
import com.example.vartalap.utils.PreferenceManager
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import javax.xml.transform.dom.DOMLocator
import kotlin.collections.ArrayList

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceManager = PreferenceManager(application)
    private val database = FirebaseFirestore.getInstance()

    //properties
    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _encodedImage = MutableLiveData<String>()
    val encodedImage: LiveData<String>
        get() = _encodedImage

    private val _conversions = MutableLiveData<List<ChatMessage>>()
    val conversions: LiveData<List<ChatMessage>>
        get() = _conversions


    //utils
    private val _isSignedOut = MutableLiveData<Boolean>()
    val isSignedOut: LiveData<Boolean>
        get() = _isSignedOut

    private val _visibleScreen = MutableLiveData<String>()
    val visibleScreen: LiveData<String>
        get() = _visibleScreen

    private val conversionList: MutableList<ChatMessage> = ArrayList()

    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) return@EventListener

        if (value != null) {
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {

                    var chatMessage: ChatMessage
                    if (preferenceManager.getString(Constants.KEY_USER_ID)!! == documentChange.document.getString(Constants.KEY_SENDER_ID)) {
                        chatMessage = ChatMessage(
                            documentChange.document.getString(Constants.KEY_SENDER_ID)?:"",
                            documentChange.document.getString(Constants.KEY_RECEIVER_ID) ?: "",
                            documentChange.document.getString(Constants.KEY_LAST_MASSAGE) ?: "",
                            "",
                            documentChange.document.getDate(Constants.KEY_TIMESTAMP) ?: Date(),
                            documentChange.document.getString(Constants.KEY_RECEIVER_ID)?:"",
                            documentChange.document.getString(Constants.KEY_RECEIVER_NAME)?:"",
                            documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE)?:""
                        )
                    }else{
                        chatMessage = ChatMessage(
                            documentChange.document.getString(Constants.KEY_SENDER_ID)?:"",
                            documentChange.document.getString(Constants.KEY_RECEIVER_ID) ?: "",
                            documentChange.document.getString(Constants.KEY_LAST_MASSAGE) ?: "",
                            "",
                            documentChange.document.getDate(Constants.KEY_TIMESTAMP) ?: Date(),
                            documentChange.document.getString(Constants.KEY_SENDER_ID)?:"",
                            documentChange.document.getString(Constants.KEY_SENDER_NAME)?:"",
                            documentChange.document.getString(Constants.KEY_SENDER_IMAGE)?:""
                        )
                    }
                    conversionList.add(chatMessage)
                }
                else if(documentChange.type == DocumentChange.Type.MODIFIED){
                    for (conversion in conversionList){
                        val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        val receiverId = documentChange.document.getString( Constants.KEY_RECEIVER_ID)
                        if(conversion.senderId == senderId && conversion.receiverId == receiverId){
                            conversionList[conversionList.indexOf(conversion)].message = documentChange.document.getString(Constants.KEY_LAST_MASSAGE)?:""
                            conversionList[conversionList.indexOf(conversion)].date = documentChange.document.getDate(Constants.KEY_TIMESTAMP)?: Date()
                            break
                        }
                    }
                }
            }
            conversionList.sortWith(compareByDescending{ it.date})
            _visibleScreen.value = Constants.SCREEN_MAIN_CONTENT
            _conversions.value = conversionList
        }
    }

    init {
        _visibleScreen.value = Constants.SCREEN_LOADING
        _name.value = preferenceManager.getString(Constants.KEY_NAME)
        _encodedImage.value = preferenceManager.getString(Constants.KEY_IMAGE)

        getToken()
        listenConversion()
    }

    private fun listenConversion(){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)

        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener(this::updateToken)
    }

    private fun updateToken(token: String) {
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID)!!)
            .update(Constants.KEY_FCM_TOKEN, token)
            .addOnFailureListener { showToast("Unable to update token") }
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    fun signOut() {
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID)!!)
            .update(Constants.KEY_FCM_TOKEN, FieldValue.delete())
            .addOnSuccessListener {
                showToast("Signing out...")
                preferenceManager.clear()
                _isSignedOut.value = true
            }
            .addOnFailureListener { showToast("Unable to sign out") }
    }

}