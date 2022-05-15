package com.zybooks.manyfacesoffrasier

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ToggleButton

const val SETTINGS_BOOL = "com.zybooks.manyfacesoffrasier.bool"

class Settings : AppCompatActivity() {
    lateinit var settingsToggleButton: ToggleButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        settingsToggleButton = findViewById(R.id.settingsToggleButton)
        settingsToggleButton.isChecked = intent.getBooleanExtra(SETTINGS_BOOL, false)


    }
    fun onfinishedClick(view : View) {
        var togBut = settingsToggleButton.isActivated
        var intent = Intent()

        intent.putExtra(SETTINGS_BOOL, settingsToggleButton.isChecked)
        setResult(RESULT_OK, intent)
        finish()
    }
}