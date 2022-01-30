package com.example.linuxtest.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.linuxtest.R
import com.example.linuxtest.adapter.ImageAdapter
import com.example.linuxtest.databinding.ActivityMainBinding
import com.example.linuxtest.databinding.PopupSaveBinding
import com.example.linuxtest.databinding.StrokeImagesBinding
import com.example.linuxtest.image.CustomDraw
import com.example.linuxtest.image.Image
import com.example.linuxtest.storage.ImageDatabase
import com.example.linuxtest.storage.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val illegalChars = charArrayOf('/', '\n', '\r', '\t', '\u0000', '`', '?', '*', '\\',
        '<', '>', '|', '\"', ':') // illegal file name characters
    private var currentImgName: String? = null
    private var readStorageAllowed = false
    private val widths = arrayListOf(8f, 12f, 16f, 20f, 24f, 28f, 32f) // actual stroke widths

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
        val buttonClear = binding.clear
        val line1 = binding.line1
        val line2 = binding.line2
        val spin1 = binding.brushWidth
        val spin2 = binding.colors

        val imagesDB = ImageDatabase.getInstance(this).imageDao()
        drawView = CustomDraw(this)
        currentImgName = intent.getStringExtra("imageName") // current saved image
        title = currentImgName ?: "Untitled" // show which file is being edited at the top

        currentImgName?.let {
            lifecycleScope.launch(Dispatchers.IO) {
                val uriStr = imagesDB.getUri(it)

                withContext(Dispatchers.Main) {
                    drawView.loadDrawing(uriStr)
                }
            }
        }

        // Add canvas and constrain it in between the two lines
        pageLayout.addView(drawView)
        pageLayout.foreground = ContextCompat.getDrawable(this, R.drawable.shape_window_dim)
        pageLayout.foreground.alpha = 0 // have dim foreground there, but not in preview

        drawView.id = View.generateViewId()
        drawView.layoutParams.height = 0 // 0 dp = match constraint
        val limits = ConstraintSet()
        limits.clone(pageLayout)
        limits.connect(drawView.id, ConstraintSet.TOP, line1.id,  ConstraintSet.BOTTOM,1)
        limits.connect(drawView.id, ConstraintSet.BOTTOM, line2.id, ConstraintSet.TOP,1)
        limits.applyTo(pageLayout)

        // Create bitmaps out of the views in stroke_images.xml (to be displayed in the spinner)
        val strokeBinding = StrokeImagesBinding.inflate(layoutInflater)
        val views = arrayOf(strokeBinding.view1, strokeBinding.view2, strokeBinding.view3,
            strokeBinding.view4, strokeBinding.view5, strokeBinding.view6, strokeBinding.view7)
        val imageArray = views.map { view -> Bitmap.createBitmap(view.layoutParams.width,
            view.layoutParams.height, Bitmap.Config.RGB_565) }
        val pictureAdapter = ImageAdapter(this, imageArray)
        spin1.adapter = pictureAdapter

        spin1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                curWidth = widths[pos]
                drawView.updatePaint(curWidth, curColor)
            }
        }

        // Must be in the same order as colors above
        val colNames = resources.getStringArray(R.array.colors)
        val infoColors = ArrayAdapter(this, android.R.layout.simple_list_item_1, colNames)
        infoColors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin2.adapter = infoColors

        spin2.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                curColor = colors[pos]
                drawView.updatePaint(curWidth, curColor)
            }
        }

        buttonSettings.setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }

        buttonClear.setOnClickListener {
            drawView.clearDrawing()
            newDrawing()
        }

        val photoResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val info = result.data?.data // image URI (not to be confused with URL)

                info?.let {
                    contentResolver.openFileDescriptor(info, "r").use { parcelFileDescriptor ->
                        val fileDescriptor = parcelFileDescriptor?.fileDescriptor

                        fileDescriptor?.let { fd ->
                            val image2 = BitmapFactory.decodeFileDescriptor(fd)
                            drawView.loadPicture(image2)
                        }
                    }
                }
            }
        }

        buttonUpload.setOnClickListener {
            // Get photo from gallery
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                // Request permission to look at camera roll
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                readStorageAllowed = true
            }

            if (readStorageAllowed) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*" // access gallery or photos

                try {
                    photoResultLauncher.launch(intent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        this, "You must give permission to access photos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        buttonSave.setOnClickListener {
            // Save image to database
            currentImgName?.let {
                // Image already saved before, don't ask for name
                drawView.saveDrawing(it, false)
                Toast.makeText(this, "Saved $it.png", Toast.LENGTH_SHORT).show()
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

            editTextName.setOnKeyListener{ _, keyCode, keyEvent ->
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

                when {
                    name.isEmpty() -> {
                        Toast.makeText(this, "A name is required.", Toast.LENGTH_SHORT)
                            .show()
                        return@inner
                    }
                    name.indexOfAny(illegalChars) >= 0 -> {
                        Toast.makeText(this, "Invalid file name", Toast.LENGTH_SHORT)
                            .show()
                        return@inner
                    }
                }

                drawView.saveDrawing(name, false)?.let { uri ->
                    // Add image to the database in the background
                    lifecycleScope.launch {
                        try {
                            imagesDB.addImage(Image(name, uri.toString()))

                            withContext(Dispatchers.Main) {
                                currentImgName = name
                                this@MainActivity.title = name
                                Toast.makeText(applicationContext, "Saved new image!", Toast.LENGTH_SHORT).show()
                                popupWindow.dismiss()
                            }
                        } catch (ex: SQLiteConstraintException) {
                            // A conflict occurred when inserting the new image
                            withContext(Dispatchers.Main) {
                                Toast.makeText(applicationContext, "Name already taken", Toast.LENGTH_SHORT).show()
                                return@withContext
                            }
                        }
                    }
                }
            }
        }

        val drawingResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Load the image and change the title if an image was selected
                result.data?.getStringExtra("imageName")?.let { imgName ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val uriStr = imagesDB.getUri(imgName)

                        withContext(Dispatchers.Main) {
                            drawView.loadDrawing(uriStr)
                            this@MainActivity.title = imgName
                        }
                    }
                }
            }
        }

        buttonLoad.setOnClickListener {
            // Select saved image, then load it when activity is recreated
            val intent = Intent(this, SavesActivity::class.java)
            drawingResultLauncher.launch(intent)
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
            if (prefs.username.isBlank() || prefs.password.isBlank() || prefs.hostname.isBlank() ||
                prefs.serverName.isBlank() || prefs.serverPassword.isBlank() ||
                prefs.serverHostname.isBlank()) {
                Toast.makeText(this, "RPi info missing in settings", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Save & scale before printing
            drawView.saveDrawing(currentImgName!!, true)
            Toast.makeText(this, "Saved $currentImgName.png", Toast.LENGTH_SHORT).show()
            // Show print log
            val intent = Intent(this, PrintActivity::class.java)
            intent.putExtra("imageName", currentImgName)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0) {
            // If request is cancelled, the result array is empty
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readStorageAllowed = true
            }
        }
    }

    private fun newDrawing() {
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
