package com.collegegraduate.foodexplorer.userInterface

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.collegegraduate.foodexplorer.dataModels.FoodItem
import com.collegegraduate.foodexplorer.nav.Screen
import com.collegegraduate.foodexplorer.viewModels.HomeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val favouriteIds by viewModel.favouriteIds.collectAsState(initial = emptySet())
    val infiniteTransition = rememberInfiniteTransition(label = "bg_gradient")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )

    val dynamicGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F0F23).copy(alpha = 0.9f),
            Color(0xFF1A1A2E).copy(alpha = 0.95f),
            Color(0xFF16213E).copy(alpha = 0.9f)
        ),
        startY = gradientOffset * 500f,
        endY = (gradientOffset * 500f) + 1000f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dynamicGradient)
    ) {
        FloatingOrbs()

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar()

            when {
                uiState.isLoading -> {
                    LoadingAnimation()
                }

                uiState.error != null -> {
                    ErrorState(
                        error = uiState.error!!,
                        onRetry = { viewModel.loadFoodItems() }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(uiState.foodItems) { index, foodItem ->
                            var isVisible by remember { mutableStateOf(false) }

                            LaunchedEffect(key1 = index) {
                                delay(index * 100L) // Staggered animation
                                isVisible = true
                            }

                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInVertically(
                                    initialOffsetY = { it / 2 },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ) + fadeIn(animationSpec = tween(600))
                            ) {
                                E3DFoodCard(
                                    foodItem = foodItem,
                                    isFavourite = favouriteIds.contains(foodItem.id.toString()),
                                    onItemClick = {
                                        navController.navigate(Screen.Detail.createRoute(foodItem.id))
                                    },
                                    onFavouriteClick = {
                                        viewModel.toggleFavourite(foodItem.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppBar() {
    val infiniteTransition = rememberInfiniteTransition(label = "header_glow")
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_intensity"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF667eea).copy(alpha = glowIntensity),
                        Color(0xFFf093fb).copy(alpha = glowIntensity)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Text(
            text = "🍽️ Food Explorer",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun E3DFoodCard(
    foodItem: FoodItem,
    isFavourite: Boolean,
    onItemClick: () -> Unit,
    onFavouriteClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var isImageLoading by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "card_scale"
    )

    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 12f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "card_elevation"
    )

    val consistentGlassBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E).copy(alpha = 0.8f),
            Color(0xFF16213E).copy(alpha = 0.9f),
            Color(0xFF0F0F23).copy(alpha = 0.85f)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .graphicsLayer {
                shadowElevation = elevation
                rotationX = if (isPressed) 5f else 0f
                rotationY = if (isPressed) 2f else 0f
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onItemClick()
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
    ) {
        Box(
            modifier = Modifier
                .background(consistentGlassBrush)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF667eea).copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    AsyncImage(
                        model = foodItem.imageUrl,
                        contentDescription = foodItem.name ?: "Food item",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.1f)),
                        contentScale = ContentScale.Crop,
                        onLoading = { isImageLoading = true },
                        onSuccess = { isImageLoading = false },
                        onError = { isImageLoading = false }
                    )

                    if (isImageLoading) {
                        ShimmerOverlay()
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Content section
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = foodItem.name ?: "Unknown Food",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = foodItem.shortDescription ?: "No description available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price and rating with enhanced styling
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Price chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF667eea),
                                            Color(0xFF764ba2)
                                        )
                                    )
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "₹${foodItem.price ?: 0}",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${foodItem.rating ?: 0.0}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                AnimatedFavoriteButton(
                    isFavourite = isFavourite,
                    onClick = onFavouriteClick
                )
            }
        }
    }
}

@Composable
fun AnimatedFavoriteButton(
    isFavourite: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val currentIsFavourite by rememberUpdatedState(isFavourite)

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "fav_scale",
        finishedListener = {
            isPressed = false
        }
    )

    val rotation by animateFloatAsState(
        targetValue = if (currentIsFavourite) 12f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fav_rotation"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (currentIsFavourite) {
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF6B9D),
                            Color(0xFFFF8E8E)
                        )
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                }
            )
            .clickable {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (currentIsFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (currentIsFavourite) "Remove from favourites" else "Add to favourites",
            tint = if (currentIsFavourite) Color.White else Color.White.copy(alpha = 0.7f),
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    rotationZ = rotation
                }
        )
    }
}

@Composable
fun ShimmerOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val gradient = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.3f),
                Color.Transparent
            ),
            start = Offset(shimmerTranslateAnim - 200f, 0f),
            end = Offset(shimmerTranslateAnim, size.height)
        )

        drawRect(brush = gradient)
    }
}
@Composable
fun FloatingOrbs() {
    val orbs = remember {
        List(8) { index ->
            OrbState(
                x = (index * 125) % 1000f,
                y = (index * 250) % 2000f,
                size = 20f + (index % 4) * 10f,
                speed = 0.3f + (index % 3) * 0.3f,
                color = when (index % 4) {
                    0 -> Color(0xFF667eea).copy(alpha = 0.1f)
                    1 -> Color(0xFFf093fb).copy(alpha = 0.1f)
                    2 -> Color(0xFF764ba2).copy(alpha = 0.1f)
                    else -> Color(0xFF667eea).copy(alpha = 0.08f)
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            orbs.forEach { orb ->
                orb.y -= orb.speed
                orb.x += sin(orb.y / 100f) * 0.5f
                if (orb.y < -100f) {
                    orb.y = 2100f
                    orb.x = ((orb.x + 150) % 1000f)
                }
            }
            delay(50)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        orbs.forEach { orb ->
            drawCircle(
                color = orb.color,
                radius = orb.size,
                center = Offset(orb.x, orb.y)
            )
        }
    }
}
@Composable
fun LoadingAnimation() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "loading")

            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing)
                ),
                label = "loading_rotation"
            )

            val scale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "loading_scale"
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
                    .graphicsLayer { rotationZ = rotation }
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF667eea),
                                Color(0xFFf093fb),
                                Color(0xFF667eea)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🍽️",
                    fontSize = 32.sp,
                    modifier = Modifier.graphicsLayer { rotationZ = -rotation }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Loading delicious foods...",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ErrorState(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Error animation
            val infiniteTransition = rememberInfiniteTransition(label = "error_pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "error_pulse_scale"
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF6B6B).copy(alpha = 0.3f),
                                Color(0xFFFF8E8E).copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Oops! Something went wrong",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Animated retry button
            var isPressed by remember { mutableStateOf(false) }
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                label = "retry_button_scale"
            )

            Button(
                onClick = onRetry,
                modifier = Modifier
                    .scale(buttonScale)
                    .clip(RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF667eea),
                                    Color(0xFFf093fb)
                                )
                            )
                        )
                        .padding(horizontal = 32.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Try Again",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

data class OrbState(
    var x: Float,
    var y: Float,
    val size: Float,
    val speed: Float,
    val color: Color
)

private fun sin(x: Float): Float = kotlin.math.sin(x.toDouble()).toFloat()