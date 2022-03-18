package com.explorer.ui.main.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.explorer.R
import com.explorer.databinding.HomeFragmentBinding

class HomeFragment : Fragment(R.layout.home_fragment) {

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.Factory()
    }

    private lateinit var binding: HomeFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = HomeFragmentBinding.bind(view)
        binding.viewModel = viewModel
    }
}