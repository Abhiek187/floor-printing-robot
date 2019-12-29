package com.example.linuxtest

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.jcraft.jsch.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val widths = arrayListOf(8f,10f,12f,14f,16f,18f,20f)
    private val colors = arrayListOf(Color.BLACK,Color.RED,Color.rgb(255,165,0),Color.YELLOW,Color.GREEN,Color.BLUE,Color.rgb(128,0,128),Color.rgb(165,42,42),Color.WHITE)
    private val colNames = arrayListOf("Black","Red","Orange","Yellow","Green","Blue","Purple","Brown","White")
    var curWidth = 8f
    var curColor = Color.BLACK
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI elements
        val pageLayout = findViewById<ConstraintLayout>(R.id.pageLayout)
        val buttonUpload = findViewById<Button>(R.id.buttonUpload)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonLoad = findViewById<Button>(R.id.buttonLoad)
        val buttonPrint = findViewById<Button>(R.id.buttonPrint)
        val clear = findViewById<Button >(R.id.clear)
        val line1 = findViewById<View>(R.id.line1)
        val line2 = findViewById<View>(R.id.line2)
        val spin1 = findViewById<Spinner>(R.id.brushWidth)
        val spin2 = findViewById<Spinner>(R.id.colors)

        // Load content from JSON
        val json = JSONObject(assets.open("google-services.json").bufferedReader()
            .readText())
        val username = json.getString("username")
        val password = json.getString("password")
        val hostname = json.getString("hostname") // change if static IP address changes

        val imagesDB = ImagesDBHelper(this)

        val  limits = ConstraintSet()
        val newView = CustomDraw(this)


        val infoWidth = ArrayAdapter(this,android.R.layout.simple_list_item_1,widths)
        infoWidth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin1.adapter=infoWidth

        spin1.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                curWidth=widths[pos]
                newView.upDatePaint(curWidth,curColor)
            }
        }

        val infoColors = ArrayAdapter(this,android.R.layout.simple_list_item_1,colNames)
        infoColors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spin2.adapter=infoColors

        spin2.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                curColor=colors[pos]
                newView.upDatePaint(curWidth,curColor)
            }
        }
        buttonUpload.setOnClickListener {
            // Get photo from library
            println("Uploading...")
        }

        buttonSave.setOnClickListener {
            // Save image to database
            val randNum = (0..100).random()
            imagesDB.addImage("Name #$randNum", "Image #$randNum")
            Toast.makeText(this, "Saved Image #$randNum", Toast.LENGTH_SHORT).show()
        }

        buttonLoad.setOnClickListener {
            // Load saved image
            val intent = Intent(this, SavesActivity::class.java)
            startActivity(intent)
        }

        buttonPrint.setOnClickListener {
            // Send image for printing on the pi
            if (!isOnline()) {
                Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            thread {
                ssh(username, password, hostname)
            }
        }
        pageLayout.addView(newView)
        newView.id=View.generateViewId()
        newView.layoutParams.height=0
        /*val parms = newView.layoutParams as ConstraintLayout.LayoutParams
        parms.topToBottom = clear.id
        parms.bottomToTop = buttonUpload.id
        newView.requestLayout()*/
        limits.clone(pageLayout)
        limits.connect(newView.id, ConstraintSet.TOP,line1.id,ConstraintSet.BOTTOM,1)
        limits.connect(newView.id,ConstraintSet.BOTTOM,line2.id,ConstraintSet.TOP,1)
        limits.applyTo(pageLayout)

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

            val image = "'King Dinga.jpg'" // 60.3 KB = 13:11 (~76 bytes/s)
            /*sftp(session, src = "${this.filesDir.path}/NewTextFile.txt",
                dest = "./floor*") // /data/user/0/com.example.linuxtest/files*/
            execute(session, command = "python3 lol.py")
            execute(session, command = "cd floor* && python3 img_info.py $image")

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
