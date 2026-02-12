package com.example.walletmatetracker.ui.splash



import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.walletmatetracker.R
import com.example.walletmatetracker.data.local.database.ExpenseDatabase
import com.example.walletmatetracker.ui.main.MainActivity
import com.example.walletmatetracker.ui.onboarding.GetStartedActivity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this, GetStartedActivity::class.java))
//            finish()
//        }, 2500) // 2.5 seconds


        val userDao = ExpenseDatabase.getDatabase(this).userDao()

        lifecycleScope.launch {

            val loggedInUser = userDao.getLoggedInUser()

            if (loggedInUser != null) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, GetStartedActivity::class.java))
            }

            finish()


        }
    }
}

