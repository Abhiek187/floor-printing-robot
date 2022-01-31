package com.example.linuxtest.image

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import com.example.linuxtest.activities.MainActivity
import java.io.File
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class CustomDraw(context: Context) : View(context) {
    private val access = context as MainActivity
    private var xCoord = 0f
    private var yCoord = 0f
    private var mBitmap: Bitmap? = null // loaded drawing

    private var paints = arrayListOf<Paint>()
    private var finalPath = arrayListOf<Array<Path>>() // arrays of 5 paths
    private var sizePaint = -1

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Don't draw until the spinners have loaded
        if (sizePaint < 0) return false

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN -> {
                val ids = event.actionIndex
                if (ids > 4) return false
                xCoord = event.getX(ids)
                yCoord = event.getY(ids)
                finalPath[sizePaint][ids].moveTo(xCoord,yCoord)
            }
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    xCoord = event.getX(i)
                    yCoord = event.getY(i)
                    finalPath[sizePaint][i].lineTo(xCoord,yCoord)
                }
            }
        }

        invalidate()
        performClick()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mBitmap?.let {
            // Hardware bitmaps don't support software rendering
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && it.config == Bitmap.Config.HARDWARE) {
                mBitmap = it.copy(Bitmap.Config.ARGB_8888, true)
            }

            canvas.drawBitmap(it, 0f, 0f, null)
        }

        for (k in 0 until finalPath.size) {
            for (path in finalPath[k]) {
                canvas.drawPath(path, paints[k])
            }
        }
    }

    fun clearDrawing() {
        mBitmap = null
        finalPath.clear()
        paints.clear()
        sizePaint = -1
        updatePaint(access.curWidth, access.curColor)
        invalidate()
    }

    fun saveDrawing(imageName: String, scale: Boolean): Uri? {
        // Save drawing as a bitmap and convert it to a PNG file
        var bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // make background white instead of transparent (black)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        draw(canvas)

        if (scale) {
            bitmap = Bitmap.createScaledBitmap(bitmap, (this.width / 3f).roundToInt(),
                (this.height / 3f).roundToInt(), true)
        }

        val cv = ContentValues()
        val fileName = if (scale) "scaled_$imageName.png" else "$imageName.png"
        cv.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png")

        // Enforce scoped storage for Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cv.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            cv.put(MediaStore.MediaColumns.IS_PENDING, 1)
        } else {
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(directory, fileName)
            cv.put(MediaStore.MediaColumns.DATA, file.absolutePath)
        }

        val uri = access.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)

        if (uri != null) {
            thread {
                access.contentResolver.openOutputStream(uri).use { output ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                }
            }
        } else {
            Toast.makeText(access.applicationContext,
                "Unable to save drawing, no content resolver found", Toast.LENGTH_SHORT).show()
        }

        return uri
    }

    fun updateDrawing(image: Image) {
        // Write a new bitmap to the image's designated URI
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // make background white instead of transparent (black)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        draw(canvas)

        thread {
            access.contentResolver.openOutputStream(image.uri.toUri()).use { output ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            }
        }
    }

    fun loadDrawing(image: Image) {
        // Clear the previous drawing and load the image as a bitmap
        clearDrawing()
        val uri = image.uri.toUri()

        mBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val src = ImageDecoder.createSource(access.contentResolver, uri)
            ImageDecoder.decodeBitmap(src)
        } else {
            MediaStore.Images.Media.getBitmap(access.contentResolver, uri)
        }
    }

    fun updatePaint(width: Float, color: Int) {
        val painted = Paint(Paint.ANTI_ALIAS_FLAG)
        painted.style = Paint.Style.STROKE
        painted.strokeWidth = width
        painted.color = color
        paints.add(painted)
        sizePaint += 1
        val paths = Array(5) { Path() }
        finalPath.add(paths)
    }

    fun loadPicture(temp: Bitmap) {
        // Retrieve picture from the Photo Gallery
        clearDrawing()
        mBitmap = Bitmap.createBitmap(temp)
    }
}
