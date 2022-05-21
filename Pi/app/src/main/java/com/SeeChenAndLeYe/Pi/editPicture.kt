package com.SeeChenAndLeYe.Pi

import android.R.attr.bitmap
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class editPicture : AppCompatActivity() {
    companion object {
        val TAKE_PHOTO: Int = 1
        val CHOOSE_PHOTO: Int = 2
    }

    var imageUri: Uri ?= null
    var isEditing: Boolean = false

    val myDatabase = myDatabase(this, "testtb", 1)

    // Start of onCreate Function
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_picture)

        val backButton: ImageButton = findViewById(R.id.imageButton_BackButton)
        val editTitle: EditText = findViewById(R.id.editText_Title)

        // 返回按钮点击事件
        backButton.setOnClickListener {
            finish()
        }
        // 返回键点击事件

        // 编辑框焦点事件
        editTitle.onFocusChangeListener = View.OnFocusChangeListener{ view, b ->
            if(!b){
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
                    // 收起软键盘
                    hideSoftInputFromWindow(editTitle.windowToken, 0)
                }
            }
        }
        // 编辑框焦点事件

        val floatingCheck: View = findViewById(R.id.floating_Check)
        val floatingClose: View = findViewById(R.id.floating_Close)
        val floatingSave: View  = findViewById(R.id.floating_Save)
        val floatingDownload: View = findViewById(R.id.floating_Download)

        val buttonOpenResource: ImageButton = findViewById(R.id.imageButton_openFile)
        val imageViewArea: ImageView = findViewById(R.id.showImageArea)

        val styleZero: Button = findViewById(R.id.styleZero)
        val styleOne: Button = findViewById(R.id.styleOne)
        val styleTwo: Button = findViewById(R.id.styleTwo)

        val showOriginal: Button = findViewById(R.id.showOriginal)

        val linkContent: EditText = findViewById(R.id.linkContent)
        val linkConfigm: Button = findViewById(R.id.button_LinkConfigm)
        val linkCancel: Button = findViewById(R.id.button_LinkCancel)

        // 若是历史项目 则加载项目
        if (editMode == "Past") {
            imageViewArea.setImageBitmap(newBitmap)
            imageViewArea.scaleType = ImageView.ScaleType.FIT_CENTER
            isEditing = true
            loadAllLayout()
        }

        // 点击图片区，没有图片时打打开相机开始
        imageViewArea.setOnClickListener {
            if (!isEditing)
                take_Photo()
        }
        // 点击图片区，没有图片时打打开相机结束

        // 点击 Menu 菜单开始
        buttonOpenResource.setOnClickListener {
            val popup_Menu = PopupMenu(this, buttonOpenResource)
            val inflater: MenuInflater = popup_Menu.menuInflater
            inflater.inflate(R.menu.onekey_menu, popup_Menu.menu)
            popup_Menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener{ item ->
                when(item.itemId) {
                    R.id.menu_openCamera -> {
                        take_Photo()
                    }

                    R.id.menu_openGallery -> {
                        choose_Photo()
                    }

                    R.id.menu_FromWeb -> {
                        web_Photo()
                    }

                    else -> {}
                }
                true
            })

            // 如果 Android 版本大于 10 时 设置 Menu 的图标
            if (Build.VERSION.SDK_INT >= 29) {
                popup_Menu.setForceShowIcon(true)
            }

            popup_Menu.show()
        }
        // 点击 Menu 菜单结束

        // 点按显示原图事件开始
        showOriginal.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    showOriginal.text = resources.getText(R.string.hide_Original)
                    imageViewArea.setImageBitmap(oriBitmap)
                }

                MotionEvent.ACTION_UP -> {
                    showOriginal.text = resources.getText(R.string.show_Original)
                    imageViewArea.setImageBitmap(newBitmap)
                }

                else -> {}
            }

            return@setOnTouchListener true
        }
        // 点按显示原图事件结束

        // 保存项目按钮事件开始
        floatingSave.setOnLongClickListener {
            Toast.makeText(this, R.string.text_saveProject, Toast.LENGTH_LONG).show()
            return@setOnLongClickListener true
        }
        floatingSave.setOnClickListener {
            saveProject()
        }
        // 保存项目按钮事件结束
        // 保存图片按钮事件开始
        floatingDownload.setOnLongClickListener {
            Toast.makeText(this, R.string.text_saveToDevice, Toast.LENGTH_LONG).show()
            return@setOnLongClickListener true
        }
        floatingDownload.setOnClickListener {
            Toast.makeText(this, resources.getText(R.string.file_Save_to).toString() + saveImage((imageViewArea.drawable as BitmapDrawable).bitmap, findViewById<EditText>(R.id.editText_Title).text.toString()), Toast.LENGTH_SHORT).show()
        }
        // 保存图片按钮事件结束

        // 保存按钮点击事件开始
        floatingCheck.setOnClickListener {
            floatingSave.visibility = View.VISIBLE
            floatingDownload.visibility = View.VISIBLE
            floatingClose.visibility = View.VISIBLE

            val saveTop =
                dp2Pixel(-65)?.let { it1 ->
                    ObjectAnimator.ofFloat(floatingSave, "translationY", it1).apply {
                        duration = 500
                    }
                }
            val saveShow = ObjectAnimator.ofFloat(floatingSave, "alpha", 0f, 1f).apply {
                duration = 500
            }
            val saveAnimation = AnimatorSet().apply {
                play(saveTop).with(saveShow)
            }

            val downloadTop = dp2Pixel(-130)?.let { it1 ->
                ObjectAnimator.ofFloat(floatingDownload, "translationY", it1).apply {
                    duration = 500
                }
            }
            val downloadShow = ObjectAnimator.ofFloat(floatingDownload, "alpha", 0f, 1f).apply {
                duration = 500
            }
            val downloadAnimation = AnimatorSet().apply {
                play(downloadTop).with(downloadShow)
            }

            val checkHide = ObjectAnimator.ofFloat(floatingCheck, "alpha", 1f, 0f).apply {
                duration = 500
            }
            val checkHideRotation = ObjectAnimator.ofFloat(floatingCheck, "rotation", 0f, 360f).apply {
                duration = 500
            }

            val closeShow = ObjectAnimator.ofFloat(floatingClose, "alpha", 0f, 1f).apply {
                duration = 500
            }

            AnimatorSet().apply {
                play(saveAnimation).with(downloadAnimation)
                play(saveAnimation).with(checkHide)
                play(saveAnimation).with(closeShow)
                play(saveAnimation).with(checkHideRotation)
                start()
                addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        floatingCheck.visibility = View.GONE
                        super.onAnimationEnd(animation)
                    }
                })
            }
        }

        floatingClose.setOnClickListener {
            floatingCheck.visibility = View.VISIBLE

            val saveTop =
                dp2Pixel(0)?.let { it1 ->
                    ObjectAnimator.ofFloat(floatingSave, "translationY", it1).apply {
                        duration = 500
                    }
                }
            val saveShow = ObjectAnimator.ofFloat(floatingSave, "alpha", 1f, 0f).apply {
                duration = 500
            }
            val saveAnimation = AnimatorSet().apply {
                play(saveTop).with(saveShow)
            }

            val downloadTop = dp2Pixel(0)?.let { it1 ->
                ObjectAnimator.ofFloat(floatingDownload, "translationY", it1).apply {
                    duration = 500
                }
            }
            val downloadShow = ObjectAnimator.ofFloat(floatingDownload, "alpha", 1f, 0f).apply {
                duration = 500
            }
            val downloadAnimation = AnimatorSet().apply {
                play(downloadTop).with(downloadShow)
            }

            val checkShow = ObjectAnimator.ofFloat(floatingCheck, "alpha", 0f, 1f).apply {
                duration = 500
            }

            val closeHide = ObjectAnimator.ofFloat(floatingClose, "alpha", 1f, 0f).apply {
                duration = 500
            }
            val closeHideRotation = ObjectAnimator.ofFloat(floatingClose, "rotation", 0f, 360f).apply {
                duration = 500
            }

            AnimatorSet().apply {
                play(saveAnimation).with(downloadAnimation)
                play(saveAnimation).with(checkShow)
                play(saveAnimation).with(closeHide)
                play(saveAnimation).with(closeHideRotation)
                start()
                addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        floatingSave.visibility = View.GONE
                        floatingDownload.visibility = View.GONE
                        floatingClose.visibility = View.GONE
                        super.onAnimationEnd(animation)
                    }
                })
            }
        }
        // 保存按钮点击事件结束

        // 输入链接确认按钮开始
        linkConfigm.setOnClickListener {
            var uriPhoto = linkContent.text.toString()

            if (uriPhoto == ""){
                Toast.makeText(this, resources.getText(R.string.hint_input_link), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            DownloadImageFromInternet(imageViewArea).execute(uriPhoto)
            imageViewArea.scaleType = ImageView.ScaleType.FIT_CENTER

            loadAllLayout()
            closeLinkLayout()
        }
        // 输入链接确认按钮结束

        // 输入链接取消按钮开始
        linkCancel.setOnClickListener {
            closeLinkLayout()
        }
        // 输入链接取消按钮结束

        // 点击样式事件开始
        // 样式零
        styleZero.setOnClickListener {
            newBitmap = oriBitmap
            imageViewArea.setImageBitmap(newBitmap)
        }
        // 样式一
        styleOne.setOnClickListener {
            newBitmap = oriBitmap?.let { it1 -> photoStyle().styleOne(it1, 33) }
            imageViewArea.setImageBitmap(newBitmap)
        }
        // 样式二
        styleTwo.setOnClickListener {
            newBitmap = oriBitmap?.let { it1 -> photoStyle().styleTwo(it1) }
            imageViewArea.setImageBitmap(newBitmap)
        }

        // 点击样式事件结束
    }
    // End of onCreate Function

    // Start of saveImage Function
    private fun saveImage(bitmap: Bitmap, title: String):Uri{
        val saveImageUrl = MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            title,
            ""
        )

        return Uri.parse(saveImageUrl)
    }
    // Start of saveImage Function

    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        init {
            Toast.makeText(applicationContext, "Please wait, it may take a few minute...",     Toast.LENGTH_SHORT).show()
        }
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }

    // Start of saveProject Function
    fun saveProject(){
        haveNew = true

        val db = myDatabase.writableDatabase

        val values = ContentValues().apply {
            put("projectTitle", findViewById<EditText>(R.id.editText_Title).text.toString())
            put("oriBitMap", oriBitmap?.let { StringAndBitmap().Bitmap2String(it) })
            put("newBitMap", newBitmap?.let { StringAndBitmap().Bitmap2String(it) })
            put("lastEditDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()).toString())
        }

        db.insert("testtb", null, values)
    }
    // End of saveProject Function

    // THE FOLLOWING FUNCTION HAVE BEEN COMPLETED

    // Start of take_Photo Function
    fun take_Photo() {

        val outputImage = File(externalCacheDir, "output_Image.jpg")

        try {
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        imageUri =
            FileProvider.getUriForFile(
                this@editPicture,
                "com.try.haha",
                outputImage
            )

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, TAKE_PHOTO)
    }
    // End of take_Photo Function
    // Start of choose_Photo Function
    fun choose_Photo(){
        if (ContextCompat.checkSelfPermission(this@editPicture, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@editPicture, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            startActivityForResult(intent, CHOOSE_PHOTO)
        }
    }
    // End of choose_Photo Function
    // Start of web_Photo Function
    fun web_Photo(){
        val inputLink_Layout = findViewById<FrameLayout>(R.id.inputLink_Layout)
        inputLink_Layout.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(inputLink_Layout, "alpha", 0f, 1f).apply {
            duration = 500
            start()
        }
        isEditing = true
    }
    // End of web_Photo Function

    // Start of closeLinkLayout Function
    fun closeLinkLayout() {
        val inputLink_Layout = findViewById<FrameLayout>(R.id.inputLink_Layout)
        ObjectAnimator.ofFloat(inputLink_Layout, "alpha", 1f, 0f).apply {
            duration = 500
            start()
            addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    inputLink_Layout.visibility = View.GONE
                    super.onAnimationEnd(animation)
                }
            })
        }
    }
    // End of closeLinkLayout Function

    // Start of loadAllLayout Function
    fun loadAllLayout(){
        if (editMode == "New"){
            oriBitmap = (findViewById<ImageView>(R.id.showImageArea).drawable as BitmapDrawable).bitmap
            newBitmap = oriBitmap
        }
        findViewById<FrameLayout>(R.id.style_Layout).visibility = View.VISIBLE
        findViewById<View>(R.id.floating_Check).visibility = View.VISIBLE
        findViewById<Button>(R.id.showOriginal).visibility = View.VISIBLE
    }
    // End of loadAllLayout Function

    // Start of dp2Pixel Function
    fun dp2Pixel(dpValue: Int): Float?{
        val density = resources.displayMetrics.density
        return (dpValue * density + 0.5f)
    }
    // End of dp2Pixel Function

    // Start of dispatchTouchEvent Function
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // 监听焦点触摸
        if (ev != null){
            if (ev.action == MotionEvent.ACTION_DOWN) {
                val v = currentFocus

                // 监听焦点是否在编辑框里面
                if (v is EditText) {
                    v.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    // End of dispatchTouchEvent Function

    // Start of Override onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageViewArea: ImageView = findViewById(R.id.showImageArea)

        when (requestCode) {
            TAKE_PHOTO -> if (resultCode == RESULT_OK) {
                try {
                    var bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri!!))
                    imageViewArea.setImageBitmap(bitmap)
                    imageViewArea.scaleType = ImageView.ScaleType.FIT_CENTER
                    isEditing = true
                    loadAllLayout()
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }

            CHOOSE_PHOTO -> if (resultCode == RESULT_OK) {
                if (data != null) {
                    imageUri = data.data
                    imageViewArea.setImageURI(imageUri)
                    imageViewArea.scaleType = ImageView.ScaleType.FIT_CENTER
                    isEditing = true
                    loadAllLayout()
                }
            }

            else -> {}
        }
    }
    // End of Override onActivityResult

    // Start of Override onRequestPermissionResult
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            1 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    choose_Photo()
                } else {
                    Toast.makeText(this, resources.getString(R.string.deniedPermission), Toast.LENGTH_SHORT).show()
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    // End of Override onRequestPermissionResult
}