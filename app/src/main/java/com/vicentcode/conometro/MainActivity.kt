package com.vicentcode.conometro

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Chronometer
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.vicentcode.conometro.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private lateinit var v: ActivityMainBinding

var crono: Job? = null
private lateinit var chronometer: Chronometer
class MainActivity : AppCompatActivity() , CoroutineScope {

    private var running = false
    private var time = 0L
    val timesHistory = ArrayList<String>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v = ActivityMainBinding.inflate(layoutInflater)
        val view = v.root
        setContentView(view)


        v.tvCrono.text = getString(R.string.zeroCrono)
        val tiempos = intent.getStringExtra("tiempos")
        if (tiempos != null) {
            running = !running
            if (running) {
                stop()
            } else {
                stop()
            }
            v.tvCrono.text = tiempos
            time = parseTimeToMillis(tiempos)
        }


        if (timesHistory!!.isEmpty()){
            v.tvList.text = getString(R.string.emptyList)
            v.fabSend.isVisible = false
        }

        v.fabReset.setOnClickListener {
            time = 0L
            v.tvCrono.text = getString(R.string.zeroCrono)
        }

        v.fabSave.setOnClickListener {
            v.tvList.text=""
            v.fabSend.isVisible = true
            timesHistory.add(v.tvCrono.text.toString())
            val adapter = ArrayAdapter(this, R.layout.list_pref , timesHistory!!.toMutableList())
            v.listVPre.adapter = adapter
        }
        v.fabSave.setOnLongClickListener {
            timesHistory.clear()
            v.fabSend.isVisible = false
            v.tvList.text = getString(R.string.emptyList)
            val adapter = ArrayAdapter(this, R.layout.list_pref , timesHistory!!.toMutableList())
            v.listVPre.adapter = adapter
            val snackbar = Snackbar.make(view, "Lista vaciada", Snackbar.LENGTH_SHORT).show()
            true
        }

        v.fabSend.setOnClickListener {
            val intent = Intent(this@MainActivity, listClock::class.java)
            val vector = timesHistory.map { it.toString() }.toTypedArray()
            intent.putExtra("times", vector)
            stop()
            startActivity(intent)
            finish()
        }

        v.fabPlay.setOnClickListener {
            running = !running
            if (running) {
                start()
            } else {
                stop()

            }
        }

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        stop()
        finish()
    }

    private fun start() {
        v.fabPlay.setImageResource(R.drawable.pause)
        val coroutine = launch(Dispatchers.Main) {
            while (running) {
                time += 100
                v.tvCrono.text = formatTime(time)
                delay(100)
            }
        }
    }


    private fun stop() {
        v.fabPlay.setImageResource(R.drawable.play)
        running = false

        if (crono != null && crono!!.isActive) {
            crono!!.cancel()
        }
    }

    private fun parseTimeToMillis(timeString: String): Long {
        val parts = timeString.split(":")
        if (parts.size == 3) {
            try {
                val minutes = parts[0].toInt()
                val seconds = parts[1].toInt()
                val milliseconds = parts[2].toInt()
                return (minutes * 60 * 1000 + seconds * 1000 + milliseconds * 10).toLong()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, getString(R.string.errorTimeRegister), Toast.LENGTH_SHORT).show()
            }
        }
        return 0
    }


    private fun formatTime(time: Long): String {
        val minutes = time / (1000 * 60)
        val seconds = (time / 1000) % 60
        val milliseconds = time % 1000

        return String.format("%02d:%02d:%02d", minutes, seconds, milliseconds / 10)
    }





}