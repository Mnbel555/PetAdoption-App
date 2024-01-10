package ipca.study.petadapt

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyAdsPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments = ArrayList<Fragment>()
    private val fragmentTitles = ArrayList<String>()

    // Add a fragment to the adapter
    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentTitles.add(title)
    }

    // Return the total number of fragments
    override fun getItemCount(): Int {
        return fragments.size
    }

    // Create and return fragment specified position
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    //  fragment title at specified position
    fun getPageTitle(position: Int): String {
        return fragmentTitles[position]
    }
}
