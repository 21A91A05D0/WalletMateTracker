package com.example.walletmatetracker.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.example.walletmatetracker.ui.main.MainActivity


import com.example.walletmatetracker.databinding.ActivityGetStartedBinding
import com.example.walletmatetracker.ui.auth.login.LoginActivity

class GetStartedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGetStartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }
}
