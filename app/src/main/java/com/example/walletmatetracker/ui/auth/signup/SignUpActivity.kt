package com.example.walletmatetracker.ui.auth.signup


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.walletmatetracker.data.local.database.ExpenseDatabase
import com.example.walletmatetracker.data.local.entity.UserEntity
import com.example.walletmatetracker.databinding.ActivitySignupBinding
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private val userDao by lazy {
        ExpenseDatabase.getDatabase(this).userDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {

        binding.btnSignUp.setOnClickListener {

            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()


            when {

                name.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                    Toast.makeText(
                        this,
                        "All fields are required",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                password != confirmPassword -> {
                    Toast.makeText(
                        this,
                        "Passwords do not match",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {

                    lifecycleScope.launch {

                        val existingUser =
                            userDao.getUserByEmail(email)

                        if (existingUser != null) {

                            Toast.makeText(
                                this@SignUpActivity,
                                "Account already exists",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            val newUser = UserEntity(
                                name = name,
                                email = email,
                                password = password
                            )

                            userDao.insertUser(newUser)

                            Toast.makeText(
                                this@SignUpActivity,
                                "Account created. Please login.",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        }
                    }
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
}
