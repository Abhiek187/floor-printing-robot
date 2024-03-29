package com.example.linuxtest.activities

import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import com.example.linuxtest.databinding.ActivityPrintBinding
import com.example.linuxtest.image.Image
import com.example.linuxtest.storage.Prefs
import com.jcraft.jsch.*
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.concurrent.thread

class PrintActivity : AppCompatActivity() {
    private lateinit var textViewLog: TextView
    private lateinit var imageViewPrint: ImageView
    private lateinit var currentImage: Image
    private var didFinish = true
    private var prevOutput = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        textViewLog = binding.textViewLog
        textViewLog.text = ""
        imageViewPrint = binding.imageViewPrint

        // Load pi info from SharedPreferences
        val sharedPref = Prefs(this)
        val serverName = sharedPref.serverName
        val serverPassword = sharedPref.serverPassword
        val serverHost = sharedPref.serverHostname
        /*val username = sharedPref.username
        val password = sharedPref.password
        val hostname = sharedPref.hostname*/

        currentImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("image", Image::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("image")!!
        }

        thread {
            serverConnect(serverName, serverHost, serverPassword)
            //ssh(username, password, hostname)
        }
    }

    private fun serverConnect(serverName: String, serverHost: String, serverPassword: String) {
        try {
            val jsch = JSch()
            val session = jsch.getSession(serverName, serverHost)
            session.setPassword(serverPassword)
            session.setPortForwardingL(4755,"localhost",4755)

            // Avoid asking for key confirmation
            val properties = Properties()
            properties["StrictHostKeyChecking"] = "no"
            session.setConfig(properties)

            uiPrint("Connecting to $serverHost...")
            session.connect(3000) // timeout after 3 seconds

            val sharedPref = Prefs(this)
            val username = sharedPref.username
            val password = sharedPref.password
            //val hostname = sharedPref.hostname
            val hostname = "localhost" // localhost is the server

            ssh(username, password, hostname, 4755)

            uiPrint("Disconnecting from $serverHost...")
            session.disconnect()
        } catch (ex: JSchException) {
            ex.printStackTrace()
            uiPrint("Couldn't connect to $serverHost")
        }
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

            uiPrint("Connecting to $hostname...")
            session.connect(3000) // timeout after 3 seconds

            val imagePath = currentImage.uri.toUri().path // 60.3 KB = 13:11 (~76 bytes/s)
            println("image URI = ${currentImage.uri}")
            println("image path = $imagePath")
            sftpPut(session, src = "${this.filesDir.path}/${currentImage.name}", dest = "./floor*")
            execute(session, command = "cd floor*; python3 img_info.py '${currentImage.name}' &> out.log")
            sftpGet(session, src = "./floor-printing-robot/new_scaled_${currentImage.name}", dest = this.filesDir.path)

            uiPrint("Disconnecting from $hostname...")
            session.disconnect()

            // Show printed image
            val filePath = "${this.filesDir.path}/new_scaled_${currentImage.name}.png"
            val bitmap = BitmapFactory.decodeFile(filePath)
            runOnUiThread {
                imageViewPrint.setImageBitmap(bitmap)
                imageViewPrint.contentDescription = "A print of ${currentImage.name}"
            }
        } catch (ex: JSchException) {
            ex.printStackTrace()
            uiPrint("Couldn't connect to $hostname")
        }

        runOnUiThread {
            Toast.makeText(this, "Print job finished!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sftpPut(session: Session, src: String, dest: String) {
        // Transfer files from the app to the pi
        val sftpChannel = session.openChannel("sftp") as ChannelSftp
        sftpChannel.connect()
        sftpChannel.put(src, dest)
        uiPrint("Transferred file from $src to $dest\n")
        sftpChannel.disconnect()
    }

    private fun sftpGet(session: Session, src: String, dest: String) {
        // Transfer files from the pi to the app
        val sftpChannel = session.openChannel("sftp") as ChannelSftp
        sftpChannel.connect()
        sftpChannel.get(src, dest)
        uiPrint("Transferred file from $src to $dest")
        sftpChannel.disconnect()
    }

    private fun execute(session: Session, command: String) {
        // Create an SSH Channel
        val sshChannel = session.openChannel("exec") as ChannelExec
        /*val outputStream = ByteArrayOutputStream()
        val errStream = ByteArrayOutputStream()
        sshChannel.outputStream = outputStream
        sshChannel.setExtOutputStream(errStream)*/

        // Execute Linux command
        sshChannel.setCommand(command)
        sshChannel.connect()
        uiPrint("Output from $command:")
        didFinish = false

        thread {
            while (!didFinish) {
                getOutput(session, command = "cat floor*/out.log")
            }
        }

        // Wait for command to finish
        while (!sshChannel.isClosed) {
            Thread.sleep(1_000)
        }

        didFinish = true
        uiPrint("") // just a newline

        // Check if program succeeded or failed
        /*if (sshChannel.exitStatus == 0) {
            uiPrint(outputStream.toString()) // success: print stdout
        } else {
            uiPrint(errStream.toString()) // error: print stderr
        }*/

        sshChannel.disconnect()
    }

    private fun getOutput(session: Session, command: String) {
        // Get output of command in real time
        val sshChannel = session.openChannel("exec") as ChannelExec
        val outputStream = ByteArrayOutputStream()
        sshChannel.outputStream = outputStream

        // Execute Linux command
        sshChannel.setCommand(command)
        sshChannel.connect()

        // Wait for command to finish
        while (!sshChannel.isClosed) {
            Thread.sleep(1_000)
        }

        // Check if output has changed, and print what's newly added
        if (outputStream.toString() != prevOutput) {
            val newOutput = outputStream.toString().substringAfter(prevOutput)
            runOnUiThread { textViewLog.append(newOutput) } // success: print stdout (no extra \n)
            prevOutput = outputStream.toString()
        }

        sshChannel.disconnect()
    }

    private fun uiPrint(str: String) {
        runOnUiThread { textViewLog.append("$str\n") }
    }
}
