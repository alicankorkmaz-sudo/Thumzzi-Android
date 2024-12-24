package studio.astroturf.thumbwrestling

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class SoundManager(
    context: Context,
) {
    private val soundPool =
        SoundPool
            .Builder()
            .setMaxStreams(4)
            .setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            ).build()

    private val soundHit = soundPool.load(context, R.raw.hit, 1)
    private val soundCombo = soundPool.load(context, R.raw.combo, 2)
    private val soundWin = soundPool.load(context, R.raw.win, 1)
    private val soundLose = soundPool.load(context, R.raw.lose, 1)

    fun playHitSound() {
        soundPool.play(soundHit, 1f, 1f, 1, 0, 1f)
    }

    fun playComboSound() {
        println("Playing combo sound")
        soundPool.play(soundCombo, 1f, 1f, 1, 0, 1f)
    }

    fun playWinSound() {
        soundPool.play(soundWin, 1f, 1f, 1, 0, 1f)
    }

    fun playLoseSound() {
        soundPool.play(soundLose, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}

@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }

    return soundManager
} 
