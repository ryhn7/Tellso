package com.example.tellso.ui.create

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.example.tellso.R
import com.example.tellso.databinding.FragmentCreateStoryBinding
import com.example.tellso.utils.animateVisibility
import com.example.tellso.utils.reduceFileImage
import com.example.tellso.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@ExperimentalPagingApi
@AndroidEntryPoint
class CreateStoryFragment : Fragment() {

    private var _binding: FragmentCreateStoryBinding? = null
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var getFile: File? = null
    private var location: Location? = null
    private var token: String = ""

    private val viewModel: CreateStoryViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val file = File(currentPhotoPath).also { getFile = it }
            val os: OutputStream

            // Rotate image to correct orientation
            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val exif = ExifInterface(currentPhotoPath)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            val rotatedBitmap: Bitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(bitmap, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(bitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(bitmap, 270)
                ExifInterface.ORIENTATION_NORMAL -> bitmap
                else -> bitmap
            }

            // Convert rotated image to file
            try {
                os = FileOutputStream(file)
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()

                getFile = file
            } catch (e: Exception) {
                e.printStackTrace()
            }

            binding.imageView.setImageBitmap(rotatedBitmap)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            uriToFile(selectedImg, requireContext()).also { getFile = it }

            binding.imageView.setImageURI(selectedImg)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                }
            }
        }

        binding.cameraButton.setOnClickListener { startIntentCamera() }
        binding.galerryButton.setOnClickListener { startIntentGallery() }
        binding.postButton.setOnClickListener { uploadStory() }
        binding.switchAddLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getLocation()
            } else {
                location = null
            }
        }
    }

    private fun uploadStory() {
        setLoadingState(true)

        val etDescription = binding.etDescription
        var isValid = true

        // Check if description is empty
        if (etDescription.text.toString().isBlank()) {
            etDescription.error = getString(R.string.empty_desc_error)
            isValid = false
        }

        // Check if image is empty
        if (getFile == null) {
            showSnackbar(getString(R.string.empty_img_error))
            isValid = false
        }

        // Upload story if all fields are valid
        if (isValid) {
            val file = reduceFileImage(getFile as File)
            val description =
                etDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val reqImgFile = file.asRequestBody("image/*".toMediaType())
            val imgMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                reqImgFile
            )

            var lat: RequestBody? = null
            var lon: RequestBody? = null

            if (location != null) {
                lat =
                    location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                lon =
                    location?.longitude.toString().toRequestBody("text/plain".toMediaType())
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.uploadStory(token, imgMultipart, description, lat, lon)
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect { response ->
                        response.onSuccess {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.post_success),
                                Toast.LENGTH_SHORT
                            ).show()

                            findNavController().popBackStack(R.id.navigation_home, false)
                            setLoadingState(false)
                        }

                        response.onFailure {
                            setLoadingState(false)
                            showSnackbar(getString(R.string.post_failed))
                        }
                    }
            }
        } else {
            setLoadingState(false)
        }
    }

    private fun startIntentGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startIntentCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireContext().packageManager)

        com.example.tellso.utils.createTempFile(requireContext().applicationContext).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.tellso",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Location permission granted
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this.location = location
                    Log.d(TAG, "getLocation: ${location.latitude}, ${location.longitude}")
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.activate_location_message),
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.switchAddLocation.isChecked = false
                }
            }
        } else {
            // Location permission denied
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.d(TAG, "$permissions")
        when {
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getLocation()
            }
            else -> {
                Snackbar
                    .make(
                        binding.root,
                        getString(R.string.location_permission_denied),
                        Snackbar.LENGTH_SHORT
                    )
                    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    .setAction(getString(R.string.denied_action)) {

                        // When user not grant permission, user need to activate the permission manually
                        // Direct user to the application detail setting
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                            val uri = Uri.fromParts("package", requireContext().packageName, null)
                            intent.data = uri

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    .show()

                binding.switchAddLocation.isChecked = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            cameraButton.isEnabled = !isLoading
            galerryButton.isEnabled = !isLoading
            etDescription.isEnabled = !isLoading

            viewLoading.animateVisibility(isLoading)
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}