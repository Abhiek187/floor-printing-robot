package com.example.linuxtest.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.example.linuxtest.R
import com.example.linuxtest.adapter.ImageAdapter
import com.example.linuxtest.databinding.ActivityMainBinding
import com.example.linuxtest.databinding.PopupSaveBinding
import com.example.linuxtest.databinding.StrokeImagesBinding
import com.example.linuxtest.image.CustomDraw
import com.example.linuxtest.storage.ImagesDBHelper
import com.example.linuxtest.storage.Prefs

class MainActivity : AppCompatActivity() {
    private val illegalChars = charArrayOf('/', '\n', '\r', '\t', '\u0000', '`', '?', '*', '\\',
        '<', '>', '|', '\"', ':') // illegal file name characters
    private var currentImgName: String? = null
    private val widths = arrayListOf(8f,12f,16f,20f,24f,28f,32f)

    /*private val imageArrays = arrayOf(
        R.drawable.stroke_width_8f,
        R.drawable.stroke_width_10f,
        R.drawable.stroke_width_12f,
        R.drawable.stroke_width_14f,
        R.drawable.stroke_width_16f,
        R.drawable.stroke_width_18f,
        R.drawable.stroke_width_20f
    )*/

    private val colors = arrayListOf(
        Color.BLACK,
        /*Color.RED,
        Color.rgb(255,165,0), // orange
        Color.YELLOW,
        Color.GREEN,
        Color.BLUE,
        Color.rgb(128,0,128), // purple
        Color.rgb(165,42,42), // brown*/
        Color.WHITE
    )
    private val colNames = arrayListOf("Black",/*"Red","Orange","Yellow","Green","Blue","Purple",
        "Brown",*/"White") // must be in the same order as above
    var curWidth = 8f
    var curColor = Color.BLACK
    private lateinit var drawView: CustomDraw

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = Prefs(this)
        if (sharedPref.isFirst) {
            startActivity(Intent(this, IntroScreen::class.java))
        }

        // UI elements
        val pageLayout = binding.pageLayout
        val buttonUpload = binding.buttonUpload
        val buttonSave = binding.buttonSave
        val buttonLoad = binding.buttonLoad
        val buttonPrint = binding.buttonPrint
        val buttonSettings = binding.setting
        val line1 = binding.line1
        val line2 = binding.line2
        val spin1 = binding.brushWidth
        val spin2 = binding.colors

        val strokeBinding = StrokeImagesBinding.inflate(layoutInflater)
        val view1 = strokeBinding.view1
        val view2 = strokeBinding.view2
        val view3 = strokeBinding.view3
        val view4 = strokeBinding.view4
        val view5 = strokeBinding.view5
        val view6 = strokeBinding.view6
        val view7 = strokeBinding.view7

        val b1 = Bitmap.createBitmap(view1.layoutParams.width,view1.layoutParams.height,Bitmap.Config.RGB_565)
        val b2 = Bitmap.createBitmap(view2.layoutParams.width,view2.layoutParams.height,Bitmap.Config.RGB_565)
        val b3 = Bitmap.createBitmap(view3.layoutParams.width,view3.layoutParams.height,Bitmap.Config.RGB_565)
        val b4 = Bitmap.createBitmap(view4.layoutParams.width,view4.layoutParams.height,Bitmap.Config.RGB_565)
        val b5 = Bitmap.createBitmap(view5.layoutParams.width,view5.layoutParams.height,Bitmap.Config.RGB_565)
        val b6 = Bitmap.createBitmap(view6.layoutParams.width,view6.layoutParams.height,Bitmap.Config.RGB_565)
        val b7 = Bitmap.createBitmap(view7.layoutParams.width,view7.layoutParams.height,Bitmap.Config.RGB_565)

        val imageArray = arrayOf(b1,b2,b3,b4,b5,b6,b7)

        val imagesDB = ImagesDBHelper(this)
        drawView = CustomDraw(this)
        currentImgName = intent.getStringExtra("imageName") // current saved image
        title = currentImgName ?: "Untitled" // show which file is being edited at the top

        currentImgName?.let {
            drawView.loadDrawing("${this.filesDir.path}/$it.png")
        }

        pageLayout.addView(drawView)
        pageLayout.foreground = getDrawable(R.drawable.shape_window_dim)
        pageLayout.foreground.alpha = 0 // have dim foreground there, but not in preview

