import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import studio.astroturf.thumbwrestling.ui.theme.Background
import studio.astroturf.thumbwrestling.ui.theme.Primary
import studio.astroturf.thumbwrestling.ui.theme.Secondary

@Composable
fun SetupScreen(
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: GameViewModel = viewModel()
    val gameState by viewModel.gameState.collectAsState()

    val colorOptions =
        listOf(
            Primary to "Blue",
            Secondary to "Green",
            Color(0xFFE53935) to "Red",
            Color(0xFFFFD600) to "Yellow",
            Color(0xFFAA00FF) to "Purple",
            Color(0xFF00B8D4) to "Cyan",
        )

    val backgroundOptions =
        listOf(
            Background to "Navy",
            Color(0xFF000000) to "Black",
            Color(0xFF1A237E) to "Indigo",
            Color(0xFF311B92) to "Purple",
            Color(0xFF1B5E20) to "Forest",
            Color(0xFF263238) to "Dark",
        )

    Row(
        modifier = modifier.fillMaxSize(),
    ) {
        // Player 1 Setup
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .background(gameState.player1AreaColor),
        ) {
            PlayerSetupArea(
                playerName = "PLAYER 1",
                buttonColor = gameState.player1ButtonColor,
                areaColor = gameState.player1AreaColor,
                isReady = gameState.player1Ready,
                colorOptions = colorOptions,
                backgroundOptions = backgroundOptions,
                onButtonColorSelected = { viewModel.updatePlayer1ButtonColor(it) },
                onAreaColorSelected = { viewModel.updatePlayer1AreaColor(it) },
                onReadyChanged = { viewModel.setPlayer1Ready(it) },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .rotate(90f)
                        .padding(24.dp),
            )
        }

        // Divider with glow effect
        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        brush =
                            Brush.horizontalGradient(
                                colors =
                                    listOf(
                                        Color.White.copy(alpha = 0f),
                                        Color.White.copy(alpha = 0.7f),
                                        Color.White.copy(alpha = 0f),
                                    ),
                            ),
                    ),
        )

        // Player 2 Setup
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .background(gameState.player2AreaColor),
        ) {
            PlayerSetupArea(
                playerName = "PLAYER 2",
                buttonColor = gameState.player2ButtonColor,
                areaColor = gameState.player2AreaColor,
                isReady = gameState.player2Ready,
                colorOptions = colorOptions,
                backgroundOptions = backgroundOptions,
                onButtonColorSelected = { viewModel.updatePlayer2ButtonColor(it) },
                onAreaColorSelected = { viewModel.updatePlayer2AreaColor(it) },
                onReadyChanged = { viewModel.setPlayer2Ready(it) },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .rotate(-90f)
                        .padding(24.dp),
            )
        }
    }

    // Start game when both players are ready
    LaunchedEffect(gameState.player1Ready, gameState.player2Ready) {
        if (gameState.player1Ready && gameState.player2Ready) {
            delay(500)
            onStartGame()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlayerSetupArea(
    playerName: String,
    buttonColor: Color,
    areaColor: Color,
    isReady: Boolean,
    colorOptions: List<Pair<Color, String>>,
    backgroundOptions: List<Pair<Color, String>>,
    onButtonColorSelected: (Color) -> Unit,
    onAreaColorSelected: (Color) -> Unit,
    onReadyChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .verticalScroll(scrollState)
                    .padding(bottom = 80.dp), // Ready butonu için alan bırak
        ) {
            // Player name with glow effect
            Text(
                playerName,
                style =
                    MaterialTheme.typography.displayMedium.copy(
                        shadow =
                            Shadow(
                                color = Color.White.copy(alpha = 0.3f),
                                offset = Offset(0f, 0f),
                                blurRadius = 8f,
                            ),
                    ),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp),
            )

            // Button Color Selection
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.3f),
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "BUTTON COLOR",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        colorOptions.forEach { (color, name) ->
                            ColorButton(
                                color = color,
                                name = name,
                                isSelected = color == buttonColor,
                                onClick = { onButtonColorSelected(color) },
                            )
                        }
                    }
                }
            }

            // Area Color Selection
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.3f),
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "AREA COLOR",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        backgroundOptions.forEach { (color, name) ->
                            ColorButton(
                                color = color,
                                name = name,
                                isSelected = color == areaColor,
                                onClick = { onAreaColorSelected(color) },
                            )
                        }
                    }
                }
            }
        }

        // Ready button at bottom
        Button(
            onClick = { onReadyChanged(!isReady) },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = if (isReady) Color(0xFF00C853) else Color(0xFF424242),
                ),
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .size(width = 200.dp, height = 56.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isReady) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    if (isReady) "READY!" else "GET READY",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun ColorButton(
    color: Color,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = color,
            ),
        border =
            if (isSelected) {
                BorderStroke(3.dp, Color.White)
            } else {
                null
            },
        modifier =
            Modifier
                .padding(4.dp)
                .size(width = 85.dp, height = 36.dp),
        elevation =
            ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp,
            ),
    ) {
        Text(
            name,
            color = if (color.luminance() > 0.5f) Color.Black else Color.White,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
        )
    }
} 
