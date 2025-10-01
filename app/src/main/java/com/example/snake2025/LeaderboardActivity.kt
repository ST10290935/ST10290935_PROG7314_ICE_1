package com.example.snake2025

import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LeaderboardActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        fetchTopScores()
    }

    private fun fetchTopScores() {
        db.collection("scores")
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { snaps ->
                val list = ArrayList<Map<String,String>>()
                for (doc in snaps) {
                    val username = doc.getString("username") ?: "Unknown"
                    val score = doc.getLong("score")?.toString() ?: "0"
                    val m = mapOf("line1" to username, "line2" to "Score: $score")
                    list.add(m)
                }
                val from = arrayOf("line1","line2")
                val to = intArrayOf(android.R.id.text1, android.R.id.text2)
                val adapter = SimpleAdapter(this, list, android.R.layout.simple_list_item_2, from, to)
                findViewById<ListView>(R.id.lvLeaderboard).adapter = adapter
            }
    }
}
