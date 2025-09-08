package com.example.kotlingraphicsandroid6

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.concarctodownview.ConcArcToDownView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConcArcToDownView.create( this)
    }
}