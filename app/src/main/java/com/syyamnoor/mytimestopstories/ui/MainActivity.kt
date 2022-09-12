package com.syyamnoor.mytimestopstories.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.syyamnoor.mytimestopstories.R
import com.syyamnoor.mytimestopstories.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}