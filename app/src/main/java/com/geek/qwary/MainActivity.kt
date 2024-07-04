package com.geek.qwary

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.qwary.Qwary

class MainActivity : AppCompatActivity() {

    private val appId = "c5e3e8c3-5b12-481d-a4c9-1570bd532860"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Qwary.configure(this, appId)
        Qwary.addEvent("T29_04_1")
        Qwary.addEvent("HomePageVisitor")
        Qwary.addEvent("T29_04_2")
        Qwary.addEvent("NoCode")
        Qwary.addEvent("T29_04_3")

        findViewById<Button>(R.id.event).setOnClickListener {
            Qwary.addEvent("T03_05_1")
        }
        findViewById<Button>(R.id.logout).setOnClickListener {
            Qwary.logout()
        }
    }

}