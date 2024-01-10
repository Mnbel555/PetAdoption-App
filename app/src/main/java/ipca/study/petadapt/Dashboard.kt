package ipca.study.petadapt


import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class Dashboard : AppCompatActivity(), AddFragment.BottomNavigationListener {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initializing BottomNavigationView
       bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Setting up item selection listener for BottomNavigationView
       bottomNavigationView.setOnItemSelectedListener { menuItem ->
           when(menuItem.itemId){
               R.id.bottom_home -> {
                   replaceFragment(HomeFragment())
                   true
               }
               R.id.bottom_chat -> {
                   replaceFragment(ChatFragment())
                   true
               }
               R.id.bottom_add  -> {
                   replaceFragment(AddFragment())
                   bottomNavigationView.visibility = View.GONE
                   true
               }
               R.id.bottom_favorite -> {
                   replaceFragment(MyAdsFragment())
                   true
               }
               R.id.bottom_profile -> {
                   replaceFragment(ProfileFragment())
                   true
               }
               else -> false
           }
       }
        // Initial fragment to be displayed (HomeFragment)
       replaceFragment(HomeFragment())
    }
    //BottomNavigationListener interface method to show BottomNavigationView
    override fun showBottomNavigation() {
        bottomNavigationView.visibility = View.VISIBLE
    }

    //Replace the current fragment in container
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()/*commit applies changes to fragment*/
    }

}