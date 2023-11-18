package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel que contiene los datos de la aplicación y métodos para procesar los datos.
 */
class GameViewModel : ViewModel() {

    // Estado de la interfaz de usuario del juego
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    var userGuess by mutableStateOf("")
        private set

    // Conjunto de palabras utilizadas en el juego
    private var usedWords: MutableSet<String> = mutableSetOf()
    private lateinit var currentWord: String

    init {
        resetGame()
    }

    /*
     * Vuelve a inicializar los datos del juego para reiniciar el juego.
     */
    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    /*
     * Actualiza la suposición del usuario.
     */
    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    /*
     * Verifica si la suposición del usuario es correcta.
     * Aumenta la puntuación en consecuencia.
     */
    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            // La suposición del usuario es correcta, aumenta la puntuación
            // y llama a updateGameState() para preparar el juego para la siguiente ronda
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            // La suposición del usuario es incorrecta, muestra un error
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        // Reinicia la suposición del usuario
        updateUserGuess("")
    }

    /*
     * Saltar a la siguiente palabra
     */
    fun skipWord() {
        updateGameState(_uiState.value.score)
        // Reinicia la suposición del usuario
        updateUserGuess("")
    }

    /*
     * Elige una nueva currentWord y currentScrambledWord y actualiza UiState según
     * el estado actual del juego.
     */
    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            // Última ronda en el juego, actualiza isGameOver a true, no elijas una nueva palabra
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else {
            // Ronda normal en el juego
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    currentWordCount = currentState.currentWordCount.inc(),
                    score = updatedScore
                )
            }
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // Baraja la palabra
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    private fun pickRandomWordAndShuffle(): String {
        // Continúa eligiendo una nueva palabra al azar hasta que obtienes una que no se ha utilizado antes
        currentWord = allWords.random()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }
}
