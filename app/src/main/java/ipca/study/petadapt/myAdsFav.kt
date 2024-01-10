package ipca.study.petadapt



import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class myAdsFav : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterAd: HomeAdapter
    private var adArrayList: ArrayList<Pet> = ArrayList()

    private lateinit var database: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    private lateinit var query: Query

    private var context: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onDetach() {
        super.onDetach()
        this.context = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_ads_fav, container, false)
        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference
        currentUser = FirebaseAuth.getInstance().currentUser!!

        // Initialize RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.home_all_ads_rv)

        // Retrieve data from Firebase and populate adArrayList
        retrieveDataFromFirebase()

        return view
    }
    private fun retrieveDataFromFirebase() {
        val allPetsQuery: Query = database.child("User").child("Pets")
        Log.d(ContentValues.TAG, "Number of pets: ${allPetsQuery}")
        query = allPetsQuery

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(ContentValues.TAG, "Number of pets: ${dataSnapshot.childrenCount}")
                adArrayList.clear()

                // add pets to the list
                for (dss in dataSnapshot.children) {
                    val user = dss.getValue(Pet::class.java)
                    adArrayList.add(user as Pet)
                }
                var filterFavouriteList = adArrayList.filter { s -> s.isFavourite == true }

                if (context != null) {
                    // Initialize and set up the adapter with filtered list
                    adapterAd = HomeAdapter(requireContext(), filterFavouriteList as ArrayList<Pet>)

                    // Set layout manager for RecyclerView
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())

                    // Set adapter for RecyclerView
                    recyclerView.adapter = adapterAd
                }
                Log.d(ContentValues.TAG, "Pet list size after update: ${filterFavouriteList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled: Error: ${error.message}")
            }
        })
    }
}
