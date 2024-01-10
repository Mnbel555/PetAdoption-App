package ipca.study.petadapt


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val myAccountButton: Button = view.findViewById(R.id.my_account_button)
        myAccountButton.setOnClickListener {
            // Navigate to the RetrieveProfile activity
            val intent = Intent(activity, RetrieveProfile::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        val  buttonPasswordChange: Button = view.findViewById(R.id.change_password_button)
        buttonPasswordChange.setOnClickListener {
            val intent = Intent(activity, PasswordReset::class.java)
            startActivity(intent)
        }
        val  buttonLogout: Button    = view.findViewById(R.id.logout_button)
        buttonLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return view
    }
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { dialog, which ->

            Firebase.auth.signOut()
            val intent = Intent(activity, LogIn::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("No", null)

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}