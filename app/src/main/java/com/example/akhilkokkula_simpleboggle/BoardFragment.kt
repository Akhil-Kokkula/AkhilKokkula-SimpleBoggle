package com.example.akhilkokkula_simpleboggle

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.akhilkokkula_simpleboggle.databinding.FragmentBoardBinding
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class BoardFragment : Fragment() {
    private var _binding: FragmentBoardBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }
    private var listener: BoardFragmentListener? = null
    private lateinit var letters1: List<String>
    private lateinit var letters2: List<String>
    private var boardVal: Int = 1
    private lateinit var lettersClicked: MutableSet<String>
    private lateinit var wordsCorrectlySubmitted: MutableSet<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        lettersClicked = mutableSetOf()
        wordsCorrectlySubmitted = mutableSetOf()

        letters1 = listOf("c", "g", "b", "o", "o", "l", "n", "s", "a", "f", "o", "e", "i", "w", "m", "t")
        letters2 = listOf("i", "o", "j", "s", "h", "a", "e", "r", "l", "e", "d", "h", "g", "r", "a", "l")

        binding.clearBtn.setOnClickListener(clearBtnClickListener)
        binding.submitBtn.setOnClickListener(submitBtnClickListener)

        updateBoardLetters()



        return binding.root
    }

    private fun updateBoardLetters() {
        var lettersToUse: List<String>
        if (boardVal == 1) {
            lettersToUse = letters1
        } else {
            lettersToUse = letters2
        }

        val outerLayout = binding.outerLinearLayout
        var lettersIndex = 0
        for (i in 0 until outerLayout.childCount) {
            val child = outerLayout.getChildAt(i)
            if (child is LinearLayout) {
                for (j in 0 until child.childCount) {
                    val button = child.getChildAt(j)
                    if (button is Button) {
                        button.text = lettersToUse[lettersIndex]
                        button.setOnClickListener(letterBtnClickListener)
                        lettersIndex++
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun updateBoard(isClicked : Boolean) {
        if (isClicked) {
            if (boardVal == 1) {
                boardVal = 2
            } else if (boardVal == 2) {
                boardVal = 1
            }
        }

        updateBoardLetters()
    }

    fun letterClick(view: View) {
        val wordDisplay = binding.wordSelectedDisplay
        val currBtn = view as Button
        wordDisplay.append(currBtn.text.toString())
        currBtn.isEnabled = false
        currBtn.alpha = 0.5f
        lettersClicked.add(currBtn.tag.toString())
        enableAllBtnsExceptClicked()
        val currBtnTag : String = currBtn.tag.toString()
        val btnCoord = currBtnTag.split(",")
        println("current btn pressed is, $currBtnTag")
        val x = btnCoord[0].toInt()
        val y = btnCoord[1].toInt()
        val directions = arrayOf(
            intArrayOf(0, 1),
            intArrayOf(0, -1),
            intArrayOf(1, 0),
            intArrayOf(-1, 0),
            intArrayOf(1, 1),
            intArrayOf(-1, -1),
            intArrayOf(1, -1),
            intArrayOf(-1, 1)
        )

        var validBtnsSet = mutableSetOf<String>()
        for (direction in directions) {
            val newX = x + direction[0]
            val newY = y + direction[1]
            if (newX >= 0 && newX <= 3 && newY >= 0 && newY <= 3) {
                val coordStr = "$newX,$newY"
                validBtnsSet.add(coordStr)
            }
        }
        println("all valid btns are $validBtnsSet")
        disableInvalidBtns(validBtnsSet)
    }

    private fun disableInvalidBtns(validBtnsSet : MutableSet<String>) {
        val outerLayout = binding.outerLinearLayout
        for (row in 0 until 4) {
            for (col in 0 until 4) {
                val coordStr = "$row,$col"
                if (validBtnsSet.contains(coordStr)) {
                    continue
                }
                val rowLayout = outerLayout.getChildAt(row) as LinearLayout
                val btnToDisable = rowLayout.getChildAt(col) as Button
                btnToDisable.isEnabled = false
                btnToDisable.alpha = 0.5f
            }
        }
    }

    private fun enableAllBtnsExceptClicked() {
        val outerLayout = binding.outerLinearLayout
        for (row in 0 until 4) {
            for (col in 0 until 4) {
                val coordStr = "$row,$col"
                if (lettersClicked.contains(coordStr)) {
                    continue
                }
                val rowLayout = outerLayout.getChildAt(row) as LinearLayout
                val btnToEnable = rowLayout.getChildAt(col) as Button
                btnToEnable.isEnabled = true
                btnToEnable.alpha = 1.0f
            }
        }
    }

    private fun enableAllBtns() {
        val outerLayout = binding.outerLinearLayout
        for (row in 0 until 4) {
            for (col in 0 until 4) {
                val rowLayout = outerLayout.getChildAt(row) as LinearLayout
                val btnToEnable = rowLayout.getChildAt(col) as Button
                btnToEnable.isEnabled = true
                btnToEnable.alpha = 1.0f
            }
        }
    }

    private val letterBtnClickListener = View.OnClickListener { view ->
        letterClick(view)
    }

    private val clearBtnClickListener = View.OnClickListener { view ->
        clearBtnClick(view)
    }

    private val submitBtnClickListener = View.OnClickListener { view ->
        submitBtnClick(view)
    }

    fun clearBtnClick(view: View) {
        resetBoard()
    }

    private fun resetBoard() {
        enableAllBtns()
        binding.wordSelectedDisplay.text = ""
        lettersClicked = mutableSetOf()
    }

    fun submitBtnClick(view: View) {
        val searchWord = binding.wordSelectedDisplay.text.toString()
        var score = 0
        val numOfVowelsInSearchWord = numOfVowelsInWord(searchWord)

        if (searchWord.length < 4) {
            score = -10
        } else if (numOfVowelsInSearchWord <= 1) {
            score = -10
        } else if (!wordInDict(requireContext(), searchWord)) {
            score = -10
        } else if (wordsCorrectlySubmitted.contains(searchWord)) {
            score = -10
        } else {
            wordsCorrectlySubmitted.add(searchWord)
            score += searchWord.length - numOfVowelsInSearchWord
            score += (5 * numOfVowelsInSearchWord)
            if (numOfSpecialConsonants(searchWord) >= 1) {
                score = 2 * score
            }
        }

        sendDataToActivity(score)
        resetBoard()
    }

    interface BoardFragmentListener {
        fun onDataPassed(score: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BoardFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement BoardFragmentListener")
        }
    }

    fun sendDataToActivity(score: Int) {
        listener?.onDataPassed(score)
    }

    private fun wordInDict(context: Context, word : String) : Boolean {
        val inputStream : InputStream = context.assets.open("dictionaryfile.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            if (line?.contains(word) == true) {
                return true
            }
        }
        return false
    }

    private fun numOfSpecialConsonants(word : String) : Int {
        val specialConsonantsSet = setOf('s', 'z', 'p', 'x', 'q')
        var numOfSpecialConsonants = 0
        word.forEach { char ->
            if (specialConsonantsSet.contains(char)) {
                numOfSpecialConsonants++
            }
        }

        return numOfSpecialConsonants
    }

    private fun numOfVowelsInWord(word : String) : Int {
        val vowelsSet = setOf('a', 'e', 'i', 'o', 'u')
        var numOfVowels = 0
        word.forEach { char ->
            if (vowelsSet.contains(char)) {
                numOfVowels++
            }
        }

        return numOfVowels
    }

}