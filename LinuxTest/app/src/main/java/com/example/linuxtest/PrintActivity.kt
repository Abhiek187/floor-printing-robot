package com.example.linuxtest

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.jcraft.jsch.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.concurrent.thread

class PrintActivity : AppCompatActivity() {
    private lateinit var textViewLog: TextView
    private lateinit var imageViewPrint: ImageView
    private lateinit var currentImgName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print)
        
        textViewLog = findViewById(R.id.textViewLog)
        textViewLog.text = ""
        imageViewPrint = findViewById(R.id.imageViewPrint)

        // Load content from JSON
        val json = JSONObject(assets.open("google-services.json").bufferedReader()
            .readText())
        val username = json.getString("username")
        val password = json.getString("password")
        val hostname = json.getString("hostname")
        currentImgName = intent.getStringExtra("imageName")!!

        thread {
            ssh(username, password, hostname)
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
            session.connect()

            val image = "scaled_$currentImgName.png" // 60.3 KB = 13:11 (~76 bytes/s)
            sftpPut(session, src = "${this.filesDir.path}/$image", dest = "./floor*")
            execute(session, command = "python3 lol.py")
            execute(session, command = "cd floor* && python3 img_info.py '$image'")
            sftpGet(session, src = "./floor-printing-robot/new_$image", dest = this.filesDir.path)

            uiPrint("Disconnecting from $hostname...")
            session.disconnect()

            // Show printed image
            val filePath = "${this.filesDir.path}/new_scaled_$currentImgName.png"
            val bitmap = BitmapFactory.decodeFile(filePath)
            runOnUiThread {
                imageViewPrint.setImageBitmap(bitmap)
                imageViewPrint.contentDescription = "A print of $currentImgName"
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
        uiPrint("Transferred file from $src to $dest")
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
        val inputStream = sshChannel.inputStream
        val buffer = ByteArray(1024)
        val outputStream = ByteArrayOutputStream()
        val errStream = ByteArrayOutputStream()
        sshChannel.outputStream = outputStream
        sshChannel.setExtOutputStream(errStream)

        // Execute Linux command
        sshChannel.setCommand(command)
        sshChannel.connect()
        uiPrint("Output from $command:")

        // Wait for command to finish
        while (!sshChannel.isClosed) {
            /*while (inputStream.available() > 0) {
                val i = inputStream.read(buffer, 0, 1024)
                if (i < 0) break
                uiPrint(String(buffer, 0, i))
            }*/

            Thread.sleep(1_000)
        }

        // Check if program succeeded or failed
        if (sshChannel.exitStatus == 0) {
            uiPrint(outputStream.toString()) // success: print stdout
        } else {
            uiPrint(errStream.toString()) // error: print stderr
        }

        sshChannel.disconnect()
    }

    private fun uiPrint(str: String) {
        runOnUiThread { textViewLog.append("$str\n") }
    }
}
