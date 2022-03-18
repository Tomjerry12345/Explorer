package com.explorer.ui.main.identifkasi

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.explorer.BuildConfig
import com.explorer.R
import com.explorer.databinding.IdentifikasiFragmentBinding
import com.explorer.utils.other.Constant
import com.explorer.utils.other.showLogAssert
import com.explorer.utils.other.uriToBitmap
import com.explorer.utils.system.GetFilesFromStorage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File


class IdentifikasiFragment : Fragment(R.layout.identifikasi_fragment) {

    private val viewModel: IdentifikasiViewModel by viewModels {
        IdentifikasiViewModel.Factory()
    }

    var imageSize = 224

    private lateinit var binding: IdentifikasiFragmentBinding

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                var image = uriToBitmap(requireActivity(), uri)
                val dimension: Int = image.width.coerceAtMost(image.height)
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
                binding.viewImage.setImageBitmap(image)

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                val result = viewModel.classifyImage(requireContext(), image, imageSize)
                showLogAssert("result", result)
                binding.txtResult.text = result
            }
        }
    }

    private var latestTmpUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = IdentifikasiFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val observer = GetFilesFromStorage(requireActivity())

        lifecycle.addObserver(observer)

        binding.mbPick.setOnClickListener {
            MaterialAlertDialogBuilder(view.context)
                .setTitle("Action")
                .setItems(Constant.items_pick) { dialog, which ->
                    showLogAssert("select dialog", "$which")

                    if (which == 0) {
                        takeImage()
                    } else {
                        observer.setFile(Constant.MIME_ALL_IMAGE)
                    }
                }
                .show()
        }

        getFile(observer)

    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png").apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

    private fun getFile(observer: GetFilesFromStorage) {
        observer.succes.observe(viewLifecycleOwner) {
            if (it) {
                val mimeType = observer.getMimeType()

                if (mimeType == "image/jpeg" || mimeType == "image/png" || mimeType == "image/jpg") {
                    val bitmap = observer.getBitmap()
                    binding.viewImage.setImageBitmap(bitmap)

                    val image = bitmap?.let { it1 -> Bitmap.createScaledBitmap(it1, imageSize, imageSize, false) };
                    val result =
                        image?.let { it1 ->
                            viewModel.classifyImage(requireContext(),
                                it1, imageSize)
                        }
                    showLogAssert("result", "$result")
                    binding.txtResult.text = result
                }
            }
        }

    }

}