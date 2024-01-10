package ipca.study.petadapt


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.util.Patterns
import android.view.View
import android.widget.Button

import android.widget.TextView
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



class SignUp : AppCompatActivity() {

    // Declare UI elements
    private lateinit var signupUsername: TextView
    private lateinit var signupName: TextView

    private lateinit var signupEmail: TextView
    private lateinit var signupPassword: TextView
    private lateinit var signupConfirmPassword: TextView
    private lateinit var signupButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    // User input variables
    private lateinit var sUserName: String
    private lateinit var sName: String
    private lateinit var sEmail: String
    private lateinit var sPassword: String
    private lateinit var sConfirmPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        signupUsername = findViewById(R.id.signup_username)
        signupName = findViewById(R.id.signup_name)
        signupEmail = findViewById(R.id.signup_email)
        signupPassword = findViewById(R.id.signup_password)
        signupConfirmPassword = findViewById(R.id.signup_confirm_password)
        signupButton = findViewById(R.id.signup_button)

        signupButton.setOnClickListener {
            // Get user input
            sUserName = signupUsername.text.toString().trim()
            sName = signupName.text.toString().trim()
            sEmail = signupEmail.text.toString().trim()
            sPassword = signupPassword.text.toString().trim()
            sConfirmPassword = signupConfirmPassword.text.toString().trim()

            if (!isValidEmail(sEmail)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check for empty fields
            if (sEmail.isEmpty() || sPassword.isEmpty() || sConfirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (sPassword != sConfirmPassword) {
                Toast.makeText(
                    this,
                    "Passwords do not match. Please enter the same password",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Register New user in Firebase Authentication
            auth.createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                Log.d("SignUp", "Email verification sent successfully.")
                                Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()
                                saveData()
                            }
                            ?.addOnFailureListener {
                                Log.e("SignUp", "Email verification failed: $it")
                                Toast.makeText(this, "Email verification failed: $it", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Log.e("SignUp", "Authentication failed: ${task.exception}")
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun saveData() {
        sUserName = signupUsername.text.toString().trim()
        sName = signupName.text.toString().trim()
        sEmail = signupEmail.text.toString().trim()
        sPassword = signupPassword.text.toString().trim()

        val user = Model(sUserName ,sName,  sEmail, sPassword)
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("User").child(userID).setValue(user)

        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
    }

    fun onGoogleSignUpClick(view: View) {

    }
    fun onFacebookSignUpClick(view: View) {

    }
    fun onSignInClick(view: View) {
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
    }
}
