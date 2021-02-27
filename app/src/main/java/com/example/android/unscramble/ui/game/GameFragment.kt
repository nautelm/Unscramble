/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment where the game is played, contains the game logic.
 */

/* Android Architecture Component(Separation of concerns/ Drive UI from a model)
 *
 * MVVM
 * View(UI controller : activity/fragment)はView・Dataの表示やuser interactionのみ扱う
 * viewModelではUI controllerが必要とするデータの処理(計算, データの取得など)を行う
 *
 * DataBinding
 * binding data from code to views + view binding (binding views to code)
 * Example using view binding in UI controller
 *      binding.textViewUnscrambledWord.text = viewModel.currentScrambledWord
 * 
 * Example using data binding in layout file
 *      android:text="@{gameViewModel.currentScrambledWord}"
 */

class GameFragment : Fragment() {

    // viewModelをfragment(UI controller)に紐づける
    // byで委譲する(拡張機能に集中するため, 基本のロジックを任せる)
    private val viewModel: GameViewModel by viewModels()

    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    // Create a ViewModel the first time the fragment is created.
    // If the fragment is re-created, it receives the same GameViewModel instance created by the
    // first fragment

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
//        binding = GameFragmentBinding.inflate(inflater, container, false)
        // change viewBinding to dataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        
        Log.d("GameFragment", "GameFragment created/re-created!")
        Log.d("GameFragment", "Word: ${viewModel.currentScrambledWord} " +
                "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}")
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // dataBinding 変数の初期化
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS

        // ライフサイクルを監視する
        binding.lifecycleOwner = viewLifecycleOwner
        // Setup a click listener for the Submit and Skip buttons.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }

        // attach an observer for currentScrambledWord(LiveData)
        // 第1引数はviewLifecycleOwner : Activity/Fragmentがアクティブかどうかを見る
        // 第2引数はラムダ式(newWordを引数にとって, その単語を画面に反映する)
        // dataBindingで監視するので不要
//        viewModel.currentScrambledWord.observe(viewLifecycleOwner,
//            { newWord -> binding.textViewUnscrambledWord.text = newWord})
        
        // Update the UI
//        updateNextWordOnScreen()
//        binding.score.text = getString(R.string.score, 0)
//        binding.wordCount.text = getString(
//            R.string.word_count, 0, MAX_NO_OF_WORDS)

//        viewModel.score.observe(viewLifecycleOwner,
//            { newScore -> binding.score.text = getString(R.string.score, newScore) })
//
//        viewModel.currentWordCount.observe(viewLifecycleOwner,
//            { newWordCount -> binding.wordCount.text =
//                getString(R.string.word_count, newWordCount, MAX_NO_OF_WORDS)}
//        )

    }
    
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if(viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if(viewModel.nextWord()) {
//                updateNextWordOnScreen()
            } else {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    private fun onSkipWord() {
        if(viewModel.nextWord()) {
            setErrorTextField(false)
//            updateNextWordOnScreen()
        } else {
            showFinalScoreDialog()
        }
    }
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false) // バックキーを押されてもキャンセルしないようにする
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }
//    override fun onDetach() {
//        super.onDetach()
//        Log.d("GameFragment", "GameFragment destroyed!")
//    }

//    private fun getNextScrambledWord(): String {
//        val tempWord = allWordsList.random().toCharArray()
//        tempWord.shuffle()
//        return String(tempWord)
//    }

    /*
     * Re-initializes the data in the ViewModel and updates the views with the new data, to
     * restart the game.
     */
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
//        updateNextWordOnScreen()
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
    * Sets and resets the text field error status.
    */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

//    /*
//     * Displays the next scrambled word on screen.
//     */
//    private fun updateNextWordOnScreen() {
//        binding.textViewUnscrambledWord.text = viewModel.currentScrambledWord
//    }
}
