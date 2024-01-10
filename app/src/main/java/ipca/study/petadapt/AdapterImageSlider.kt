package ipca.study.petadapt

import android.content.ContentValues.TAG
import android.content.Context
import android.nfc.Tag
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView

import ipca.study.petadapt.databinding.RowImageSliderBinding


class AdapterImageSlider : RecyclerView.Adapter<AdapterImageSlider.HolderImageSlider> {

    private lateinit var binding: RowImageSliderBinding

    // Constants
    private companion object{
        private const val TAG = "IMAGE_SLIDER_TAG"
    }

    private lateinit var context: Context

    private lateinit var imageArrayList: ArrayList<ModelImageSlider>


    // Create a ViewHolder for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImageSlider {
        //bind xml
        binding = RowImageSliderBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderImageSlider(binding.root)

    }
    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: HolderImageSlider, position: Int) {
      //set view in row_ad.xml
        val modelImageSlider = imageArrayList[position]
        val imageUrl = modelImageSlider.imageUrl
        val imageCount = "${position+1}/${imageArrayList.size}"

        // Set image count and load the image using Glide library
        holder.image_count.text = imageCount
        try {

            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.dummy_image)
            requestOptions.error(R.drawable.dummy_image)
            Glide.with(context)
                .load(imageUrl).apply(requestOptions)
                .into(holder.image_iv)
        } catch (e: Exception){
            Log.e(TAG, "onBindViewHolder: ",e)
        }
        // Set a click listener for the item
        holder.itemView.setOnClickListener{

        }

    }
    // Return the total number of items in the data set
    override fun getItemCount(): Int {
       return imageArrayList.size
    }

    constructor(context: Context,imageArrayList: ArrayList<ModelImageSlider>){7

    this.context = context
    this.imageArrayList = imageArrayList}

    // Inner class representing the ViewHolder for each item in the RecyclerView

    inner class HolderImageSlider(itemView: View): RecyclerView.ViewHolder(itemView){

        var image_iv: ShapeableImageView  = binding.imageIv
        var image_count: TextView = binding.imageCount

    }
}