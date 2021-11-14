package com.example.vartalap.screens.signInPage

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.vartalap.R
import com.example.vartalap.databinding.FragmentSignInBinding
import com.example.vartalap.utils.Constants
import com.example.vartalap.utils.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.HashMap

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private lateinit var viewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SignInViewModel::class.java]

        if(PreferenceManager(requireContext()).getBoolean(Constants.KEY_IS_SIGNED_IN)){
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToMainFragment())
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setListeners()
        observeViewModel()

        return binding.root
    }


    private fun observeViewModel() {
        viewModel.isSignedIn.observe(viewLifecycleOwner) { isSignedUp ->

            if (isSignedUp) {
                findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToMainFragment())
            }
        }
    }

    private fun setListeners(){
        binding.createNewAccount.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }
    }


}