package studio.astroturf.thumbwrestling

import GameViewModel
import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun PlayerArea(
    isRightPlayer: Boolean,
    modifier: Modifier = Modifier,
) {
    val viewModel: GameViewModel = viewModel()
    val gameState by viewModel.gameState.collectAsState()
    val playerState = if (isRightPlayer) gameState.player2 else gameState.player1
    val soundManager = rememberSoundManager()
    val view = LocalView.current

    var showParticles by remember { mutableStateOf(false) }
    var particlePosition by remember { mutableStateOf(Offset.Zero) }

    // Güvenli alanı daha küçük tutalım
    val safeAreaHeight = 300.dp
    val safeAreaWidth = 160.dp
    val buttonSize = 80.dp

    // Buton pozisyonunu state olarak tutuyoruz
    var targetOffset by remember {
        mutableStateOf(
            Offset(
                Random.nextInt(0, (safeAreaWidth.value - buttonSize.value).toInt()).toFloat(),
                Random.nextInt(50, (safeAreaHeight.value - buttonSize.value).toInt()).toFloat(),
            ),
        )
    }

    // Animasyonlu offset
    val animatedOffset by animateOffsetAsState(
        targetValue = targetOffset,
        animationSpec = tween(500), // 500ms'lik yumuşak geçiş
        label = "button position animation",
    )

    // Her saniye butonun hedef pozisyonunu güncelliyoruz
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            targetOffset =
                Offset(
                    Random.nextInt(0, (safeAreaWidth.value - buttonSize.value).toInt()).toFloat(),
                    Random.nextInt(50, (safeAreaHeight.value - buttonSize.value).toInt()).toFloat(),
                )
        }
    }

    LaunchedEffect(Unit) {
        println("Starting to collect events") // Debug log
        viewModel.events.collect { event ->
            println("Received event: $event") // Debug log
            when (event) {
                GameViewModel.GameEvent.ComboAchieved -> {
                    println("Playing combo sound from event") // Debug log
                    soundManager.playComboSound()
                }
            }
        }
    }

    // Combo durumunu hesapla
    val currentTime = System.currentTimeMillis()
    val isComboActive = currentTime - playerState.lastHitTimestamp <= 2000L // 2000L GameViewModel'deki comboTimeWindow ile aynı olmalı
    val displayCombo = if (isComboActive) playerState.comboCount else 0

    Box(
        modifier =
            modifier
                .background(if (isRightPlayer) gameState.player2AreaColor else gameState.player1AreaColor)
                .padding(16.dp),
    ) {
        // Status bar (health + combo) - top center
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(8.dp),
        ) {
            // Health display - smaller hearts in a row
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector =
                            if (index < playerState.health) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Outlined.FavoriteBorder
                            },
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Updated combo counter
            Text(
                text = "x$displayCombo",
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        shadow =
                            Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f,
                            ),
                    ),
                color =
                    if (isComboActive && displayCombo > 0) {
                        Color.Yellow
                    } else {
                        Color.White.copy(alpha = 0.7f)
                    },
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        if (!gameState.gameOver) { // Oyun bitmemişse buton veya sayaç göster
            if (gameState.buttonVisible) {
                Button(
                    onClick = {
                        viewModel.onButtonClick(isRightPlayer)
                        soundManager.playHitSound()
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        showParticles = true
                        particlePosition = animatedOffset
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = if (isRightPlayer) gameState.player2ButtonColor else gameState.player1ButtonColor,
                        ),
                    modifier =
                        Modifier
                            .offset(
                                x = animatedOffset.x.dp,
                                y = animatedOffset.y.dp,
                            ).size(buttonSize),
                ) {
                    Text("TAP!")
                }
            } else if (gameState.countdownVisible) { // Sadece oyun başında sayaç göster
                // Countdown always in center
                Text(
                    text = gameState.countdownSeconds.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        // Particle effect
        if (showParticles) {
            HitParticleEffect(
                position = particlePosition,
                onFinish = { showParticles = false },
            )
        }

        // Game over message and restart button - center
        if (gameState.gameOver) {
            // Sadece kazanan oyuncunun tarafında ses çal
            LaunchedEffect(Unit) {
                if (gameState.winner == (if (isRightPlayer) 2 else 1)) {
                    soundManager.playWinSound()
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center),
            ) {
                Text(
                    text =
                        if (gameState.winner == (if (isRightPlayer) 2 else 1)) {
                            "WIN!"
                        } else {
                            "LOSE!"
                        },
                    style = MaterialTheme.typography.displayMedium,
                    color =
                        if (gameState.winner == (if (isRightPlayer) 2 else 1)) {
                            Color.Green
                        } else {
                            Color.Red
                        },
                )

                // Sadece kaybeden oyuncunun ekranında restart butonu göster
                if (gameState.winner != (if (isRightPlayer) 2 else 1)) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.startNewGame() },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                            ),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                            )
                            Text("PLAY AGAIN")
                        }
                    }
                }
            }
        }
    }
} 