        drawView.id=View.generateViewId()
        drawView.layoutParams.height=0 // 0 dp = match constraint
        val limits = ConstraintSet()
        limits.clone(pageLayout)
        limits.connect(drawView.id, ConstraintSet.TOP,line1.id,ConstraintSet.BOTTOM,1)
        limits.connect(drawView.id,ConstraintSet.BOTTOM,line2.id,ConstraintSet.TOP,1)
        limits.applyTo(pageLayout)

        val pictureAdapter = ImageAdapter(this, imageArray)
        spin1.adapter = pictureAdapter

        spin1.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                curWidth=widths[pos]
                drawView.updatePaint(curWidth,curColor)
            }
        }

        val infoColors = ArrayAdapter(this,android.R.layout.simple_list_item_1,colNames)
        infoColors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin2.adapter=infoColors

        spin2.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                curColor=colors[pos]
                drawView.updatePaint(curWidth,curColor)
            }
        }

        buttonSettings.setOnClickListener{
            startActivity(Intent(this, Settings::class.java))
        }

        buttonUpload.setOnClickListener {
            // Get photo from gallery
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" // access gallery or photos

            intent.resolveActivity(packageManager)?.let {
                startActivityForResult(intent, 0)
            }
        }

        buttonSave.setOnClickListener {
            // Save image to database
            currentImgName?.let {
                // Image already saved before, don't ask for name
                drawView.saveDrawing("$it.png", false)
                Toast.makeText(this, "Saved $it", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true // can dismiss by tapping outside the popup
            pageLayout.foreground.alpha = 220 // dim background when popup appears

            val popupView = PopupSaveBinding.inflate(layoutInflater)
            val popupWindow = PopupWindow(popupView.root, width, height, focusable)
            popupWindow.showAtLocation(pageLayout, Gravity.CENTER, 0, 0)

            val editTextName = popupView.editTextName
            val buttonSaveName = popupView.buttonSaveName

            editTextName.setOnKeyListener{_, keyCode,keyEvent ->
                if(keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    buttonSaveName.performClick()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            // Restore background when popup disappears
            popupWindow.setOnDismissListener { pageLayout.foreground.alpha = 0 }

            buttonSaveName.setOnClickListener inner@{
                val name = editTextName.text.toString()

                if (name.isEmpty()) {
                    Toast.makeText(this, "A name is required.", Toast.LENGTH_SHORT)
                        .show()
                    return@inner
                } else if (name.indexOfAny(illegalChars) >= 0) {
                    Toast.makeText(this, "Invalid file name", Toast.LENGTH_SHORT)
                        .show()
                    return@inner
                } else {
                    val copies = imagesDB.getSaves().filter { save -> save.name == name }

                    if (copies.isNotEmpty()) {
                        Toast.makeText(this, "Name already taken", Toast.LENGTH_SHORT)
                            .show()
                        return@inner
                    }
                }

                currentImgName = name
                this.title = name
                val image = "$name.png"
                imagesDB.addImage(name, image)
                drawView.saveDrawing(image, false)
                Toast.makeText(this, "Saved new image!", Toast.LENGTH_SHORT).show()
                popupWindow.dismiss()
            }
        }

        buttonLoad.setOnClickListener {
            // Select saved image, then load it when activity is recreated
            val intent = Intent(this, SavesActivity::class.java)
            startActivity(intent)
        }

        buttonPrint.setOnClickListener {
            // Send image for printing on the pi
            if (currentImgName == null) {
                Toast.makeText(this, "Please save before printing.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (!isOnline()) {
                Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val prefs = Prefs(this)
            if (prefs.username.isBlank() || prefs.password.isBlank() || prefs.hostname.isBlank()) {
                Toast.makeText(this, "RPI info missing in settings", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Save & scale before printing
            drawView.saveDrawing("$currentImgName.png", true)
            Toast.makeText(this, "Saved $currentImgName", Toast.LENGTH_SHORT).show()
            // Show print log
            val intent = Intent(this, PrintActivity::class.java)
            intent.putExtra("imageName", currentImgName)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            val info = data?.data ?: return // image URI (not to be confused with URL)
            val parcelFileDescriptor = contentResolver.openFileDescriptor(info, "r")

            val fileDescriptor = parcelFileDescriptor?.fileDescriptor ?: return
            val image2 = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            drawView.loadPicture(image2)

            parcelFileDescriptor.close()
        }
    }

    fun newDrawing() {
        // Clear = create new drawing (so can save new images on the same session)
        currentImgName = null
        title = "Untitled"
    }

    private fun isOnline(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val n = cm.activeNetwork
        n?.let {
            val nc = cm.getNetworkCapabilities(n)
            // Check for both wifi and cellular network
            return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
        return false
    }
}

