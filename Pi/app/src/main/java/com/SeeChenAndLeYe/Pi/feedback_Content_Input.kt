package com.SeeChenAndLeYe.Pi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged

class feedback_Content_Input : AppCompatActivity() {
    var feedback_isEdit:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_content_input)

        findViewById<ImageButton>(R.id.imageButton_backButton).setOnClickListener {
            if (feedback_isEdit) {
                leave_Edit()
            } else {
                finish()
            }
        }

        val systemLanguage = resources.configuration.locales.get(0).language
        var wordLimit: String ?= ""
        val feedback_Edit_Content: EditText = findViewById(R.id.feedback_Edit_Content)
        feedback_Edit_Content.doAfterTextChanged {
            feedback_isEdit = true
            if (systemLanguage == "en"){
                wordLimit = (1000 - (feedback_Edit_Content.text.length)).toString() + " Word Left"
            } else if (systemLanguage == "zh") {
                wordLimit = "剩余 " + (1000 - (feedback_Edit_Content.text.length)).toString() + " 字"
            }

            findViewById<TextView>(R.id.word_Count).text = wordLimit
        }

        if (feedback_User_Content != ""){
            feedback_isEdit = false
            feedback_Edit_Content.setText(feedback_User_Content)
        }

        val submit_button: ImageButton = findViewById(R.id.imageButton_Refresh)
        submit_button.setOnClickListener {
            feedback_User_Content = feedback_Edit_Content.text.toString()
            finish()
        }
    }

    private fun leave_Edit(){
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
            .setMessage(resources.getString(R.string.editNoSave))
            .setPositiveButton(resources.getString(R.string.text_Save)){_, which ->
                feedback_User_Content = findViewById<EditText>(R.id.feedback_Edit_Content).text.toString()
                feedback_isEdit = false
                finish()
            }.setNegativeButton(resources.getString(R.string.text_DontSave)){_, which ->
                feedback_isEdit = false
                finish()
            }.setNeutralButton(resources.getString(R.string.text_Cancel)){_, which ->

            }
            .create().show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){ // 返回键事件
            if(feedback_isEdit){
                leave_Edit()
            }else{
                finish()
            }
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}