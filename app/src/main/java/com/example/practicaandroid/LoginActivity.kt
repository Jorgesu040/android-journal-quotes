package com.example.practicaandroid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.practicaandroid.databinding.ActivityLoginBinding
import com.example.practicaandroid.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getData()
        instances()
        actions()
    }

    private fun getData() {
        val usuario = intent.getSerializableExtra("usuario") as? User
        usuario?.let{
            binding.emailInput.setText(usuario.correo)
        }
    }

    private fun instances() {
        auth = FirebaseAuth.getInstance()
    }

    private fun actions() {
        binding.btnLogin.setOnClickListener {
            // 1. Comprobar que todos los campos (edittext) estan rellenos
            val correo = binding.emailInput.text.toString()
            val pass = binding.passwordInput.text.toString()

            if (!camposRellenos(correo, pass)) {
                Snackbar.make(
                    binding.root,
                    "Los campos no estan rellenos",
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                auth.signInWithEmailAndPassword(correo, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            goToMainActivity()
                        } else {
                            Snackbar.make(
                                binding.root,
                                "Fallo de auth en la aplicacion",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        binding.tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun camposRellenos(correo: String, pass: String): Boolean {
        return (!correo.isEmpty() && !pass.isEmpty())
    }

    override fun onRestart () {
        super.onRestart()
        clearLoginCamps()
    }

    private fun clearLoginCamps() {
        binding.emailInput.text.clear()
        binding.passwordInput.text.clear()
    }
}