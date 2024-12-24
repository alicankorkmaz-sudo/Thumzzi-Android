import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GameViewModel : ViewModel() {
    data class PlayerState(
        val health: Int = 5,
        val comboCount: Int = 0,
        val lastHitTimestamp: Long = 0
    )

    data class GameState(
        val player1: PlayerState = PlayerState(),
        val player2: PlayerState = PlayerState(),
        val buttonVisible: Boolean = false,
        val countdownVisible: Boolean = true,
        val countdownSeconds: Int = 3,
        val gameOver: Boolean = false,
        val winner: Int? = null
    )

    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()

    private val comboTimeWindow = 2000L
    private val comboHealthBonus = 1

    // Add events for sound coordination
    sealed class GameEvent {
        object ComboAchieved : GameEvent()
    }

    private val _events = MutableSharedFlow<GameEvent>()
    val events = _events.asSharedFlow()

    init {
        startGame()
    }

    private fun startGame() {
        viewModelScope.launch {
            // Oyun başlangıç sayacı
            for (i in 3 downTo 1) {
                _gameState.update { it.copy(countdownSeconds = i) }
                delay(1000)
            }
            
            _gameState.update { it.copy(
                buttonVisible = true,
                countdownVisible = false
            )}
        }
    }

    private fun startNewRound() {
        if (_gameState.value.gameOver) return
        
        viewModelScope.launch {
            _gameState.update { it.copy(buttonVisible = false) }
            delay(1000) // 1 saniye bekle
            _gameState.update { it.copy(buttonVisible = true) }
        }
    }

    fun onButtonClick(isRightPlayer: Boolean) {
        val currentState = _gameState.value
        
        if (!currentState.buttonVisible || currentState.gameOver) return

        val currentTime = System.currentTimeMillis()
        updateComboAndHealth(isRightPlayer, currentTime)
        
        if (!_gameState.value.gameOver) {
            startNewRound()
        }
    }

    private fun updateComboAndHealth(isRightPlayer: Boolean, hitTime: Long) {
        _gameState.update { state ->
            // Vuran oyuncunun combo'sunu güncelle
            val attackingPlayer = if (isRightPlayer) state.player2 else state.player1
            val defendingPlayer = if (isRightPlayer) state.player1 else state.player2
            
            val newCombo = if (hitTime - attackingPlayer.lastHitTimestamp <= comboTimeWindow) {
                attackingPlayer.comboCount + 1
            } else {
                1
            }

            // Combo bonusu kontrolü - vuran oyuncuya can ekle
            val bonusHealth = if (newCombo > 0 && newCombo % 3 == 0) {
                // Trigger combo event
                _events.tryEmit(GameEvent.ComboAchieved)
                comboHealthBonus
            } else {
                0
            }

            // Savunmadaki oyuncunun canını azalt
            val newDefenderHealth = (defendingPlayer.health - 1).coerceAtLeast(0)
            
            // Vuran oyuncunun canını güncelle (bonus dahil)
            val newAttackerHealth = (attackingPlayer.health + bonusHealth).coerceAtMost(5)

            // Can bonusu alındıysa combo sıfırlanacak, alınmadıysa yeni combo değeri kullanılacak
            val finalCombo = if (bonusHealth > 0) 0 else newCombo

            // Her başarılı vuruşta savunmadaki oyuncunun combosu sıfırlanır
            val defendingPlayerWithResetCombo = defendingPlayer.copy(
                health = newDefenderHealth,
                comboCount = 0,  // Her vuruşta savunmadaki oyuncunun combosu sıfırlanır
                lastHitTimestamp = 0  // Combo zamanlamasını da sıfırlıyoruz
            )

            if (newDefenderHealth <= 0) {
                state.copy(
                    gameOver = true,
                    winner = if (isRightPlayer) 2 else 1,
                    buttonVisible = false,
                    player1 = if (isRightPlayer) {
                        defendingPlayerWithResetCombo
                    } else {
                        attackingPlayer.copy(
                            health = newAttackerHealth,
                            comboCount = finalCombo,
                            lastHitTimestamp = hitTime
                        )
                    },
                    player2 = if (isRightPlayer) {
                        attackingPlayer.copy(
                            health = newAttackerHealth,
                            comboCount = finalCombo,
                            lastHitTimestamp = hitTime
                        )
                    } else {
                        defendingPlayerWithResetCombo
                    }
                )
            } else {
                state.copy(
                    player1 = if (isRightPlayer) {
                        defendingPlayerWithResetCombo
                    } else {
                        attackingPlayer.copy(
                            health = newAttackerHealth,
                            comboCount = finalCombo,
                            lastHitTimestamp = hitTime
                        )
                    },
                    player2 = if (isRightPlayer) {
                        attackingPlayer.copy(
                            health = newAttackerHealth,
                            comboCount = finalCombo,
                            lastHitTimestamp = hitTime
                        )
                    } else {
                        defendingPlayerWithResetCombo
                    }
                )
            }
        }
    }

    fun startNewGame() {
        _gameState.value = GameState()
        startGame()
    }
} 