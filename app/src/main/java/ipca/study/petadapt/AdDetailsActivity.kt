package ipca.study.petadapt

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ipca.study.petadapt.databinding.ActivityAdDetailsBinding

class AdDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdDetailsBinding
    // Reference to the Firebase Realtime Database
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("pet")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using View Binding
        binding = ActivityAdDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the Pet object from the intent
        val pets = intent.getParcelableExtra<Pet>("pets")
        Log.d(TAG, "Pets: ${pets}")

        // Update the UI with the pet details
        updateUI(pets)
    }

    private fun updateUI(petDetailsModel: Pet?) {
        if (petDetailsModel != null) {
            binding.apply {

                // Set various details of the pet to the corresponding TextViews
                nameDetail.text = petDetailsModel.petName
                titleDetail.text = petDetailsModel.adTitle
                discriptionDetail.text = petDetailsModel.adDescription
                breedDetail.text = petDetailsModel.petBreed
                ageDetail.text = petDetailsModel.petAge
                genderDetail.text = if (petDetailsModel.sexMale == true) "Male" else "Female"
                vaccineDetail.text = if (petDetailsModel.vaccinatedYes == true) "Yes" else "No"

                // list of ModelImageSlider objects from the image URLs
                val modelImageSliderList =
                    petDetailsModel.imageUrls?.mapIndexed { index, imageUrl ->
                        ModelImageSlider(id = index.toString(), imageUrl = imageUrl)
                    } ?: emptyList()

                // AdapterImageSlider with the list of images
                val adapterImageSlider =
                    AdapterImageSlider(this@AdDetailsActivity, ArrayList(modelImageSliderList))
                imageSlider.adapter = adapterImageSlider

                // click listeners for toolbar buttons based on user ownership
                toolbarDeleteDetail.setOnClickListener {
                    showDeleteConfirmationDialog(petDetailsModel)
                }

                val currentUserID = FirebaseAuth.getInstance().currentUser?.uid

                if (currentUserID == petDetailsModel.userId) {
                    toolbarEditDetail.visibility = View.VISIBLE
                    toolbarDeleteDetail.visibility = View.VISIBLE
                } else {
                    toolbarEditDetail.visibility = View.GONE
                    toolbarDeleteDetail.visibility = View.GONE
                }
            }
        } else {
            Log.e(TAG, "Pet details not found.")
            finish()
        }
    }

    // Create and show a dialog to confirm pet deletion
    private fun showDeleteConfirmationDialog(petDetailsModel: Pet) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Confirm Deletion")
        alertDialogBuilder.setMessage("Are you sure you want to delete this pet?")

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->

            deletePetFromFirebase(petDetailsModel)
        }

        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->

            dialog.dismiss()
        }

        alertDialogBuilder.create().show()
    }

    private fun deletePetFromFirebase(petDetailsModel: Pet) {
        // Use the unique ID to delete the specific pet from Firebase
        val petRef =
            FirebaseDatabase.getInstance().getReference("Pets").child(petDetailsModel.userId.toString())
        petRef.child(petDetailsModel.petId.orEmpty()).removeValue()


        // Close the activity after deletion
        finish()
    }
}