package com.example.greenlight

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val iv = findViewById<ImageView>(R.id.imgView_logo)
        val myAnim = AnimationUtils.loadAnimation(this, R.anim.mytransition)
        iv.startAnimation(myAnim)
        val i = Intent(this@SplashActivity, MainActivity::class.java)
        val timer: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(4000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    startActivity(i)
                    finish()
                }
            }
        }
        timer.start()
    }
}
