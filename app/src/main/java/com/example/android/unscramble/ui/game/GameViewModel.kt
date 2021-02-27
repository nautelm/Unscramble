package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private var wordList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    /* backing property
     * viewModel の中ではprivate var(viewModel内のみで編集可能) なプロパティがよい
     * しかし, viewModel外から参照したい場合public valにする必要がある
     * そこでgetterメソッドをoverrideする
     */

//    private var currentWordCount = 0
    private var _currentWordCount = 0
    val currentWordCount: Int
        get() = _currentWordCount

//    private var score = 0
    private var _score = 0
    val score: Int
        get() = _score

//    private var currentScrambledWord = "test"
    private lateinit var _currentScrambledWord: String
    val currentScrambledWord: String
        get() = _currentScrambledWord

    
    // initはインスタンス生成時に最も早く呼び出される
    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }


    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        // もとの単語と異なるまでシャッフル
        while(tempWord.toString().equals(currentWord, false)) {
            tempWord.shuffle()
        }
        // 同じ単語が出題されないようにチェック
        if(wordList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord = String(tempWord)
            ++_currentWordCount
            wordList.add(currentWord)
        }
    }

    // viewから呼び出す. 次の単語を生成する
    fun nextWord(): Boolean {
        return if(_currentWordCount < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else {
            false
        }
    }

    private fun increaseScore() {
        _score += SCORE_INCREASE
    }

    fun isUserWordCorrect(playerWord: String): Boolean {
        if(playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    fun reinitializeData() {
        _score = 0
        _currentWordCount = 0
        wordList.clear()
        getNextWord()
    }

    // viewModelが破棄される直前にonCleared()が呼び出される
    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }
}