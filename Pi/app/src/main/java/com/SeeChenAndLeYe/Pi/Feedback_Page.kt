package com.SeeChenAndLeYe.Pi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.telecom.TelecomManager
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

var feedback_User_Content : String = ""

class Feedback_Page : AppCompatActivity() {
    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_page)

        val button_Back: ImageButton = findViewById(R.id.imageButton_BackButton3)
        button_Back.setOnClickListener {
            finish()
        }

        val systemVersion = android.os.Build.VERSION.RELEASE
        val systemModel = android.os.Build.MODEL
        val deviceBrand = android.os.Build.BRAND
        val cpuInformation = BufferedReader(FileReader("/proc/cpuinfo"), 8192).readLine()

        val editText_Title: EditText = findViewById(R.id.feedbackPage_Title)
        editText_Title.onFocusChangeListener = View.OnFocusChangeListener{ view, b ->
            if(!b){
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
                    hideSoftInputFromWindow(editText_Title.windowToken, 0)
                }
            }
        }

        val feedback_content:TextView = findViewById(R.id.feedback_content)
        var clickTimes = 1
        feedback_content.setOnClickListener {
            val handle = Handler()
            if (clickTimes != 2){
                clickTimes += 1
                handle.postDelayed({clickTimes = 1}, 300)
            } else {
                open_feedback_Content_Input()
            }
        }
        feedback_content.setOnLongClickListener {
            open_feedback_Content_Input()

            return@setOnLongClickListener true
        }
        feedback_content.movementMethod = ScrollingMovementMethod()

        val button_Submit: Button = findViewById(R.id.feedback_Submit)
        button_Submit.setOnClickListener {

            if (feedback_User_Content != "") {
                var submitTitle: String = resources.getText(R.string.text_Feedback)
                    .toString() + "-" + editText_Title.text.toString()
                var submitInformation: Boolean =
                    findViewById<Switch>(R.id.allowPhoneInformation).isChecked
                var submitContent: String = ""
                var systemInformation: String =
                    "\nSystem Version : " + systemVersion + "\nSystem Model : " + systemModel + "\nDevice Brand : " + deviceBrand + "\n CPU : " + cpuInformation
                if (submitInformation) {
                    submitContent =  feedback_content.text.toString() + systemInformation
                } else {
                    submitContent = feedback_content.text.toString()
                }

                var emailAddress = arrayOf("AndroidTermProjectFeedback@outlook.com")

                val intent = Intent(Intent.ACTION_SENDTO)
                intent.type = "text/html"
                intent.data = Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL, emailAddress)
                intent.putExtra(Intent.EXTRA_SUBJECT, submitTitle)
                intent.putExtra(Intent.EXTRA_TEXT, submitContent)

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(this, resources.getText(R.string.target_not_found), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, resources.getText(R.string.feedback_no_content), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun open_feedback_Content_Input(){
        val intent = Intent(this, feedback_Content_Input::class.java)
        startActivity(intent)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            if (ev.action == MotionEvent.ACTION_DOWN) {
                val v = currentFocus
                if(v is EditText){
                    // 监听焦点是否在编辑框里面
                    v.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onResume() {
        super.onResume()
        if (feedback_User_Content != ""){
            findViewById<TextView>(R.id.feedback_content).text = feedback_User_Content
        }

    }
}