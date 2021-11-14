package com.example.vartalap.screens.mainPage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vartalap.R
import com.example.vartalap.adapters.ConversionClickListener
import com.example.vartalap.adapters.RecentConversationsAdapter
import com.example.vartalap.databinding.FragmentMainBinding
import com.example.vartalap.models.User

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setRecyclerView()
        setListeners()
        observeViewModel()

        return binding.root
    }

    private fun observeViewModel(){
        viewModel.isSignedOut.observe(viewLifecycleOwner){
            if (it){
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToSignInFragment())
            }
        }
    }

    private fun setListeners(){
        binding.fabNewChat.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToUsersFragment())
        }
    }

    private fun setRecyclerView(){
        val conversionAdapter = RecentConversationsAdapter(ConversionClickListener { chatMessage ->
            val user = User(chatMessage.conversionId, chatMessage.conversionName, "", chatMessage.conversionImage, "")
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToChatFragment(user))
        })
        binding.conversionRecyclerView.adapter = conversionAdapter
    }

}