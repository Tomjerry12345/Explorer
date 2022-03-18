package com.explorer.ui.examples.widget

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.explorer.R
import com.explorer.databinding.ExamplesApiFragmentBinding
import com.explorer.databinding.ExamplesWidgetFragmentBinding

class ExamplesWidgetFragment : Fragment(R.layout.examples_widget_fragment) {

    private val viewModel: ExamplesWidgetViewModel by viewModels {
        ExamplesWidgetViewModel.Factory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = ExamplesWidgetFragmentBinding.bind(view)
        binding.viewModel = viewModel
    }

}