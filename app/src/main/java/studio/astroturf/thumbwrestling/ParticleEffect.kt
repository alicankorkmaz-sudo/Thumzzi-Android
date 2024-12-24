package studio.astroturf.thumbwrestling

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Particle(
    val initialPosition: Offset,
    val angle: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val lifetime: Float,
)

@Composable
fun HitParticleEffect(
    position: Offset,
    onFinish: () -> Unit,
) {
    var particles by remember {
        mutableStateOf(
            List(20) { index ->
                Particle(
                    initialPosition = position,
                    angle = Random.nextFloat() * 360f,
                    speed = Random.nextFloat() * 500f + 200f,
                    size = Random.nextFloat() * 8f + 2f,
                    color =
                        Color(
                            red = Random.nextFloat(),
                            green = Random.nextFloat(),
                            blue = Random.nextFloat(),
                            alpha = 1f,
                        ),
                    lifetime = 1f,
                )
            },
        )
    }

    val transition = rememberInfiniteTransition(label = "particle")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "particle_animation",
    )

    LaunchedEffect(time) {
        particles =
            particles
                .map { particle ->
                    particle.copy(lifetime = particle.lifetime - 0.016f)
                }.filter { it.lifetime > 0f }

        if (particles.isEmpty()) {
            onFinish()
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val distance = particle.speed * (1f - particle.lifetime)
            val x = particle.initialPosition.x + kotlin.math.cos(Math.toRadians(particle.angle.toDouble())).toFloat() * distance
            val y = particle.initialPosition.y + kotlin.math.sin(Math.toRadians(particle.angle.toDouble())).toFloat() * distance

            drawCircle(
                color = particle.color.copy(alpha = particle.lifetime),
                radius = particle.size * particle.lifetime,
                center = Offset(x, y),
            )
        }
    }
} 
