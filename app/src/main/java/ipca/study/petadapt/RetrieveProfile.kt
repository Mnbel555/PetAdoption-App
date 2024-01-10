package ipca.study.petadapt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class RetrieveProfile : AppCompatActivity() {

    // UI components
    private lateinit var profileImg: ImageView
    private lateinit var titleName: TextView
    private lateinit var titleUsername: TextView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileUsername: TextView
    private lateinit var profilePassword: TextView
    private lateinit var updateProfileButton: Button
    private lateinit var chooseImageButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val storageReference = FirebaseStorage.getInstance().reference

    // Activity result launcher for image picking
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val imageUri: Uri? = data.data
                    if (imageUri != null) {
                        uploadImage(imageUri)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrieve_profile)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        profileImg = findViewById(R.id.profileImg)
        titleName = findViewById(R.id.titleName)
        titleUsername = findViewById(R.id.titleUsername)
        profileName = findViewById(R.id.profile_name)
        profileEmail = findViewById(R.id.profile_email)
        profileUsername = findViewById(R.id.profile_username)
        profilePassword = findViewById(R.id.profile_password)
        updateProfileButton = findViewById(R.id.update_profile_button)
        chooseImageButton = findViewById(R.id.choose_image_button)

        // Fetch and display user data
        fetchUserDataFromFirebase()

        updateProfileButton.setOnClickListener {
            val updatedName = profileName.text.toString().trim()
            val updatedUsername = profileUsername.text.toString().trim()


            // Update the user's profile data in Firebase
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userReference = database.child("User").child(currentUser.uid)
                userReference.child("name").setValue(updatedName)
                userReference.child("username").setValue(updatedUsername)

                //update name at same time to display updated result
                titleName.text = updatedName
                titleUsername.text = updatedUsername

                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
        }

        chooseImageButton.setOnClickListener {
            openFileChooser()
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imagePicker.launch(intent)
    }

    //  checks whether an image file with the user's UID already exists.
    private fun uploadImage(imageUri: Uri) {
        Log.d("ImageInfo", "Image URI: $imageUri")

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val storageRef = storageReference.child("profile_images/${currentUser.uid}.jpg")

            storageRef.metadata.addOnSuccessListener { metadata ->
                if (metadata.sizeBytes == 0L) {
                    // If size is 0, it means the file doesn't exist, so upload it
                    uploadNewImage(storageRef, imageUri)
                } else {
                    // If size is not 0, file exists, so update the existing image
                    updateExistingImage(storageRef, imageUri)
                }
            }
        }
    }

    private fun uploadNewImage(storageRef: StorageReference, imageUri: Uri) {
        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val userReference = database.child("User").child(currentUser.uid)
                        userReference.child("imageUrl").setValue(downloadUri.toString())
                        fetchUserDataFromFirebase()
                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateExistingImage(storageRef: StorageReference, imageUri: Uri) {
        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                fetchUserDataFromFirebase()
                Toast.makeText(this, "Image updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchUserDataFromFirebase() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userReference = database.child("User").child(currentUser.uid)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile = snapshot.getValue(Model::class.java)
                        if (userProfile != null) {
                            // Update UI with user profile data
                            updateUI(userProfile)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    Toast.makeText(
                        this@RetrieveProfile,
                        "Error fetching user data: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun updateUI(userProfile: Model) {
        // Update UI with user profile data
        titleName.text = userProfile.name
        titleUsername.text = userProfile.username
        profileName.text = userProfile.name
        profileEmail.text = userProfile.email
        profileUsername.text = userProfile.username
        profilePassword.text = userProfile.password

        val imageUrl = userProfile.imageUrl
        if (imageUrl.isNotEmpty()) {
            // Load the image using Glide
            Glide.with(this).load(imageUrl).into(profileImg)
        }
    }
}
