package com.iseven.alcosafe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ToggleButton

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sexToggle = findViewById<ToggleButton>(R.id.sexToggle)
        val permisToggle = findViewById<ToggleButton>(R.id.permisToggle)
        val poidsEdit = findViewById<EditText>(R.id.poidsEdit)
        val homeButton = findViewById<ImageButton>(R.id.home)

        sexToggle.isChecked = sharedPreferences.getBoolean("homme", true)
        poidsEdit.setText(sharedPreferences.getInt("poids", 75).toString())
        permisToggle.isChecked = sharedPreferences.getBoolean("permisDef", true)

        homeButton.setOnClickListener {
            val backHome = Intent(this, MainActivity::class.java)
            startActivity(backHome)
        }

        sexToggle.setOnClickListener {
            sharedEditor?.putBoolean("homme", sexToggle.isChecked)
            sharedEditor?.commit()
        }

        permisToggle.setOnClickListener {
            sharedEditor?.putBoolean("permisDef", permisToggle.isChecked)
            sharedEditor?.commit()
        }

        poidsEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sharedEditor?.putInt("poids", poidsEdit.text.toString().toInt())
                sharedEditor?.commit()
            }
            false
        }

        poidsEdit.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                sharedEditor?.putInt("poids", poidsEdit.text.toString().toInt())
                sharedEditor?.commit()
            }
        }



    }
}