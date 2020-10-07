package com.example.cashlessfuel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {
     val SPLASH_DISPLAY_LENGTH : Long = 3000

    /** Called when the activity is first created.  */
    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.activity_splash)

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/Handler().postDelayed(Runnable { /* Create an Intent that will start the Menu-Activity. */
            val mainIntent = Intent(this, LoginActivity::class.java)
            //val mainIntent = Intent(this, DashboardActivity::class.java)
            this.startActivity(mainIntent)
            this.finish()
        }, SPLASH_DISPLAY_LENGTH)
    }

}