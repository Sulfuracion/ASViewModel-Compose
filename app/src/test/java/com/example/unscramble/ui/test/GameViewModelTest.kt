package com.example.unscramble.ui.test

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
//  import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.testng.Assert.assertNotEquals

class GameViewModelTest {
    private val viewModel = GameViewModel()

    // Prueba para verificar que la inicialización del ViewModel carga la primera palabra correctamente.
    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {
        val gameUiState = viewModel.uiState.value
        val unScrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)

        // Asegura que la palabra actual esté desordenada.
        assertNotEquals(unScrambledWord, gameUiState.currentScrambledWord)
        // Asegura que el contador de palabras actuales esté configurado en 1.
        assertTrue(gameUiState.currentWordCount == 1)
        // Asegura que inicialmente la puntuación sea 0.
        assertTrue(gameUiState.score == 0)
        // Asegura que la suposición incorrecta de la palabra sea falsa.
        assertFalse(gameUiState.isGuessedWordWrong)
        // Asegura que el juego no haya terminado.
        assertFalse(gameUiState.isGameOver)
    }

    // Prueba para verificar que la suposición incorrecta establece correctamente la bandera de error.
    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        // Dada una palabra incorrecta como entrada
        val incorrectPlayerWord = "and"

        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()

        val currentGameUiState = viewModel.uiState.value
        // Asegura que la puntuación no cambie
        assertEquals(0, currentGameUiState.score)
        // Asegura que el método checkUserGuess() actualice isGuessedWordWrong correctamente.
        assertTrue(currentGameUiState.isGuessedWordWrong)
    }

    // Prueba para verificar que la suposición correcta actualiza la puntuación y la bandera de error se desactiva.
    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()
        currentGameUiState = viewModel.uiState.value

        // Asegura que el método checkUserGuess() actualice isGuessedWordWrong correctamente.
        assertFalse(currentGameUiState.isGuessedWordWrong)
        // Asegura que la puntuación se actualice correctamente.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    // Prueba para verificar que al saltar una palabra, la puntuación permanece sin cambios y el contador de palabras aumenta.
    @Test
    fun gameViewModel_WordSkipped_ScoreUnchangedAndWordCountIncreased() {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()
        currentGameUiState = viewModel.uiState.value
        val lastWordCount = currentGameUiState.currentWordCount

        viewModel.skipWord()
        currentGameUiState = viewModel.uiState.value
        // Asegura que la puntuación permanezca sin cambios después de saltar la palabra.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
        // Asegura que el contador de palabras aumente en 1 después de saltar la palabra.
        assertEquals(lastWordCount + 1, currentGameUiState.currentWordCount)
    }

    // Prueba para verificar que todos los intentos correctos actualizan correctamente el estado de la interfaz de usuario.
    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        repeat(MAX_NO_OF_WORDS) {
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            currentGameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
            // Asegura que después de cada respuesta correcta, la puntuación se actualice correctamente.
            assertEquals(expectedScore, currentGameUiState.score)
        }
        // Asegura que después de responder todas las preguntas, el contador de palabras actuales esté actualizado.
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        // Asegura que después de responder 10 preguntas, el juego haya terminado.
        assertTrue(currentGameUiState.isGameOver)
    }

    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }
}
