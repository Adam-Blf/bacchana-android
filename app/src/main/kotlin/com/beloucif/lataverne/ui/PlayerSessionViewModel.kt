package com.beloucif.lataverne.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.beloucif.lataverne.core.Gender
import com.beloucif.lataverne.core.Player
import com.beloucif.lataverne.core.Relationship
import com.beloucif.lataverne.core.createPlayer
import com.beloucif.lataverne.data.PlayerStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Roster shared across every screen: Welcome writes it, Hub/game screens read it. */
class PlayerSessionViewModel(private val playerStore: PlayerStore) : ViewModel() {

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    init {
        viewModelScope.launch {
            playerStore.players.collect { names ->
                if (_players.value.isEmpty() && names.isNotEmpty()) {
                    _players.value = names.map { createPlayer(it) }
                }
            }
        }
    }

    fun addPlayer(name: String) {
        val trimmed = name.trim().take(20)
        if (trimmed.isBlank()) return
        if (_players.value.any { it.name.equals(trimmed, ignoreCase = true) }) return
        _players.value = _players.value + createPlayer(trimmed)
        persist()
    }

    fun removePlayer(playerId: String) {
        _players.value = _players.value.filterNot { it.id == playerId }
        persist()
    }

    /**
     * Genre/statut declares facultativement a l'inscription - 100% local (jamais envoye en
     * analytics, voir com.beloucif.lataverne.core.Player), non persistes dans [PlayerStore]
     * (seuls les noms le sont) : comme sur le web, c'est un detail de session, redemande a
     * chaque nouvelle inscription plutot que fige entre deux soirees.
     */
    fun setPlayerAttributes(playerId: String, gender: Gender?, relationship: Relationship?) {
        _players.value = _players.value.map { player ->
            if (player.id == playerId) player.copy(gender = gender, relationship = relationship) else player
        }
    }

    fun reactivateAll() {
        _players.value = _players.value.map { it.copy(active = true) }
    }

    fun hasEnoughPlayers(): Boolean = _players.value.size >= 2

    /**
     * Reglages > Reinitialiser la tablee : efface les joueurs (memoire + [PlayerStore]) sans
     * jamais toucher au premium (autre store, [com.beloucif.lataverne.billing.EntitlementRepository]).
     * Mirrors clearPlayers()+resetGame() de useGameStore sur le web.
     */
    fun resetAll() {
        _players.value = emptyList()
        viewModelScope.launch { playerStore.clear() }
    }

    private fun persist() {
        viewModelScope.launch { playerStore.savePlayers(_players.value.map { it.name }) }
    }

    class Factory(private val playerStore: PlayerStore) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PlayerSessionViewModel(playerStore) as T
    }
}
