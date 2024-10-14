package com.dicoding.asclepius.view.result

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.R
import com.dicoding.asclepius.ViewModelFactory
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    private val resultViewModel: ResultViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar?.title = getString(R.string.result)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
        }

        val result = intent.getStringExtra(EXTRA_RESULT)
        result?.let {it ->
            Log.d("Result", "Accuracy : $it")
            "Hasil Klasifikasi : $it".also { binding.resultText.text = it }
        }

        val inference = intent.getLongExtra(EXTRA_INFERENCE, 0L)
        inference.let {it ->
            Log.d("Inference", "Time : $it ms")
            "Waktu Inference : $it ms".also { binding.inferenceTime.text = it }
        }

        val date = intent.getStringExtra(EXTRA_DATE)
        date.let {it ->
            Log.d("Inference", "Time : $it")
            "Dibuat pada : $it".also { binding.date.text = it }
        }

        binding.btnSave.setOnClickListener {
            val historyEntity = HistoryEntity(
                image = imageUri.toString(),
                analysis = result.toString(),
                inference = inference.toString(),
                date = date.toString()
            )

            lifecycleScope.launch {
                val isExists = resultViewModel.isHistoryExists(imageUri.toString())
                if (isExists) {
                    Snackbar.make(binding.root, "Data sudah tersimpan sebelumnya", Snackbar.LENGTH_SHORT).show()
                } else {
                    resultViewModel.insertHistory(listOf(historyEntity))
                    Snackbar.make(binding.root, "Data berhasil disimpan", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_INFERENCE = "extra_inference_time"
        const val EXTRA_DATE = "extra_date"
    }
}