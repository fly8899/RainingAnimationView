package at.animationexampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import th.rainingAnimationView.AnimationView

class MainActivity : AppCompatActivity() {

    private lateinit var av: AnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        av = findViewById(R.id.av)
    }

    override fun onResume() {
        super.onResume()
        av.start()
    }

    override fun onPause() {
        super.onPause()
        av.stop()
    }
}