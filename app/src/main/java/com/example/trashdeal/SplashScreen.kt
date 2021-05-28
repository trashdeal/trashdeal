package com.example.trashdeal

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val bgnd_img = findViewById<ImageView>(R.id.backimg)

        setContentView(R.layout.activity_splash_screen)
        val videoView = findViewById<VideoView>(R.id.videoView)
        val video = Uri.parse("android.resource://" + packageName + "/" + R.raw.logosplashscreen)
        videoView.setVideoURI(video)
        videoView.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        })
        videoView.start()


    }

}