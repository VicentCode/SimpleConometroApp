package com.vicentcode.conometro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.vicentcode.conometro.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var v: ActivityMainBinding
    private var running = false
    private var time = 0L
    private val timesHistory = ArrayList<String>()

    private var crono: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v = ActivityMainBinding.inflate(layoutInflater)
        val view = v.root
        setContentView(view)

        setupViews()

        val tiempos = intent.getStringExtra("tiempos")
        if (tiempos != null) {
            toggleChronometerState()
            v.tvCrono.text = tiempos

            //delete this section for chronometer start automatically to start activity
            running = !running
            stopChronometer()
            //

            time = parseTimeToMillis(tiempos)
        }

        if (timesHistory.isEmpty()) {
            v.tvList.text = getString(R.string.emptyList)
            v.fabSend.isVisible = false
        }
    }

    private fun setupViews() {
        v.tvCrono.text = getString(R.string.zeroCrono)

        v.fabReset.setOnClickListener {
            resetChronometer()
        }

        v.fabSave.setOnClickListener {
            saveTimeRecord()
        }

        v.fabSave.setOnLongClickListener {
            clearTimeHistory()
        }

        v.fabSend.setOnClickListener {
            sendTimeHistory()
        }

        v.fabPlay.setOnClickListener {
            toggleChronometerState()
        }
    }

    override fun onBackPressed() {
        stopChronometer()
        finish()
    }

    private fun toggleChronometerState() {
        running = !running
        if (running) {
            startChronometer()
        } else {
            stopChronometer()
        }
    }

    private fun startChronometer() {
        v.fabPlay.setImageResource(R.drawable.pause)
        crono = launch(Dispatchers.Main) {
            while (running) {
                time += 100
                v.tvCrono.text = formatTime(time)
                delay(100)
            }
        }
    }

    private fun stopChronometer() {
        v.fabPlay.setImageResource(R.drawable.play)
        running = false

        crono?.cancel()
    }

    private fun resetChronometer() {
        time = 0L
        v.tvCrono.text = getString(R.string.zeroCrono)
    }

    private fun saveTimeRecord() {
        v.tvList.text = ""
        v.fabSend.isVisible = true
        timesHistory.add(v.tvCrono.text.toString())
        val adapter = ArrayAdapter(this, R.layout.list_pref, timesHistory.toMutableList())
        v.listVPre.adapter = adapter
    }

    private fun clearTimeHistory(): Boolean {
        timesHistory.clear()
        v.fabSend.isVisible = false
        v.tvList.text = getString(R.string.emptyList)
        val adapter = ArrayAdapter(this, R.layout.list_pref, timesHistory.toMutableList())
        v.listVPre.adapter = adapter
        Snackbar.make(v.root, getString(R.string.ListClear), Snackbar.LENGTH_SHORT).show()
        return true
    }

    private fun sendTimeHistory() {
        val intent = Intent(this@MainActivity, ListClock::class.java)
        val vector = timesHistory.map { it }.toTypedArray()
        intent.putExtra("times", vector)
        stopChronometer()
        startActivity(intent)
        finish()
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
