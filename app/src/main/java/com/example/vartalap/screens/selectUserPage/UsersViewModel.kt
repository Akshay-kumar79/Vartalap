package com.example.vartalap.screens.selectUserPage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vartalap.models.User
import com.example.vartalap.utils.Constants
import com.example.vartalap.utils.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore

class UsersViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceManager = PreferenceManager(application)

    //properties
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
    get() = _users


    //utils
    private val _visibleScreen = MutableLiveData<String>()
    val visibleScreen: LiveData<String>
        get() = _visibleScreen

    init {
        _visibleScreen.value = Constants.SCREEN_LOADING
        getUsers()
    }

    private fun getUsers() {
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS).get()
            .addOnCompleteListener { task ->

                val currentUsers = preferenceManager.getString(Constants.KEY_USER_ID)

                if (task.isSuccessful && task.result != null) {
                    val users: MutableList<User> = ArrayList()
                    for (queryDocumentSnapshot in task.result!!) {
                        if (queryDocumentSnapshot.id == currentUsers)
                            continue
                        val user = User(
                            queryDocumentSnapshot.id,
                            queryDocumentSnapshot.getString(Constants.KEY_NAME)!!,
                            queryDocumentSnapshot.getString(Constants.KEY_EMAIL)!!,
                            queryDocumentSnapshot.getString(Constants.KEY_IMAGE)!!,
                            queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN)?:""
                        )
                        users.add(user)
                    }
                    if (users.size > 0) {
                        _visibleScreen.value = Constants.SCREEN_MAIN_CONTENT
                        this._users.value = users
                    }else{
                        _visibleScreen.value = Constants.SCREEN_ERROR
                    }
                }else{
                    _visibleScreen.value = Constants.SCREEN_ERROR
                }
            }
    }

}