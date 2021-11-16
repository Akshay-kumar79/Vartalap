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
import android.view.animation.OvershootInterpolator

import android.view.animation.ScaleAnimation




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

        binding.testButton.setOnClickListener {
            val scale = ScaleAnimation(0F, 1F, 0F, 1F, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f)
            scale.duration = 300
            scale.interpolator = OvershootInterpolator()
            it.startAnimation(scale)
        }
    }

    private fun setRecyclerView() {
        val usersAdapter = UsersAdapter(UserClickListener{
            findNavController().navigate(UsersFragmentDirections.actionUsersFragmentToChatFragment(it))
        })
        binding.usersRecyclerView.adapter = usersAdapter
    }
}