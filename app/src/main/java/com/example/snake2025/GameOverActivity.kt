package com.example.snake2025

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snake2025.models.Score
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GameOverActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val score = intent.getIntExtra("score", 0)
        findViewById<TextView>(R.id.tvFinalScore).text = "Score: $score"

        findViewById<Button>(R.id.btnSaveAndContinue).setOnClickListener {
            saveScore(score)
        }

        findViewById<Button>(R.id.btnPlayAgain).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.btnLeaderboard).setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }
    }

    private fun saveScore(score: Int) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Not signed in", Toast.LENGTH_SHORT).show()
            return
        }
        val uid = user.uid
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val username = doc.getString("username") ?: user.email ?: "Unknown"
            val s = Score(uid = uid, username = username, score = score.toLong(), timestamp = Timestamp.now())
            db.collection("scores").add(s).addOnSuccessListener {
                Toast.makeText(this, "Score saved", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Save failed: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "User fetch failed: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
