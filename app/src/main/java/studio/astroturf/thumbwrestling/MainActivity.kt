package studio.astroturf.thumbwrestling

import GameViewModel
import SetupScreen
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import studio.astroturf.thumbwrestling.ui.theme.ThumbWrestlingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force landscape orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Enable edge-to-edge
        enableEdgeToEdge()

        // Make the app full screen
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ThumbWrestlingTheme {
                // Hide system bars when the app starts
                HideSystemBars()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { _ ->
                    // Padding'i kullanmıyoruz
                    GameScreen()
                }
            }
        }
    }
}

@Composable
private fun HideSystemBars() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        val activity = view.context as Activity
        val window = activity.window
        val windowInsetsController = WindowCompat.getInsetsController(window, view)

        windowInsetsController.apply {
            // Hide both the status bars and the navigation bars
            hide(WindowInsetsCompat.Type.systemBars())

            // When the user swipes from the edge, make the system bars appear temporarily
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // Keep screen on while playing
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            // Show the system bars when the composable is disposed
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    val viewModel: GameViewModel = viewModel()
    val gameState by viewModel.gameState.collectAsState()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(gameState.backgroundColor),
    ) {
        when (gameState.gamePhase) {
            GameViewModel.GamePhase.SETUP -> {
                SetupScreen(
                    onStartGame = { viewModel.startGame() },
                )
            }
            else -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left player area (Player 1)
                    Box(modifier = Modifier.weight(1f)) {
                        PlayerArea(
                            isRightPlayer = false,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .rotate(90f),
                        )
                    }

                    // Right player area (Player 2)
                    Box(modifier = Modifier.weight(1f)) {
                        PlayerArea(
                            isRightPlayer = true,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .rotate(-90f),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    ThumbWrestlingTheme {
        GameScreen()
    }
}
