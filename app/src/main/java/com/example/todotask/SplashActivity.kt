package com.example.todotask

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.codingstuff.todolist.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Correct way to handle ActionBar in Kotlin
        val actionBar = supportActionBar
        actionBar?.hide()

        // Corrected Handler declaration in Kotlin
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()  // Close SplashActivity so it doesn't remain in the back stack
        }, 2000)  // Delay in milliseconds (4000ms = 4 seconds)
    }
}
