package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    /* backing property
     * viewModel の中ではprivate var(viewModel内のみで編集可能) なプロパティがよい
     * しかし, viewModel外から参照したい場合public valにする必要がある
     * そこでgetterメソッドをoverrideする
     */

    /* LiveData
     * observerでLiveDataObjectの値の変化を監視する
     * observerはアクティブなライフサイクル(started/resumed)の時にnotifyする
     * MutableLiveData/LiveData<T>()
     * ライブデータの値へのアクセスは(object).valueで行う
     *
     */

//    private var currentWordCount = 0
    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

//    private var score = 0
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

//    private var currentScrambledWord = "test"
    // LiveDataの導入
    private val _currentScrambledWord = MutableLiveData<String>()
    // talkBackの導入(読み上げ機能)
//    val currentScrambledWord: LiveData<String>
//    get() = _currentScrambledWord
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    private var wordList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String
    
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
            _currentScrambledWord.value = String(tempWord)
//            ++_currentWordCount.
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordList.add(currentWord)

        }
    }

    // viewから呼び出す. 次の単語を生成する
    fun nextWord(): Boolean {
        return if(_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else {
            false
        }
    }

    private fun increaseScore() {
//        _score += SCORE_INCREASE
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    fun isUserWordCorrect(playerWord: String): Boolean {
        if(playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordList.clear()
        getNextWord()
    }

    // viewModelが破棄される直前にonCleared()が呼び出される
//    override fun onCleared() {
//        super.onCleared()
//        Log.d("GameFragment", "GameViewModel destroyed!")
//    }
}