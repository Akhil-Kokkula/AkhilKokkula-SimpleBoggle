package com.example.akhilkokkula_simpleboggle

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.akhilkokkula_simpleboggle.databinding.FragmentScoreBinding

class ScoreFragment : Fragment() {
    private var _binding: FragmentScoreBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private var listener: ScoreFragment.ScoreFragmentListener? = null
    private var totalScore: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        binding.newGameButton.setOnClickListener(newGameBtnClickListener)
        return binding.root
    }

    fun updateScore(score: Int) {
        if (score >= 0) {
            Toast.makeText(requireContext(), "That's correct +$score", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "That's incorrect $score", Toast.LENGTH_LONG).show()
        }
        if (totalScore + score < 0) {
            totalScore = 0
        } else {
            totalScore += score
        }
        val totalScoreStr = totalScore.toString()
        binding.scoreDisplay.text = "Score: $totalScoreStr"
    }

    fun newGameClick(view: View) {
        totalScore = 0
        val totalScoreStr = totalScore.toString()
        binding.scoreDisplay.text = "Score: $totalScoreStr"
        sendDataToActivity(true)
    }

    interface ScoreFragmentListener {
        fun onClickPassed(isClicked : Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ScoreFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ScoreFragmentListener")
        }
    }

    fun sendDataToActivity(isClicked: Boolean) {
        listener?.onClickPassed(isClicked)
    }

    private val newGameBtnClickListener = View.OnClickListener { view ->
        newGameClick(view)
    }


}