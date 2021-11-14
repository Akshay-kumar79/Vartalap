package com.example.vartalap.screens.signInPage

import android.app.Application
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vartalap.utils.Constants
import com.example.vartalap.utils.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore


class SignInViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceManager = PreferenceManager(application)

    //properties
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    //utils
    private val _isSigningIn = MutableLiveData<Boolean>()
    val isSigningIn: LiveData<Boolean>
        get() = _isSigningIn

    private val _isSignedIn = MutableLiveData<Boolean>()
    val isSignedIn: LiveData<Boolean>
        get() = _isSignedIn

    init {
        email.value = ""
        password.value = ""
    }

    fun onSignInButtonClick() {
        if (isValidSignInDetails()) {
            _isSigningIn.value = true
            signIn()
        }
    }

    private fun signIn() {
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, email.value)
            .whereEqualTo(Constants.KEY_PASSWORD, password.value)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null && task.result!!.documents.size > 0) {
                    val documentSnapshot = task.result!!.documents[0]
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.id)
                    preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME) ?: "")
                    preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE) ?: "")
                    _isSignedIn.value = true
                } else {
                    _isSigningIn.value = false
                    showToast("unable to sign in")
                }
            }
    }

    private fun isValidSignInDetails(): Boolean {

        if (email.value!!.trim().isEmpty()) {
            showToast("Enter email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()) {
            showToast("Enter valid email")
            return false
        } else if (password.value!!.trim().isEmpty()) {
            showToast("Enter password")
            return false
        }

        return true

    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }


}