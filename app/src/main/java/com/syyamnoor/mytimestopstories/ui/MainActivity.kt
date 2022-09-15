package com.syyamnoor.mytimestopstories.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.syyamnoor.mytimestopstories.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}