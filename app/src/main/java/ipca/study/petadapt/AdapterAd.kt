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


class AdapterAd(context: Context, petList: ArrayList<Pet>) : RecyclerView.Adapter<AdapterAd.HolderAd>() {

    private val context: Context = context
    private var petList: ArrayList<Pet> = petList


    init {
        Log.d("AdapterAd", "Pet list size: ${petList.size}")
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAd {
        // Inflate the layout for each item in the RecyclerView
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_ads_home, parent, false)
        return HolderAd(view)
    }

    override fun onBindViewHolder(holder: HolderAd, position: Int) {
        val pet = petList[position]
        Log.d("AdapterAd", "Binding data for pet: ${pet.adTitle}")
        holder.bind(pet)
        // Set click listener for each item to open the PetDetailsActivity

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

    override fun getItemCount(): Int {
        // Return the total number of items in the data set
        return petList.size
    }

    inner class HolderAd(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val adsImage: ShapeableImageView = itemView.findViewById(R.id.ads_image_iv)
        private val homeAdTitle: TextView = itemView.findViewById(R.id.home_ad_title)
        private val homeAdDescription: TextView = itemView.findViewById(R.id.home_ad_discription)
        private val locationAdHome: TextView = itemView.findViewById(R.id.location_ad_home)
        private val favButton: ImageButton = itemView.findViewById(R.id.fav_button)

        // Bind data to the ViewHolder
        fun bind(pet: Pet) {
            homeAdTitle.text = pet.adTitle
            homeAdDescription.text = pet.adDescription
            locationAdHome.text = pet.petBreed

            Log.d("AdapterAd", "Image URL: ${pet.imageUrls}")

            // Load and display the pet image using Glide library
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.dummy_image)
            requestOptions.error(R.drawable.dummy_image)
            Glide.with(context)
                .load(pet.imageUrls).apply(requestOptions)
                .into(adsImage)

            // Set the favorite button icon based on the favorite status
            if (pet.isFavourite == true){
                favButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.fav))
            }else{
                favButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.fav_icon))
            }

            // Set click listener for the favorite button to update favorite status
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

    // Update the favorite status in Firebase
    private fun updatePetDetails(petId: String, pet: Pet) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid
        if (userID != null) {
            val database = FirebaseDatabase.getInstance().reference
            database.child("User").child(userID).child("Pets").child(petId).setValue(pet)
                .addOnSuccessListener {
                   updateGlobalPetDetails(petId,pet)

                }
                .addOnFailureListener {

                }
        }
    }
    // Update the favorite status globally in Firebase
    private fun updateGlobalPetDetails(petId: String, pet: Pet) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid
        if (userID != null) {
            val database = FirebaseDatabase.getInstance().reference
            database.child("User").child("Pets").child(petId).setValue(pet)
                .addOnSuccessListener {
                    notifyDataSetChanged()

                }
                .addOnFailureListener {

                }
        }
    }
}
