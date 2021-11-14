package com.example.vartalap.screens.selectUserPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vartalap.adapters.UserClickListener
import com.example.vartalap.adapters.UsersAdapter
import com.example.vartalap.databinding.FragmentUsersBinding

class UsersFragment : Fragment() {

    private lateinit var viewModel: UsersViewModel
    private lateinit var binding: FragmentUsersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[UsersViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setRecyclerView()
        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun setRecyclerView() {
        val usersAdapter = UsersAdapter(UserClickListener{
            findNavController().navigate(UsersFragmentDirections.actionUsersFragmentToChatFragment(it))
        })
        binding.usersRecyclerView.adapter = usersAdapter
    }
}