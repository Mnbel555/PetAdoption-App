package ipca.study.petadapt

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth


class MyAdsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ads_my, container, false)

        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        // Create an instance of MyAdsPagerAdapter
        val adapter = MyAdsPagerAdapter(childFragmentManager, lifecycle)

        // Addchild fragments to the adapter
        adapter.addFragment(myAdsFav(), "Favorites")
        adapter.addFragment(myads_ads(), "My Ads")

        // Set the adapter to ViewPager2
        viewPager.adapter = adapter

        // Connect the TabLayout with ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        return view
    }
}




