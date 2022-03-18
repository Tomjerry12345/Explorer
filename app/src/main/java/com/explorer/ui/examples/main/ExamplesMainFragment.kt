package com.explorer.ui.examples.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.explorer.R
import com.explorer.database.firebase.FirebaseDatabase
import com.explorer.databinding.ExamplesMainFragmentBinding

class ExamplesMainFragment : Fragment(R.layout.examples_main_fragment) {

    private val viewModel: ExamplesMainViewModel by viewModels {
        ExamplesMainViewModel.Factory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = ExamplesMainFragmentBinding.bind(view)

        binding.viewModel = viewModel

    }

}