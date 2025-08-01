package com.example.kotlingraphicsandroid6

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.concseqarcdownview.ConcArcSeqDownView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConcArcSeqDownView.create(this)
    }
}