package com.example.vartalap.utils

object Constants {

    const val KEY_COLLECTION_USERS = "users"
    const val KEY_NAME = "name"
    const val KEY_EMAIL = "email"
    const val KEY_PASSWORD = "password"
    const val KEY_PREFERENCE_NAME = "chatAppPreference"
    const val KEY_IS_SIGNED_IN = "isSignedIn"
    const val KEY_USER_ID = "userId"
    const val KEY_IMAGE = "image"
    const val KEY_FCM_TOKEN = "fcmToken"
    const val KEY_AVAILABILITY = "availability"
    const val REMOTE_MSG_AUTHORIZATION = "Authorization"
    const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
    const val REMOTE_MSG_DATA = "data"
    const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"

    //chat
    const val KEY_COLLECTION_CHAT = "chat"
    const val KEY_SENDER_ID = "senderId"
    const val KEY_RECEIVER_ID = "receiverID"
    const val KEY_MESSAGE = "message"
    const val KEY_TIMESTAMP = "timeStamp"


    const val MODE_SET_DATA = "mode_set_data"
    const val MODE_INSERT_DATA = "mode_insert_data"

    //conversions
    const val KEY_COLLECTION_CONVERSATIONS = "conversations"
    const val KEY_SENDER_NAME = "sender_name"
    const val KEY_RECEIVER_NAME = "receiver_name"
    const val KEY_SENDER_IMAGE = "sender_image"
    const val KEY_RECEIVER_IMAGE = "receiver_image"
    const val KEY_LAST_MASSAGE = "last_message"

    //select user screen
    const val SCREEN_LOADING = "screen_loading"
    const val SCREEN_MAIN_CONTENT = "screen_main_content"
    const val SCREEN_ERROR = "screen_error"

    private var remoteMsgHeaders: HashMap<String, String>? = null

    fun getRemoteMsgHeaders(): HashMap<String, String> {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = HashMap()
            remoteMsgHeaders!!.put(
                REMOTE_MSG_AUTHORIZATION,
                "key=AAAAnUh0DBE:APA91bH1JRGBuUlrl9CkHS-OMuP7pk-P3DTNF9BKi7HaWo60TBFg9q99PRDjTq63migpGboY4K64kQtc_W7mJ8DtH2_6kd6Pnyn2VCEs0NYIbQ1ujYdLOypsA7F5_C7mDoYpvrWMR-Xr"
            )
            remoteMsgHeaders!!.put(
                REMOTE_MSG_CONTENT_TYPE,
                "application/json"
            )
        }
        return remoteMsgHeaders!!
    }

}