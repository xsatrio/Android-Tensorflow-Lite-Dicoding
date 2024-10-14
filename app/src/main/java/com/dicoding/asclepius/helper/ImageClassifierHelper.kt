package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import com.dicoding.asclepius.R
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.DataType
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.classifier.Classifications
import org.tensorflow.lite.task.gms.vision.classifier.ImageClassifier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ImageClassifierHelper(
    private var threshold: Float = 0.1f,
    private var maxResults: Int = 1,
    private val modelName: String = "cancer_classification.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            setupImageClassifier()
        }.addOnFailureListener {
            classifierListener?.onError(context.getString(R.string.tflitevision_is_not_initialized_yet))
        }
    }

    private fun setupImageClassifier() {
        // TODO: Menyiapkan Image Classifier untuk memproses gambar.
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()

        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
            baseOptionsBuilder.useGpu()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            baseOptionsBuilder.useNnapi()
        } else {
            baseOptionsBuilder.setNumThreads(4)
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, e.message.toString())
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
        // TODO: mengklasifikasikan imageUri dari gambar statis.
        if (!TfLiteVision.isInitialized()) {
            val errorMessage = context.getString(R.string.tflitevision_is_not_initialized_yet)
            Log.e(TAG, errorMessage)
            classifierListener?.onError(errorMessage)
            return
        }

        if (imageClassifier == null) {
            setupImageClassifier()
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.UINT8))
            .build()

        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }.copy(Bitmap.Config.ARGB_8888, true)

        var tensorImage: TensorImage? = null
        bitmap?.let {
            tensorImage = imageProcessor.process(TensorImage.fromBitmap(it))
        }

        if (tensorImage == null) {
            classifierListener?.onError("Image processing failed")
            return
        }

        var inferenceTime = SystemClock.uptimeMillis()

        var results: List<Classifications>? = null
        tensorImage?.let {
            val imageProcessingOptions = ImageProcessingOptions.builder().build()
            results = imageClassifier?.classify(tensorImage, imageProcessingOptions)
        }

        val date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime

        if (results != null) {
            classifierListener?.onResults(results, inferenceTime, date)
        } else {
            classifierListener?.onError("Classification failed")
        }
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long,
            date:String
        )
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}