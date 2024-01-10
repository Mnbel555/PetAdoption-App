package ipca.study.petadapt
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class myads_ads : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterAd: AdapterAd
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
        val view = inflater.inflate(R.layout.fragment_myads_ads, container, false)

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
        val allPetsQuery: Query = database.child("User").child(currentUser.uid).child("Pets")
        Log.d(TAG, "Number of pets: ${allPetsQuery}")
        query = allPetsQuery

        query.addValueEventListener(object : ValueEventListener {
            // Called when data at this location changes
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "Number of pets: ${dataSnapshot.childrenCount}")
                adArrayList.clear()

                // Loop through data snapshot and add pets in list
                for (dss in dataSnapshot.children) {
                    val user = dss.getValue(Pet::class.java)
                    adArrayList.add(user as Pet)
                }
                if (context != null) {
                    adapterAd = AdapterAd(requireContext(), adArrayList)

                    // Set layout manager for RecyclerView
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())

                    // Set adapter for RecyclerView
                    recyclerView.adapter = adapterAd
                }
                Log.d(TAG, "Pet list size after update: ${adArrayList.size}")
            }

            // If onDataChange operation fails
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: Error: ${error.message}")
            }
        })
    }
}
