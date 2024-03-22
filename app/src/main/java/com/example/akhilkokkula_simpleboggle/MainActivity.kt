package com.example.akhilkokkula_simpleboggle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), BoardFragment.BoardFragmentListener, ScoreFragment.ScoreFragmentListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onDataPassed(score: Int) {
        val fragmentScore = supportFragmentManager.findFragmentById(R.id.fragmentScore) as ScoreFragment?
        fragmentScore?.updateScore(score)
    }

    override fun onClickPassed(isClicked: Boolean) {
        val fragmentBoard = supportFragmentManager.findFragmentById(R.id.fragmentBoard) as BoardFragment?
        fragmentBoard?.updateBoard(isClicked)
    }


}