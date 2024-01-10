package ipca.study.petadapt

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// Adapter for RecyclerView in HomeFragment
class HomeAdapter(context: Context, petList: ArrayList<Pet>) : RecyclerView.Adapter<HomeAdapter.HolderAd>() {

    private val context: Context = context
    private var petList: ArrayList<Pet> = petList

    // Initialization block, used for logging the pet list size
    init {
        Log.d("HomeAdapter", "Pet list size: ${petList.size}")
    }


    // Inflates the layout for each item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAd {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_ads_home, parent, false)
        return HolderAd(view)
    }

    // Binds the data to views in each item view
    override fun onBindViewHolder(holder: HolderAd, position: Int) {
        val pet = petList[position]
        Log.d("HomeAdapter", "Binding data for pet: ${pet.adTitle}")
        holder.bind(pet)
        // Set a click listener forentire item view to navigate to AdDetailsActivity
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, AdDetailsActivity::class.java)
            // Pass the petId to the PetDetailsActivity

//            val bundle = Bundle()
//            bundle.putParcelable("pet", pet)
//            intent.putExtra("petId", pet.petId.orEmpty())
            intent.putExtra("pets", pet)
            // Start the PetDetailsActivity
            context.startActivity(intent)
        })


    }
    // Returns the total number of items in the data set
    override fun getItemCount(): Int {
        return petList.size
    }

    // ViewHolder class for each item view in RecyclerView
    inner class HolderAd(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val adsImage: ShapeableImageView = itemView.findViewById(R.id.ads_image_iv)
        private val homeAdTitle: TextView = itemView.findViewById(R.id.home_ad_title)
        private val homeAdDescription: TextView = itemView.findViewById(R.id.home_ad_discription)
        private val locationAdHome: TextView = itemView.findViewById(R.id.location_ad_home)
        private val favButton: ImageButton = itemView.findViewById(R.id.fav_button)


        // Bind data views
        fun bind(pet: Pet) {
            homeAdTitle.text = pet.adTitle
            homeAdDescription.text = pet.adDescription
            locationAdHome.text = pet.petBreed

            Log.d("HomeAdapter", "Image URL: ${pet.imageUrls}")
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.dummy_image)
            requestOptions.error(R.drawable.dummy_image)
            Glide.with(context)
                .load(pet.imageUrls).apply(requestOptions)
                .into(adsImage)
            // Set favorite button based on isFavourite status
            if (pet.isFavourite == true){
                favButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.fav))
            }else{
                favButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.fav_icon))
            }
            // Set a click listener for favorite button to update the pet's favorite status
            favButton.setOnClickListener(View.OnClickListener {
                if (pet.isFavourite == true){
                    pet.isFavourite = false
                    Log.d("==check:::","${pet.petId}::::::::::::::::")
                    updatePetDetails(pet.petId.toString(),pet)
                    favButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.fav_icon))
                }else{
                    pet.isFavourite = true
                    Log.d("==check:::","${pet.petId}::::::::::::::::")
                    updatePetDetails(pet.petId.toString(),pet)
                    favButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.fav))
                }

            })
        }
    }

    // Function to update the pet details in user-specific database
    private fun updatePetDetails(petId: String, pet: Pet) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid
        if (userID != null) {
            val database = FirebaseDatabase.getInstance().reference
            // Update the user-specific Pets node with the modified pet details
            database.child("User").child(userID).child("Pets").child(petId).setValue(pet)
                .addOnSuccessListener {
                    updateGlobalPetDetails(petId,pet)

                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

    // Function to update the pet details in global Pets database
    private fun updateGlobalPetDetails(petId: String, pet: Pet) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid
        if (userID != null) {
            val database = FirebaseDatabase.getInstance().reference

            // Update the global Pets node with the modified pet details
            database.child("User").child("Pets").child(petId).setValue(pet)
                .addOnSuccessListener {
                    notifyDataSetChanged()
                    // Notify adapter that the dataset has changed

                }
                .addOnFailureListener {

                }
        }
    }
}
