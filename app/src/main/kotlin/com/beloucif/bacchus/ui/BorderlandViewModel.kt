package com.beloucif.bacchus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beloucif.bacchus.core.BorderlandEngine
import com.beloucif.bacchus.core.BorderlandState
import com.beloucif.bacchus.core.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Thin reactive wrapper around the pure [BorderlandEngine] reducer. */
class BorderlandViewModel : ViewModel() {

    private val _state = MutableStateFlow(BorderlandState())
    val state: StateFlow<BorderlandState> = _state.asStateFlow()

    fun start(players: List<Player>) {
        _state.value = BorderlandEngine.initGame(players)
    }

    fun drawCard() {
        _state.value = BorderlandEngine.drawCard(_state.value)
    }

    fun revealCard() {
        _state.value = BorderlandEngine.revealCard(_state.value)
    }

    fun nextTurn() {
        _state.value = BorderlandEngine.nextTurn(_state.value)
    }

    fun startContest(challenger: Player) {
        _state.value = BorderlandEngine.startContest(_state.value, challenger)
    }

    fun escalateContest(challenger: Player) {
        _state.value = BorderlandEngine.escalateContest(_state.value, challenger)
    }

    fun resolveContest(): com.beloucif.bacchus.core.PenaltyResult? {
        val (next, penalty) = BorderlandEngine.resolveContest(_state.value)
        _state.value = next
        return penalty
    }

    fun cancelContest() {
        _state.value = BorderlandEngine.cancelContest(_state.value)
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = BorderlandViewModel() as T
    }
}
