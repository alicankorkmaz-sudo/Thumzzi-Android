package studio.astroturf.thumbwrestling

import GameViewModel
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.random.Random

@Composable
fun PlayerArea(
    isRightPlayer: Boolean,
    modifier: Modifier = Modifier
) {
    val viewModel: GameViewModel = viewModel()
    val gameState by viewModel.gameState.collectAsState()
    val playerState = if (isRightPlayer) gameState.player2 else gameState.player1

    Box(
        modifier = modifier.padding(16.dp)
    ) {
        // Status bar (health + combo) - top center
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(8.dp)
        ) {
            // Health display - smaller hearts in a row
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < playerState.health) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Combo counter - smaller text
            Text(
                text = "x${playerState.comboCount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        if (!gameState.gameOver) {  // Oyun bitmemişse buton veya sayaç göster
            if (gameState.buttonVisible) {
                // Random position for the button
                val randomPosition = remember {
                    Modifier.offset(
                        x = Random.nextInt(0, 200).dp,
                        y = Random.nextInt(50, 300).dp
                    )
                }
                
                Button(
                    onClick = { viewModel.onButtonClick(isRightPlayer) },
                    modifier = Modifier
                        .then(randomPosition)
                        .size(80.dp)
                ) {
                    Text("TAP!")
                }
            } else if (gameState.countdownVisible) {  // Sadece oyun başında sayaç göster
                // Countdown always in center
                Text(
                    text = gameState.countdownSeconds.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Game over message and restart button - center
        if (gameState.gameOver) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = if (gameState.winner == (if (isRightPlayer) 2 else 1)) {
                        "WIN!"
                    } else {
                        "LOSE!"
                    },
                    style = MaterialTheme.typography.displayMedium,
                    color = if (gameState.winner == (if (isRightPlayer) 2 else 1)) {
                        Color.Green
                    } else {
                        Color.Red
                    }
                )
                
                // Sadece kazanan oyuncunun ekranında restart butonu göster
                if (gameState.winner == (if (isRightPlayer) 2 else 1)) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.startNewGame() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null
                            )
                            Text("PLAY AGAIN")
                        }
                    }
                }
            }
        }
    }
} 