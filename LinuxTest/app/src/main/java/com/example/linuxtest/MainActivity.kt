package com.example.linuxtest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.jcraft.jsch.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val illegalChars = charArrayOf('/', '\n', '\r', '\t', '\u0000', '`', '?', '*', '\\',
        '<', '>', '|', '\"', ':') // illegal file name characters
    private var currentImgName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("isTaskRoot? $isTaskRoot")

        // UI elements
        val pageLayout = findViewById<ConstraintLayout>(R.id.pageLayout)
        val buttonUpload = findViewById<Button>(R.id.buttonUpload)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonLoad = findViewById<Button>(R.id.buttonLoad)
        val buttonPrint = findViewById<Button>(R.id.buttonPrint)

        // Load content from JSON
        val json = JSONObject(assets.open("google-services.json").bufferedReader()
            .readText())
        val username = json.getString("username")
        val password = json.getString("password")
        val hostname = json.getString("hostname") // change if static IP address changes

        val imagesDB = ImagesDBHelper(this)
        val drawView = CustomDraw(this)
        currentImgName = intent.getStringExtra("imageName") // current saved image
        title = currentImgName ?: "Untitled" // show which file is being edited at the top

        currentImgName?.let {
            drawView.loadDrawing("${this.filesDir.path}/$it.png")
        }

        pageLayout.addView(drawView)
        pageLayout.foreground = getDrawable(R.drawable.shape_window_dim)
        pageLayout.foreground.alpha = 0 // have dim foreground there, but not in preview

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
                drawView.saveDrawing("$it.png")
                Toast.makeText(this, "Saved $it", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true // can dismiss by tapping outside the popup
            pageLayout.foreground.alpha = 220 // dim background when popup appears

            val popupView = layoutInflater.inflate(R.layout.popup_save, pageLayout,
                false)
            val popupWindow = PopupWindow(popupView, width, height, focusable)
            popupWindow.showAtLocation(pageLayout, Gravity.CENTER, 0, 0)

            val editTextName = popupView.findViewById<EditText>(R.id.editTextName)
            val buttonSaveName = popupView.findViewById<Button>(R.id.buttonSaveName)

            // Restore background when popup disappears
            popupWindow.setOnDismissListener { pageLayout.foreground.alpha = 0 }

            buttonSaveName.setOnClickListener inner@{
                val name = editTextName.text.toString()

                if (name.isEmpty()) {
                    Toast.makeText(this, "A name is required.", Toast.LENGTH_SHORT)
                        .show()
                    return@inner
                } else if (name.indexOfAny(illegalChars) >= 0) {
                    Toast.makeText(this, "Invalid file name", Toast.LENGTH_SHORT).show()
                    return@inner
                }

                currentImgName = name
                val image = "$name.png"
                imagesDB.addImage(name, image)
                drawView.saveDrawing(image)
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
            thread {
                ssh(username, password, hostname)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            println(data?.data) // image URI (not to be confused with URL)
        }
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

    private fun ssh(username: String, password: String, hostname: String, port: Int = 22) {
        try {
            val jsch = JSch()
            // Connect to SSH using the Java Secure Channel
            val session = jsch.getSession(username, hostname, port)
            session.setPassword(password)

            // Avoid asking for key confirmation
            val properties = Properties()
            properties["StrictHostKeyChecking"] = "no"
            session.setConfig(properties)

            println("Connecting to $hostname...")
            session.connect()

            val image = "$currentImgName.png" // 60.3 KB = 13:11 (~76 bytes/s)
            sftp(session, src = "${this.filesDir.path}/$image", dest = "./floor*")
            execute(session, command = "python3 lol.py")
            execute(session, command = "cd floor* && python3 img_info.py '$image'")

            println("Disconnecting from $hostname...")
            session.disconnect()
        } catch (ex: JSchException) {
            ex.printStackTrace()
            println("Couldn't connect to $hostname")
        }
    }

    private fun sftp(session: Session, src: String, dest: String) {
        // Transfer files through sftp (similar to scp)
        val sftpChannel = session.openChannel("sftp") as ChannelSftp
        sftpChannel.connect()
        sftpChannel.put(src, dest) // or sftpChannel.get(dest, src)
        println("Transferred file from $src to $dest")
        sftpChannel.disconnect()
    }

    private fun execute(session: Session, command: String) {
        // Create an SSH Channel
        val sshChannel = session.openChannel("exec") as ChannelExec
        val outputStream = ByteArrayOutputStream()
        val errStream = ByteArrayOutputStream()
        sshChannel.outputStream = outputStream
        sshChannel.setExtOutputStream(errStream)

        // Execute Linux command
        sshChannel.setCommand(command)
        sshChannel.connect()
        println("Output from $command:")

        // Wait for command to finish
        while (!sshChannel.isClosed) {
            println("Waiting for response...")
            Thread.sleep(1_000)
        }

        // Check if program succeeded or failed
        if (sshChannel.exitStatus == 0) {
            println(outputStream.toString()) // success: print stdout
        } else {
            println(errStream.toString()) // error: print stderr
        }

        sshChannel.disconnect()
    }
}
