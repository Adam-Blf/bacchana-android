package com.beloucif.lataverne.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.beloucif.lataverne.content.PackRepository
import com.beloucif.lataverne.core.GameMode
import com.beloucif.lataverne.core.Player
import com.beloucif.lataverne.core.PromptDraw
import com.beloucif.lataverne.core.PromptSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PromptUiState {
    data object Loading : PromptUiState
    data object Empty : PromptUiState
    data class Ready(
        val draw: PromptDraw,
        val activeRules: List<com.beloucif.lataverne.core.ActiveRule>,
        val hasNext: Boolean,
    ) : PromptUiState
    data object Finished : PromptUiState
}

/** Drives one generic prompt-mode session (picolo, action-verite, etc.) from bundled packs. */
class PromptViewModel(private val packRepository: PackRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<PromptUiState>(PromptUiState.Loading)
    val uiState: StateFlow<PromptUiState> = _uiState.asStateFlow()

    private var session: PromptSession? = null

    fun start(mode: GameMode, players: List<Player>) {
        _uiState.value = PromptUiState.Loading
        viewModelScope.launch {
            val packs = packRepository.loadFreePacksForMode(mode)
            val items = packs.flatMap { it.items }
            if (items.isEmpty() || players.size < 2) {
                _uiState.value = PromptUiState.Empty
                return@launch
            }
            session = PromptSession(items, players)
            drawInternal(advanceFirst = false)
        }
    }

    /** Advances to the next player and draws their prompt. Call from the "Suivant" button. */
    fun drawNext() = drawInternal(advanceFirst = true)

    private fun drawInternal(advanceFirst: Boolean) {
        val current = session ?: return
        if (advanceFirst) current.advanceTurn()
        if (!current.hasNext()) {
            _uiState.value = PromptUiState.Finished
            return
        }
        val draw = current.drawNext()
        _uiState.value = PromptUiState.Ready(draw, current.activeRules, current.hasNext())
    }

    class Factory(private val packRepository: PackRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = PromptViewModel(packRepository) as T
    }
}
