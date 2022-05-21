package com.SeeChenAndLeYe.Pi

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView

class about_Page : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_page)

        val button_Back: ImageButton = findViewById(R.id.imageButton_BackButton2)
        button_Back.setOnClickListener {
            finish()
        }

        val about_Feedback: LinearLayout = findViewById(R.id.about_Feedback)
        about_Feedback.setOnClickListener {
            val intent = Intent(this, Feedback_Page::class.java)
            startActivity(intent)
        }

        val About_Overview: LinearLayout = findViewById(R.id.about_Overview)
        About_Overview.setOnClickListener {
            val intent = Intent(this, about_Overview::class.java)
            startActivity(intent)
        }

        val about_License: LinearLayout = findViewById(R.id.about_License)
        about_License.setOnClickListener {
            val intent = Intent(this, OpenSourceLicense::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.appVersion).text =  resources.getText(R.string.text_version).toString() + " " + BuildConfig.VERSION_NAME
    }
}