package com.example.walletmatetracker.ui.auth.login


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.walletmatetracker.data.local.database.ExpenseDatabase
import com.example.walletmatetracker.databinding.ActivityLoginBinding
import com.example.walletmatetracker.ui.auth.signup.SignUpActivity
//import com.example.walletmatetracker.ui.auth.signup.SignUpActivity
import com.example.walletmatetracker.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val userDao by lazy {
        ExpenseDatabase.getDatabase(this).userDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {

        binding.btnSignIn.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            lifecycleScope.launch {

                val user = userDao.getUserByEmail(email)

                when {
                    user == null -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "No account found. Please sign up.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    user.password != password -> {
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid credentials",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        // Save login session in Room
                        userDao.setUserLoggedIn(user.id)

                        startActivity(
                            Intent(this@LoginActivity, MainActivity::class.java)
                        )
                        finish()
                    }
                }
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
