package com.example.snake2025

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class GameActivity : AppCompatActivity(), GameView.GameEvents {
    private lateinit var gameView: GameView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        auth = FirebaseAuth.getInstance()
        gameView = findViewById(R.id.gameView)

        findViewById<Button>(R.id.btnUp).setOnClickListener { gameView.changeDirection(GameView.Direction.UP) }
        findViewById<Button>(R.id.btnDown).setOnClickListener { gameView.changeDirection(GameView.Direction.DOWN) }
        findViewById<Button>(R.id.btnLeft).setOnClickListener { gameView.changeDirection(GameView.Direction.LEFT) }
        findViewById<Button>(R.id.btnRight).setOnClickListener { gameView.changeDirection(GameView.Direction.RIGHT) }

        findViewById<Button>(R.id.btnLeaderboard).setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        gameView.setGameEventsListener(this)
    }

    override fun onGameOver(score: Int) {
        val i = Intent(this, GameOverActivity::class.java)
        i.putExtra("score", score)
        startActivity(i)
        finish()
    }
}
