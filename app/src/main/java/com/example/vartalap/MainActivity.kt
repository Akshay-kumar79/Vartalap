package com.example.vartalap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.vartalap.databinding.ActivityMainBinding
import com.example.vartalap.utils.Constants
import com.example.vartalap.utils.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val database = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if (PreferenceManager(this).getBoolean(Constants.KEY_IS_SIGNED_IN))
            database.collection(Constants.KEY_COLLECTION_USERS).document(PreferenceManager(this).getString(Constants.KEY_USER_ID)!!)
                .update(Constants.KEY_AVAILABILITY, true)
    }

    override fun onPause() {
        super.onPause()
        if (PreferenceManager(this).getBoolean(Constants.KEY_IS_SIGNED_IN))
            database.collection(Constants.KEY_COLLECTION_USERS).document(PreferenceManager(this).getString(Constants.KEY_USER_ID)!!)
                .update(Constants.KEY_AVAILABILITY, false)
    }
}