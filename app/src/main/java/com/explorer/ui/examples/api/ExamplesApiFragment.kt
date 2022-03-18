package com.explorer.ui.examples.api

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.explorer.R
import com.explorer.databinding.ExamplesApiFragmentBinding
import com.explorer.model.ExamplesApiModel
import com.explorer.repository.ExamplesApiRepository
import com.explorer.utils.network.Response
import com.explorer.utils.other.showLogAssert
import com.explorer.utils.other.showToast

class ExamplesApiFragment : Fragment(R.layout.examples_api_fragment) {

    private val viewModel: ExamplesApiViewModel by viewModels {
        ExamplesApiViewModel.Factory(ExamplesApiRepository())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = ExamplesApiFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.getData().observe(viewLifecycleOwner) { it ->
            when (it) {
                is Response.Changed -> {
                    showLogAssert("success", "${it.data}")
                    val response = it.data as ExamplesApiModel
                    val list: ArrayList<Map<String, Any>> =
                        response.response as ArrayList<Map<String, Any>>
                    showLogAssert("success 1", "${list[0]["nama"]}")
                }
                is Response.Error -> {
                    showLogAssert("error", it.error)
                    showToast(requireContext(), it.error)
                }
                is Response.Success -> TODO()
            }
        }
    }

}