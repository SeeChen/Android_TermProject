package com.SeeChenAndLeYe.Pi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.ImageButton
import android.widget.TextView

class OpenSourceLicense : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_source_license)

        val back_Button: ImageButton = findViewById(R.id.imageButton_BackButton5)
        back_Button.setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.licenseContent).movementMethod = ScrollingMovementMethod()
    }
}