package com.example.vartalap.screens.signUpPage

import android.app.Application
import android.graphics.Bitmap
import android.util.Base64
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vartalap.utils.Constants
import com.example.vartalap.utils.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class SignUpViewModel(application: Application) : AndroidViewModel(application) {


    private val preferenceManager = PreferenceManager(application)

    //properties
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()
    var encodedImage: String? = null

    //utils
    private val _isSigningUp = MutableLiveData<Boolean>()
    val isSigningUp: LiveData<Boolean>
        get() = _isSigningUp

    private val _isSignedUp = MutableLiveData<Boolean>()
    val isSignedUp: LiveData<Boolean>
        get() = _isSignedUp


    init {
        name.value = ""
        email.value = ""
        password.value = ""
        confirmPassword.value = ""
    }


    fun onSignUpButtonClick() {
        if (isValidSignUpDetails()) {
            _isSigningUp.value = true
            signUp()
        }
    }

    private fun signUp() {
        val database = FirebaseFirestore.getInstance()
        val user = HashMap<String, Any>()
        user[Constants.KEY_NAME] = name.value!!
        user[Constants.KEY_EMAIL] = email.value!!
        user[Constants.KEY_PASSWORD] = password.value!!
        user[Constants.KEY_IMAGE] = encodedImage!!

        database.collection(Constants.KEY_COLLECTION_USERS).add(user)
            .addOnSuccessListener { documentReference ->
                _isSigningUp.value = false
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                preferenceManager.putString(Constants.KEY_USER_ID, documentReference.id)
                preferenceManager.putString(Constants.KEY_NAME, name.value!!)
                preferenceManager.putString(Constants.KEY_IMAGE, encodedImage!!)
                _isSignedUp.value = true
            }
            .addOnFailureListener {
                _isSigningUp.value = false
                showToast(it.message ?: "sign up failed")
            }
    }

    private fun isValidSignUpDetails(): Boolean {

        if (encodedImage == null) {
            showToast("Select profile image")
            return false
        } else if (name.value!!.trim().isEmpty()) {
            showToast("Enter name")
            return false
        } else if (email.value!!.trim().isEmpty()) {
            showToast("Enter email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()) {
            showToast("Enter valid email")
            return false
        } else if (password.value!!.trim().isEmpty()) {
            showToast("Enter password")
            return false
        } else if (confirmPassword.value!!.trim().isEmpty()) {
            showToast("Confirm your password")
            return false
        } else if (password.value!! != confirmPassword.value!!) {
            showToast("Password & Confirm password must be same")
            return false
        }

        return true

    }

    fun encodeImage(bitmap: Bitmap): String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

}