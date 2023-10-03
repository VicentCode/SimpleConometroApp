package com.vicentcode.conometro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.vicentcode.conometro.databinding.ActivityListClockBinding

private lateinit var v: ActivityListClockBinding
class listClock : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        v = ActivityListClockBinding.inflate(layoutInflater)
        val view = v.root
        setContentView(view)

        val history = intent.getStringArrayExtra("times")
        val adapter = ArrayAdapter(this, android.R.layout.select_dialog_singlechoice , history!!.toMutableList())
        v.lvHistory .adapter = adapter
        v.lvHistory.choiceMode = ListView.CHOICE_MODE_SINGLE

        v.lvHistory.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = adapter.getItem(position) as String

                val intent = Intent(this@listClock, MainActivity::class.java)
                intent.putExtra("tiempos", item)
                finish()

                startActivity(intent)
            }
        }
    }
}