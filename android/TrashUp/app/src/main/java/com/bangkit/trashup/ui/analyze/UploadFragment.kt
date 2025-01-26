package com.bangkit.trashup.ui.analyze

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bangkit.trashup.R
import com.bangkit.trashup.data.remote.request.Content
import com.bangkit.trashup.data.remote.request.Part
import com.bangkit.trashup.data.remote.request.RequestBody
import com.bangkit.trashup.data.remote.retrofit.RetrofitClient
import com.bangkit.trashup.data.remote.retrofit.VertexAIService
import com.bangkit.trashup.databinding.FragmentUploadBinding
import com.bangkit.trashup.helper.ImageClassifierHelper
import com.bangkit.trashup.utils.getImageUri
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadFragment : Fragment(R.layout.fragment_upload) {

    private lateinit var binding: FragmentUploadBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private val imageUriViewModel: ImageUriViewModel by viewModels()

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            imageUriViewModel.currentImageUri = uri
            showImage(uri)
        } ?: Log.d("Photo Picker", "No media selected")
    }

    private val launcherCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                tempImageUri?.let { uri ->
                    imageUriViewModel.currentImageUri = uri
                    showImage(uri)
                }
            } else {
                imageUriViewModel.currentImageUri?.let { uri ->
                    showImage(uri)
                } ?: run {
                    Toast.makeText(requireContext(), "No Picture Taken", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private val requestGalleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startGallery() else showToast("Permission denied")
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) startCamera() else showToast("Permission denied")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadBinding.inflate(inflater, container, false)

        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    showToast(error)
                }

                override fun onResults(detectedLabel: String, probability: Float) {
                    showProgressFor10Seconds()
                    fetchGeneratedContent(detectedLabel, probability)
                }
            }
        )

        imageUriViewModel.currentImageUri?.let { uri -> showImage(uri) }

        binding.apply {
            binding.buttonAnalyze.setOnClickListener {
                imageUriViewModel.currentImageUri?.let {
                    showProgressFor10Seconds()
                    analyzeImage(it)
                } ?: run {
                    showToast("Foto atau unggah gambar terlebih dahulu")
                }
            }

            btnGallery.setOnClickListener {
                if (allGalleryPermissionsGranted()) {
                    startGallery()
                } else {
                    requestGalleryPermission()
                }
            }
            btnCamera.setOnClickListener {
                if (allCameraPermissionsGranted()) {
                    startCamera()
                } else {
                    requestCameraPermission()
                }
            }
        }

        return binding.root
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        val uri = getImageUri(requireContext())
        tempImageUri = uri
        launcherCamera.launch(uri)
    }
    private var tempImageUri: Uri? = null

    private fun showImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .override(binding.ivUploadPhoto.width, binding.ivUploadPhoto.height)
            .into(binding.ivUploadPhoto)
    }


    private fun analyzeImage(uri: Uri) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                imageClassifierHelper.classifyStaticImage(uri)
            } catch (e: Exception) {
                showToast("Error analyzing image: ${e.message}")
                Log.e("UploadActivity", "Error analyzing image", e)
            } finally {
            }
        }
    }

    private fun fetchGeneratedContent(detectedLabel: String, probability: Float) {
        if (probability < 0.75f) {
            // Tampilkan pesan pada UI jika probabilitas terlalu rendah
            showToast("Pilih gambar sampah yang lebih jelas dan jernih.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val retrofit = RetrofitClient.create(requireContext())
                val service = retrofit.create(VertexAIService::class.java)

                val requestBody = RequestBody(
                    contents = listOf(
                        Content(
                            role = "user",
                            parts = listOf(Part(text = "Buatkan kerajinan dari kategori sampah $detectedLabel."))
                        )
                    )
                )

                val response = service.generateContent(requestBody)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val generatedContent = response.body()?.joinToString("\n") { bodyElement ->
                            bodyElement.candidates.joinToString("\n") { candidate ->
                                candidate.content.parts.joinToString(" ") { part -> part.text }
                            }
                        } ?: "No content generated"
                        moveToResult(imageUriViewModel.currentImageUri, generatedContent)
                    } else {
                        val errorMessage = "Failed to generate content: ${response.code()} ${response.message()}"
                        showToast(errorMessage)
                        Log.e("UploadFragment", errorMessage)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMessage = "Error generating content: ${e.message}"
                    showToast(errorMessage)
                    Log.e("UploadFragment", errorMessage, e)
                }
            }
        }
    }


    private fun moveToResult(imageUri: Uri?, resultText: String) {
        val processedText = processText(resultText)

        hideProgressIndicator()
        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_URI, imageUri.toString())
            putExtra(ResultActivity.EXTRA_RESULT, processedText)
        }
        startActivity(intent)
    }

    private fun processText(text: String): String {
        return text.replace("**", "<b>").replace("**", "</b>")
            .replace("*", "<i>").replace("*", "</i>")
    }

    private fun requestGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        requestGalleryPermissionLauncher.launch(permission)
    }

    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun allGalleryPermissionsGranted(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun allCameraPermissionsGranted(): Boolean {
        val permissions = mutableListOf(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun showProgressIndicator() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressIndicator() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressFor10Seconds() {
        showProgressIndicator()
        CoroutineScope(Dispatchers.Main).launch {
            kotlinx.coroutines.delay(15000L)
            hideProgressIndicator()
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
