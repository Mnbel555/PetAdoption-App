package ipca.study.petadapt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LogIn : AppCompatActivity() {

    private lateinit var loginEmail: TextView
    private lateinit var loginPassword: TextView
    private lateinit var loginButton: Button
    private lateinit var forgotPassword: TextView
    private lateinit var gotoSignupButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        // Initialize Firebase Authentication
        auth = Firebase.auth
        loginEmail = findViewById(R.id.login_email)
        loginPassword = findViewById(R.id.login_password)
        forgotPassword = findViewById(R.id.forgot_password)
        gotoSignupButton = findViewById(R.id.goto_signup_button)
        loginButton = findViewById(R.id.login_button)
        loginButton.setOnClickListener {

            // Get user input
            val email = loginEmail.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            // Validate email format
            if (!isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate password presence
            if (password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sign in with email and password
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Check if email is verified
                        val verification = auth.currentUser?.isEmailVerified
                        if(verification == true){
                        val user = auth.currentUser
                        updateUI()
                        }else
                            Toast.makeText(this,"Please verify your email", Toast.LENGTH_SHORT).show()
                    } else {

                        //authentication fails, display error message
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        //updateUI(null)
                    }

                }



        }

        // Redirect to sign-up screen
        gotoSignupButton.setOnClickListener {
            val signUpIntent = Intent(this, SignUp::class.java)
            startActivity(signUpIntent)
        }

        // Redirect to password reset screen
        forgotPassword.setOnClickListener {
            val PasswordResetIntent = Intent(this, PasswordReset::class.java)
            startActivity(PasswordResetIntent)
        }
    }


    // Redirect to main dashboard screen
    private fun updateUI() {
        val intent = Intent(this, Dashboard::class.java)
        startActivity(intent)
    }

    // Validate email format using Patterns.EMAIL_ADDRESS
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    // Placeholder functions for future implementation (e.g., Google and Facebook login)
    fun onGoogleLoginClick(view: View) {

    }
    fun onFacebookLoginClick(view: View) {

    }

    // Check if user is already logged in when activity resumes
    public override fun onResume() {
        super.onResume()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI()
        }
    }
}


