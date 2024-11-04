package com.muscleup.muscleup.ui.camera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import android.widget.Button
import com.muscleup.muscleup.R
import com.muscleup.muscleup.databinding.ActivityMainBinding
import com.muscleup.muscleup.ui.camera.fragment.CameraFragment

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Set up button click handler
        val defaults = findViewById<Button>(R.id.defaults)
        defaults.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment, CameraFragment(), "devices")
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
            finish()
        }
    }
}