package ipca.study.petadapt

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ipca.study.petadapt.databinding.RowImagesPickedBinding
import java.util.*

class AdapterImagePick(
    private val context: Context,
    private val imageUris: ArrayList<Uri>
) : RecyclerView.Adapter<AdapterImagePick.HolderImagePicked>() {

    private lateinit var binding: RowImagesPickedBinding


    // Constants
    private companion object {
        private const val TAG = "IMAGES_TAG"
    }
    // Create a ViewHolder for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagePicked {
        binding = RowImagesPickedBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderImagePicked(binding.root)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: HolderImagePicked, position: Int) {
        val imageUri = imageUris[position]
        Log.d(TAG, "onBindViewHolder: imageUri $imageUri")

        try {
            // Load and display the image using Glide library
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.image)
            requestOptions.error(R.drawable.image)
            Glide.with(context)
                .load(imageUri).apply(requestOptions)
                .into(holder.imageIv)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image: ${e.message}")
        }
        // Set click listener for the close button to remove the image
        holder.closeButton.setOnClickListener {
            imageUris.removeAt(position)
            notifyDataSetChanged()
        }
    }
    // Return the total number of items in the data set
    override fun getItemCount(): Int {
        return imageUris.size
    }

    // Inner class representing the ViewHolder for each item in the RecyclerView
    inner class HolderImagePicked(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: RowImagesPickedBinding = RowImagesPickedBinding.bind(itemView)
        val imageIv = binding.imageIv
        val closeButton = binding.removeImage
    }
}
