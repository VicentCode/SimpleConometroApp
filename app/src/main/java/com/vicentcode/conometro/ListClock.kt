package com.vicentcode.conometro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.vicentcode.conometro.databinding.ActivityListClockBinding

private lateinit var v: ActivityListClockBinding

class ListClock : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v = ActivityListClockBinding.inflate(layoutInflater)
        setContentView(v.root)

        val history = intent.getStringArrayExtra("times")

        setupListView(history)
    }

    private fun setupListView(history: Array<String>?) {
        val adapter = ArrayAdapter(this, android.R.layout.select_dialog_singlechoice, history.orEmpty().toMutableList())
        v.lvHistory.adapter = adapter
        v.lvHistory.choiceMode = ListView.CHOICE_MODE_SINGLE

        v.lvHistory.setOnItemClickListener { parent, view, position, id ->
            val item = adapter.getItem(position) as String

            val intent = Intent(this@ListClock, MainActivity::class.java)
            intent.putExtra("tiempos", item)
            finish()
            startActivity(intent)
        }
    }
}
