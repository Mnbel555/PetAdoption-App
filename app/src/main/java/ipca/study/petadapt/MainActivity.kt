package ipca.study.petadapt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize

class MainActivity : AppCompatActivity() {

    private lateinit var splashLogo: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )

        // Find the logo view
        splashLogo = findViewById(R.id.splash_logo)

        // Ensure that the view is a ConstraintLayout and perform animation
        if (splashLogo is ConstraintLayout) {
            // Set to 0 for fade-in effect
            (splashLogo as ConstraintLayout).alpha = 0f
            // Animate property
            (splashLogo as ConstraintLayout).animate().setDuration(3000).alpha(1f).withEndAction {
                // After the animation, navigate login screen
                val intent = Intent(this, LogIn::class.java)
                startActivity(intent)
                // fade-in, fade-out transition between activities
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        } else {
            //if view is not a ConstraintLayout
            // Log error or message
        }
    }
}
