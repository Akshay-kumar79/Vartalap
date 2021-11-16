package com.example.vartalap.screens.chatPage

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vartalap.models.ChatMessage
import com.example.vartalap.models.User
import com.example.vartalap.utils.ApiClient
import com.example.vartalap.utils.Constants
import com.example.vartalap.utils.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
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

    private val _receiverImage = MutableLiveData<Bitmap>()
    val receiverImage: LiveData<Bitmap>
    get() = _receiverImage

    //utils
    private val _visibleScreen = MutableLiveData<String>()
    val visibleScreen: LiveData<String>
    get() = _visibleScreen

    private val _isReceiverAvailable = MutableLiveData<Boolean>()
    val isReceiverAvailable: LiveData<Boolean>
    get() = _isReceiverAvailable

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
        _isReceiverAvailable.value = false
        inputMessage.value = ""
        _name.value = receiverUser.name
        listenMessages()
        listenReceiverAvailability()
    }

    private fun listenReceiverAvailability(){
        database.collection(Constants.KEY_COLLECTION_USERS).document(receiverUser.id)
            .addSnapshotListener{ value, error ->
                if(error != null) return@addSnapshotListener
                if(value != null){
                    _isReceiverAvailable.value = value.getBoolean(Constants.KEY_AVAILABILITY)
                    receiverUser.token = value.getString(Constants.KEY_FCM_TOKEN)!!
                    if (receiverUser.image == ""){
                        receiverUser.image = value.getString(Constants.KEY_IMAGE)!!
                    }
                    _receiverImage.value = getBitmapFromEncodedImage(receiverUser.image)
                }
            }
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

        if (!isReceiverAvailable.value!!){
            try {
                val tokens = JSONArray()
                tokens.put(receiverUser.token)

                val data = JSONObject()
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME))
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN))
                data.put(Constants.KEY_MESSAGE, inputMessage.value)

                val body = JSONObject()
                body.put(Constants.REMOTE_MSG_DATA, data)
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

                sendNotification(body.toString())
            }catch (e: Exception){
                e.message?.let { showToast(it) }
            }
        }

        inputMessage.value = ""
    }

    private fun sendNotification(messageBody: String){
        ApiClient.retrofitService.sendMessage(Constants.getRemoteMsgHeaders(), messageBody).enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful){
                    try {
                        if (response.body() != null){
                            val responseJson = JSONObject(response.body()!!)
                            val results = responseJson.getJSONArray("results")
                            if (responseJson.getInt("failure") == 1){
                                val error = results.get(0) as JSONObject
                                showToast(error.getString("error"))
                                return
                            }
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }
                    showToast("Notification sent successfully")
                }else{
                    showToast("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                t.message?.let { showToast(it) }
            }

        })
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getReadableDateTime(date: Date): String = SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)

    private fun getBitmapFromEncodedImage(image: String): Bitmap {
        val bytes = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}