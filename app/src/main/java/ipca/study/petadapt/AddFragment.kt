package ipca.study.petadapt

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import ipca.study.petadapt.databinding.FragmentAddBinding
import java.util.UUID

class AddFragment : Fragment() {
    // Listener for bottom navigation
    private var bottomNavigationListener: BottomNavigationListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomNavigationListener) {
            bottomNavigationListener = context
        }
    }

    // Binding fragment
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    // Firebase authentication
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var backbtn: ImageButton

    // List to store selected image URIs
    private var imageUris: ArrayList<Uri> = ArrayList()
    private lateinit var adapterImagePick: AdapterImagePick

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val view = binding.root

        // Move this part after inflating the binding
        adapterImagePick = AdapterImagePick(requireContext(), imageUris)
        binding.addImagesRv.adapter = adapterImagePick
        loadImages()

        binding.nextButton.setOnClickListener {
            savePetData(view)
        }

        backbtn = binding.toolbarBackButton
        backbtn.setOnClickListener {
            val intent = Intent(requireContext(), Dashboard::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.clicktoaddphotos.setOnClickListener {
            showImagePickOptions()
        }



        return view
    }

    // Function to show options for image selection
    private fun showImagePickOptions() {
        val popupMenu = PopupMenu(requireContext(), binding.clicktoaddphotos)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")

        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId
            if (itemId == 1) {
                val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
                requestCameraPermission.launch(cameraPermissions)
            } else if (itemId == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pickImageGallery()
                } else {
                    val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                    requestStoragePermission.launch(storagePermission)
                }
            }
            true
        }
    }

    // Request permission to access storage
    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageGallery()
        } else {
            Toast.makeText(requireContext(), "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    // Request permissions for camera
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        var areAllGranted = true
        for (isGranted in result.values) {
            areAllGranted = areAllGranted && isGranted
        }
        if (areAllGranted) {
            pickImageCamera()
        } else {
            Toast.makeText(
                requireContext(),
                "Camera or storage or both permission denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Function to pick image from gallery
    private fun pickImageGallery() {
        Log.d(TAG, "pickImageGallery")

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    // Handle the result of the gallery image selection
    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val imageUri = data.data
                Log.d(TAG, "Selected image URI: $imageUri")
                imageUris.add(imageUri!!)
                loadImages()
            } else {
                Log.e(TAG, "Error: Data is null.")
                Toast.makeText(requireContext(), "Error getting selected file", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(TAG, "Image selection cancelled")
            Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to pick image using the camera

    private fun pickImageCamera() {
        Log.d(TAG, "pickImageCamera")

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "IMAGE_TITLE")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "IMAGE_DESCRIPTION")

        val imageUri =
            requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        imageUri?.let {
            imageUris.add(it)
            loadImages()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, it)
            cameraActivityResultLauncher.launch(intent)
        }
    }


    // Handle the result of the camera image capture
    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Do nothing here, as we are already handling the image capture in pickImageCamera()
        } else {
            Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


    // Function to load images into the RecyclerView
    private fun loadImages() {
        adapterImagePick.notifyDataSetChanged()
    }

    // Function to save pet data
    private fun savePetData(view: View) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid
        val imageUrls = uploadImagesToStorage()
        val petId = FirebaseDatabase.getInstance().getReference("Pets").child(userID.orEmpty()).push().key ?: ""
        val pet = Pet(
            petId = petId,
            petName = view.findViewById<EditText>(R.id.pet_name).text.toString().trim(),
            petAge = view.findViewById<EditText>(R.id.pet_age).text.toString().trim(),
            petBreed = view.findViewById<EditText>(R.id.pet_breed).text.toString().trim(),
            sexMale = view.findViewById<RadioButton>(R.id.radioButtonMale).isChecked,
            vaccinatedYes = view.findViewById<RadioButton>(R.id.radioButtonVaccinatedYes).isChecked,
            adTitle = view.findViewById<EditText>(R.id.ad_title).text.toString().trim(),
            adDescription = view.findViewById<EditText>(R.id.ad_discription).text.toString().trim(),
            imageUrls = imageUrls,
            userId = userID.orEmpty()
        )

        savePetDetails(userID, pet,petId)
    }


    // Function to upload images to Firebase Storage
    private fun uploadImagesToStorage(): List<String> {
        val imageUrls = mutableListOf<String>()

        for (imageUri in imageUris) {
            // Upload image to Firebase Storage and get download URL
            val imageUrl = uploadImageToStorage(imageUri)
            imageUrls.add(imageUrl)
        }

        return imageUrls
    }

    // Function to upload a single image to Firebase Storage
    private fun uploadImageToStorage(imageUri: Uri): String {
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference

        // Generate a unique name for the image using a timestamp
        val imageName = "image_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
        val imageRef: StorageReference = storageRef.child("images/$imageName")

        // Upload the image to Firebase Storage
        val uploadTask: UploadTask = imageRef.putFile(imageUri)

        try {
            // Block until the upload is complete and get the download URL
            val downloadUrl = Tasks.await(uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            })

            return downloadUrl.toString()
        } catch (e: Exception) {
            // Image upload failed, handle the error
            e.printStackTrace()

            // Here, you might want to return a default URL or throw an exception.
            return "default_image_url"
        }
    }

    // Function to save pet details to Firebase Realtime Database
    private fun savePetDetails(userID: String?, pet: Pet,petId: String) {
        if (userID != null) {
            val database = FirebaseDatabase.getInstance().reference
            database.child("User").child(userID).child("Pets").child(petId).setValue(pet)
                .addOnSuccessListener {
                    // Save global pet details after successful user-specific save
                    saveGlobalPetDetails(userID,pet,petId)
                }
                .addOnFailureListener {
                    showToast("Failed to save pet details. ${it.message}")
                }
        }
    }
    // Function to save pet details to global database
    private fun saveGlobalPetDetails(userID: String?, pet: Pet,petId: String) {
        if (userID != null) {
            val database = FirebaseDatabase.getInstance().reference
            database.child("User").child("Pets").child(petId).setValue(pet)
                .addOnSuccessListener {
                    // Show a confirmation dialog after successful save
                    showConfirmationDialog()
                }
                .addOnFailureListener {
                    showToast("Failed to save pet details. ${it.message}")
                }
        }
    }

    // Function to show confirmation dialog
    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Success")
            .setMessage("Pet details saved successfully.")
            .setPositiveButton("OK") { _, _ ->
                // Navigate back to dashboard after successful save
                val intent = Intent(context, Dashboard::class.java)
                startActivity(intent)
                requireActivity().finish()

            }
            .create()
            .show()
    }

    // Function to get the current timestamp
    private fun getTimestamp(): Long {
        return System.currentTimeMillis()
    }

    // Function to display a toast message (placeholder)
    private fun showToast(message: String) {
        // Display a toast message
    }

    // Cleanup to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface BottomNavigationListener {
        fun showBottomNavigation()
    }
}
