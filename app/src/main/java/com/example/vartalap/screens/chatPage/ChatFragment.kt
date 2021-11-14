package com.example.vartalap.screens.chatPage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vartalap.adapters.ChatAdapter
import com.example.vartalap.databinding.FragmentChatBinding
import com.example.vartalap.utils.Constants
import com.example.vartalap.utils.PreferenceManager

class ChatFragment : Fragment() {

    private val args by navArgs<ChatFragmentArgs>()

    private lateinit var viewModelFactory: ChatViewModelFactory
    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        viewModelFactory = ChatViewModelFactory(args.user, requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory)[ChatViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setRecyclerView()
        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setRecyclerView(){
        val chatAdapter = ChatAdapter(PreferenceManager(requireContext()).getString(Constants.KEY_USER_ID)!!, getBitmapFromEncodedImage(args.user.image))
        binding.recyclerView.adapter = chatAdapter
    }

    private fun getBitmapFromEncodedImage(image: String): Bitmap {
        val bytes = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}