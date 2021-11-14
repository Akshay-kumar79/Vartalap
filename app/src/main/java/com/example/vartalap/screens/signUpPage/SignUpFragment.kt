package com.example.vartalap.screens.signUpPage

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.vartalap.R
import com.example.vartalap.databinding.FragmentSignUpBinding
import com.example.vartalap.screens.signInPage.SignInFragmentDirections
import java.io.FileNotFoundException

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: SignUpViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setListeners()
        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.isSignedUp.observe(viewLifecycleOwner) { isSignedUp ->

            if (isSignedUp) {
                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToMainFragment())
            }
        }
    }

    private fun setListeners(){
        binding.signIn.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())
        }
        binding.imagePicker.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if(result.resultCode == RESULT_OK){
            if(result.data != null){
                val imageUri = result.data!!.data!!
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.profileImage.setImageBitmap(bitmap)
                    viewModel.encodedImage = viewModel.encodeImage(bitmap)

                }catch (e: FileNotFoundException){
                    e.printStackTrace()
                }
            }
        }

    }

}