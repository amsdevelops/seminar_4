package com.example.seminar_4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.seminar_4.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val scope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            job?.cancel()
            job = scope.launch {
                createSpeedometerValues()
                    .map {
                        if (binding.metricsswitch.isChecked) {
                            (it / 1.6).toInt().toString() + getString(R.string.mph)
                        } else {
                            it.toString() + getString(R.string.kmh)
                        }
                    }
                    .collect {
                        withContext(Dispatchers.Main) {
                            binding.speedometer.text = it
                        }
                    }
            }
        }
    }

    fun createSpeedometerValues() = flow {
        var isAccelerate = true
        var speed = 0
        while (true) {
            Thread.sleep(Random.nextLong(500))
            if (isAccelerate) {
                speed++
                if (speed == 100) isAccelerate = false
            } else {
                speed--
                if (speed == 0) isAccelerate = true
            }
            emit(speed)
        }
    }
}