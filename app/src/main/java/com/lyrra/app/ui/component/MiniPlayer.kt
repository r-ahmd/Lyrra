package com.lyrra.app.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lyrra.app.AlbumPalette
import com.lyrra.app.BackgroundStyle
import com.lyrra.app.NowPlayingState

/**
 * Lyrra's smooth MiniPlayer bar with spring physics, haptic feedback,
 * swipe-to-skip gestures, and fluid album art transitions.
 */
@Composable
fun MiniPlayer(
    state: NowPlayingState,
    onTogglePlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundStyle: BackgroundStyle = BackgroundStyle.Solid,
    palette: AlbumPalette? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = LocalHapticFeedback.current
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val swipeThreshold = 80f

    AnimatedVisibility(
        visible = state.hasMedia,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(animationSpec = tween(350, easing = FastOutSlowInEasing)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        ) + fadeOut(animationSpec = tween(280)),
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(24.dp))
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            when {
                                dragOffset > swipeThreshold -> { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onPrevious() }
                                dragOffset < -swipeThreshold -> { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onNext() }
                            }
                            dragOffset = 0f
                        },
                        onHorizontalDrag = { _, delta -> dragOffset += delta }
                    )
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onClick() }
                )
                .testTag("mini_player"),
            color = Color.Transparent,
            tonalElevation = 4.dp,
            shadowElevation = 10.dp,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                MiniPlayerBackground(
                    modifier = Modifier.matchParentSize(),
                    style = backgroundStyle,
                    palette = palette,
                    artworkUrl = state.artworkUrl,
                )

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Artwork with spring-based scale animation
                        val artworkScale by animateFloatAsState(
                            targetValue = if (state.isPlaying) 1.0f else 0.92f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "artwork_scale"
                        )

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .scale(artworkScale)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (state.artworkUrl != null) {
                                AsyncImage(
                                    model = state.artworkUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(48.dp),
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp),
                        ) {
                            Text(
                                text = state.title.ifBlank { "Loading..." },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = state.artist.ifBlank { "Lyrra" },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        // Playback Controls
                        IconButton(
                            onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onPrevious() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("mini_player_previous"),
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous track",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp),
                            )
                        }

                        // Prominent filled Play/Pause button with animation
                        val playPauseScale by animateFloatAsState(
                            targetValue = if (state.isPlaying) 1.0f else 0.88f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessHigh
                            ),
                            label = "play_pause_scale"
                        )

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(40.dp)
                                .scale(playPauseScale)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onTogglePlayPause()
                                }
                                .testTag("mini_player_play_pause"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (state.isPlaying) "Pause" else "Play",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp),
                            )
                        }

                        IconButton(
                            onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onNext() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next track",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }

                    // Smooth animated progress indicator
                    val animatedProgress by animateFloatAsState(
                        targetValue = state.progress.coerceIn(0f, 1f),
                        animationSpec = tween(durationMillis = 150, easing = LinearEasing),
                        label = "mini_player_progress"
                    )

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    )
                }
            }
        }
    }
}

/**
 * Background layer with smooth color gradients and glassmorphism.
 */
@Composable
private fun MiniPlayerBackground(
    modifier: Modifier,
    style: BackgroundStyle,
    palette: AlbumPalette?,
    artworkUrl: String?,
) {
    val base = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.94f)

    when {
        style == BackgroundStyle.Gradient && palette != null -> Box(
            modifier = modifier.background(
                Brush.horizontalGradient(
                    listOf(
                        palette.dominant.copy(alpha = 0.65f).compositeOver(base),
                        palette.muted.copy(alpha = 0.45f).compositeOver(base),
                    ),
                ),
            ),
        )

        style == BackgroundStyle.Blur && artworkUrl != null -> Box(modifier = modifier) {
            AsyncImage(
                model = artworkUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(28.dp),
            )
            Box(modifier = Modifier.fillMaxSize().background(base.copy(alpha = 0.60f)))
        }

        palette != null -> Box(
            modifier = modifier.background(
                palette.dominant.copy(alpha = 0.40f).compositeOver(base)
            ),
        )

        else -> Box(modifier = modifier.background(base))
    }
}
