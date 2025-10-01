package com.example.snake2025

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snake2025.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        val usernameEt = findViewById<EditText>(R.id.etUsername)
        val emailEt = findViewById<EditText>(R.id.etEmail)
        val passEt = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val username = usernameEt.text.toString().trim()
            val email = emailEt.text.toString().trim()
            val pass = passEt.text.toString().trim()
            if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { t ->
                if (t.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    val user = User(username = username, email = email)
                    db.collection("users").document(uid).set(user).addOnSuccessListener {
                        startActivity(Intent(this, GameActivity::class.java))
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed saving user: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, t.exception?.localizedMessage ?: "Registration failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
