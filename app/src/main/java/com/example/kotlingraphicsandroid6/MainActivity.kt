package com.example.kotlingraphicsandroid6

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.rightarccreateview.RightArcCreateView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RightArcCreateView.create( this)
    }
}