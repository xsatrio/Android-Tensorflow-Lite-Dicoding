package com.dicoding.asclepius.view.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.FragmentHomeBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.result.ResultActivity
import com.dicoding.asclepius.view.result.ResultActivity.Companion.EXTRA_DATE
import com.dicoding.asclepius.view.result.ResultActivity.Companion.EXTRA_IMAGE_URI
import com.dicoding.asclepius.view.result.ResultActivity.Companion.EXTRA_INFERENCE
import com.dicoding.asclepius.view.result.ResultActivity.Companion.EXTRA_RESULT
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.gms.vision.classifier.Classifications
import java.io.File

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)

        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = null
        )

        viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                currentImageUri = uri
                showImage()
            }
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
            } ?: run {
                Snackbar.make(binding.root, R.string.empty_image_warning, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            moveToCrop(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun moveToCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        val options = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(90)
        }

        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1080, 1080)
            .withOptions(options)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                viewModel.setImageUri(resultUri)
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            cropError?.let {
                Snackbar.make(binding.root, it.message  ?: "Crop Error", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        viewModel.getImageUri()?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(uri: Uri) {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    Snackbar.make(binding.root, "Error : $error" , Snackbar.LENGTH_SHORT).show()
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long, date: String) {
                    val resultText = results?.joinToString("\n") { classification ->
                        classification.categories.joinToString(" , ") {
                            "${it.label}${"%.2f".format(it.score * 100)}%"
                        }
                    }
                    moveToResult(uri, resultText ?: "No results", inferenceTime, date)
                }
            }
        )
        imageClassifierHelper.classifyStaticImage(uri)
    }

    private fun moveToResult(uri: Uri, resultText: String, inferenceTime: Long, date: String) {
        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra(EXTRA_IMAGE_URI, uri.toString())
            putExtra(EXTRA_RESULT, resultText)
            putExtra(EXTRA_INFERENCE, inferenceTime)
            putExtra(EXTRA_DATE, date)
        }
        startActivity(intent)
    }

//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
