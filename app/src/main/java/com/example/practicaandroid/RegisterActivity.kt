package com.example.practicaandroid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.practicaandroid.databinding.ActivityRegisterBinding
import com.example.practicaandroid.model.User
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        instances()
        setupActions()
    }

    private fun instances() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
    }

    private fun setupActions() {
        binding.btnRegister.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {
                        handleRegister(it, name, email)
                    }
            } else {
                Snackbar.make(binding.root, "Por favor, rellena todos los campos", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.tvGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun handleRegister(
        task: Task<AuthResult>,
        name: String,
        email: String
    ) {
        if (task.isSuccessful) {
            val uid = auth.currentUser?.uid
            val user = User(name, email)
            if (uid != null) {
                database.getReference("users").child(uid).setValue(user)
            }
            // Registro exitoso
            Snackbar.make(
                binding.root,
                "Usuario registrado correctamente, quieres iniciar sesion?",
                Snackbar.LENGTH_SHORT
            )
                .setAction("OK") {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("usuario", user)
                    startActivity(intent)
                    finish()
                }
                .show()
        } else {
            Snackbar.make(
                binding.root,
                "Error al registrarse: ${task.exception?.message}",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}