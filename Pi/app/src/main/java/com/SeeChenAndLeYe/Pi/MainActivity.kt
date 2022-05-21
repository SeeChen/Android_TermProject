package com.SeeChenAndLeYe.Pi

import android.animation.*
import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

var systemLanguage: String = ""
var oriBitmap: Bitmap ?= null
var newBitmap: Bitmap ?= null
var editMode: String ?= null

var haveNew: Boolean ?= false


class MainActivity : AppCompatActivity() {

    val myDatabase = myDatabase(this, "testtb", 1)

    var inDeleteMode = false
    var deleta_Fram: FrameLayout ?= null
    var short_View: ImageView ?= null
    var text_Title: TextView ?= null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DateNow", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()).toString())
        systemLanguage = resources.configuration.locales.get(0).language
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val createNew:Button = findViewById(R.id.create_New)
        createNew.setOnClickListener(){
            editMode = "New"
            val createNew = Intent(this, editPicture::class.java)
            startActivity(createNew)
        }

        val button_About:Button = findViewById(R.id.button_About)
        button_About.setOnClickListener {
            val aboutPage = Intent(this, about_Page::class.java)
            startActivity(aboutPage)
        }
        readPastProject()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun readPastProject(){
        val linear_PastProject: LinearLayout = findViewById(R.id.linear_PastProject)

        val db = myDatabase.writableDatabase
        val query = db.query("testtb", null, null ,null, null, null, null)
        if (query.moveToLast()) {
            do {
                val title = query.getString(0)
                val oriBM = query.getString(1)
                val newBM = query.getString(2)
                val date = query.getString(3)

                val parentLinear = LinearLayout(this)
                val shortView = ImageView(this)
                val textTitle = TextView(this)
                val deleteFrame = FrameLayout(this)
                val deleteView = ImageView(this)

                parentLinear.orientation = LinearLayout.HORIZONTAL
                parentLinear.minimumHeight = dp2Pixel(100)!!.toInt()
                parentLinear.setBackgroundColor(Color.parseColor("#494949"))
                var parentLinearParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                dp2Pixel(0)?.let { dp2Pixel(0)?.let { it1 ->
                    parentLinearParams.setMargins(it.toInt(),0,
                        it1.toInt(),5)
                } }
                parentLinear.layoutParams = parentLinearParams
                parentLinear.addForegroundRipple()

                var shortViewParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                shortViewParams.weight = 20f
                if (shortViewParams != null) {
                    shortViewParams.gravity = Gravity.CENTER
                }
                shortView.layoutParams = shortViewParams
                shortView.setImageBitmap(StringAndBitmap().string2Bitmap(newBM))

                var textTitleParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                textTitleParams.weight = 80f
                if (textTitleParams != null) {
                    textTitleParams.gravity = Gravity.CENTER
                }
                textTitle.layoutParams = textTitleParams
                textTitle.gravity = Gravity.CENTER
                textTitle.textSize = 20f
                textTitle.text = title

                deleteFrame.setBackgroundColor(Color.RED)
                var deleteParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

                deleteFrame.layoutParams = deleteParams
                deleteFrame.visibility = View.GONE
                deleteFrame.addForegroundRipple()

                deleteFrame.setOnClickListener {
                    db.execSQL("DELETE FROM testtb WHERE lastEditDate = '$date'")
                    parentLinear.visibility = View.GONE
                }

                deleteView.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_delete_24))

                deleteView.scaleType = ImageView.ScaleType.CENTER
                deleteFrame.addView(deleteView)

                dp2Pixel(100)?.let { dp2Pixel(100)?.let { it1 -> parentLinear.addView(shortView, it.toInt(), it1.toInt()) } }
                parentLinear.addView(textTitle)

                parentLinear.addView(deleteFrame)

                parentLinear.setOnClickListener {
                    editMode = "Past"
                    val createNew = Intent(this, editPicture::class.java)
                    startActivity(createNew)

                    oriBitmap = StringAndBitmap().string2Bitmap(oriBM)
                    newBitmap = StringAndBitmap().string2Bitmap(newBM)
                }
                parentLinear.setOnLongClickListener {
                    dp2Pixel(100)?.let { it1 ->
                        ObjectAnimator.ofFloat(deleteFrame, "translationY",
                            it1
                        ).apply {
                            duration = 0
                            start()
                        }
                    }
                    dp2Pixel(0)?.let { it1 ->
                        ObjectAnimator.ofFloat(deleteFrame, "translationY",
                            it1
                        ).apply {
                            duration = 100
                            start()
                        }
                    }
                    ObjectAnimator.ofFloat(shortView, "alpha", 1f, 0f).apply {
                        duration = 100
                        start()
                    }
                    ObjectAnimator.ofFloat(shortView, "alpha", 1f, 0f).apply {
                        duration = 100
                        start()
                    }
                    ObjectAnimator.ofFloat(textTitle, "alpha", 1f, 0f).apply {
                        duration = 100
                        start()
                    }
                    deleteFrame.visibility = View.VISIBLE
                    shortView.visibility = View.GONE

                    deleta_Fram = deleteFrame
                    short_View = shortView
                    text_Title = textTitle
                    inDeleteMode = true
                    return@setOnLongClickListener true
                }

                linear_PastProject.addView(parentLinear)
            } while(query.moveToPrevious())

        } else {

        }

        query.close()
    }

    fun View.addForegroundRipple() = with(TypedValue()) {
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
        foreground = ContextCompat.getDrawable(context, resourceId)
    }

    // Start of dp2Pixel Function
    fun dp2Pixel(dpValue: Int): Float?{
        val density = resources.displayMetrics.density
        return (dpValue * density + 0.5f)
    }
    // End of dp2Pixel Function

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            if (ev.action == MotionEvent.ACTION_DOWN) {
                if (inDeleteMode){
                    val x = ev.rawX.toInt()
                    val y = ev.rawY.toInt()
                    if (deleta_Fram?.let { test(it, x, y) } == false){
                        val ani1 = dp2Pixel(100)?.let { it1 ->
                            ObjectAnimator.ofFloat(deleta_Fram, "translationY",
                                it1
                            ).apply {
                                duration = 100
                            }
                        }
                        val ani2 = ObjectAnimator.ofFloat(short_View, "alpha", 0f, 1f).apply {
                            duration = 100
                        }
                        val ani3 = ObjectAnimator.ofFloat(short_View, "alpha", 0f, 1f).apply {
                            duration = 100
                        }
                        val ani4 =ObjectAnimator.ofFloat(text_Title, "alpha", 0f, 1f).apply {
                            duration = 100
                        }
                        AnimatorSet().apply {
                            play(ani1).with(ani2).with(ani3).with(ani4)
                            duration = 100
                            start()
                            addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    deleta_Fram!!.visibility = View.GONE
                                    short_View?.visibility = View.VISIBLE
                                    super.onAnimationEnd(animation)
                                }
                            })
                        }
                        inDeleteMode = false
                        return true
                    }
                    inDeleteMode = false
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun test(view: FrameLayout, x: Int, y: Int): Boolean{
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + view.measuredWidth
        val bottom = top + view.measuredHeight

        Log.d("aaaaac", top.toString())
        Log.d("aaaaac", bottom.toString())
        Log.d("aaaaac", left.toString())
        Log.d("aaaaac", right.toString())
        Log.d("aaaaac", x.toString())
        Log.d("aaaaac", y.toString())

        return y >= top && y <= bottom && x >= left && x <= right
    }

    override fun onResume() {
        super.onResume()
        findViewById<LinearLayout>(R.id.linear_PastProject).removeAllViews()
        readPastProject()
    }

}