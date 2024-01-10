package ipca.study.petadapt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class PasswordReset : AppCompatActivity() {
    private lateinit var resetPassword:EditText
    private lateinit var resetConfirm:Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)


        resetPassword = findViewById(R.id.reset_email)
        resetConfirm = findViewById(R.id.confirm_button)

        auth = FirebaseAuth.getInstance()
        resetConfirm.setOnClickListener {
            // Get email entered by the user
            val resetEmail = resetPassword.text.toString().trim()

            if (resetEmail.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Send a password reset email to given email address
            auth.sendPasswordResetEmail(resetEmail)
                .addOnSuccessListener {
                    Toast.makeText(this, "Please check your Email", Toast.LENGTH_SHORT).show()
                    // Navigate to login screen after successful password reset
                    navigateToLogin()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun navigateToLogin() {
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
        finish() // Optionally finish the PasswordReset activity so the user cannot go back to it with the back button
    }
        fun onGoBackClick(view: View) {

            val intent = Intent(this, ProfileFragment::class.java)
            startActivity(intent)
        }
    }