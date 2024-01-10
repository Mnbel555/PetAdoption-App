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

// Fragment for displaying a list of pets on home screen
class HomeFragment : Fragment() {

    private lateinit var adapter: HomeAdapter
    private lateinit var database: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    private var petList: ArrayList<Pet> = ArrayList()
    private lateinit var query: Query
    private lateinit var recyclerView: RecyclerView

    private var context: Context? = null

    // Attaches the fragment with context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    // Detaches fragment from context
    override fun onDetach() {
        super.onDetach()
        this.context = null
    }


    // Creates and returns the view associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize Firebase Database reference and get the current user
        database = FirebaseDatabase.getInstance().reference
        currentUser = FirebaseAuth.getInstance().currentUser!!

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.home_all_ads_rv)

        // Check if the user is authenticated and log the authentication token
        if (currentUser != null) {
            currentUser.getIdToken(true)
                .addOnSuccessListener { result ->
                    val token = result.token
                    Log.d(TAG, "Authentication Token: $token")

                    // Proceed with the app logic,like fetch all pets
                    fetchAllPets()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting authentication token: ${e.message}")
                }
        } else {
            // Handle where user is not authenticated
            Log.e(TAG, "User is not authenticated")
            //show a message or redirect to the login screen
        }

        return view
    }

    // Fetches all pets from the database
    private fun fetchAllPets() {
        val allPetsQuery: Query = database.child("User").child("Pets")
        Log.d(TAG, "Number of pets: ${allPetsQuery}")
        query = allPetsQuery

        query.addValueEventListener(object : ValueEventListener {

            // Called when data changes in the database
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "Number of pets: ${dataSnapshot.childrenCount}")
                petList.clear()

                // Iterate through the data snapshot to retrieve pet information
                for (dss in dataSnapshot.children) {
                    val user = dss.getValue(Pet::class.java)
                    petList.add(user as Pet)
                }

                // Update the RecyclerView with the new pet list
                if (context != null){
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    adapter = HomeAdapter(requireContext(), petList)
                    recyclerView.adapter = adapter
                }
                Log.d(TAG, "Pet list size after update: ${petList.size}")
            }

            // LOG error
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: Error: ${error.message}")
            }
        })
    }

}
