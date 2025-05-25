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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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
import com.collegegraduate.foodexplorer.viewModels.FavouritesViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    navController: NavController,
    viewModel: FavouritesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Reuse the same animated gradient background from HomeScreen
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
            FavouritesTopAppBar()

            when {
                uiState.isLoading -> {
                    LoadingAnimation()
                }

                uiState.error != null -> {
                    ErrorState(
                        error = uiState.error!!,
                        onRetry = {
                            //Add Retry Logic Further
                        }
                    )
                }

                uiState.favouriteItems.isEmpty() -> {
                    FavouritesEmptyState(navController = navController)
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(
                            items = uiState.favouriteItems,
                            key = { _, foodItem -> foodItem.id }
                        ) { index, foodItem ->
                            var isVisible by remember(key1 = "visible_${foodItem.id}") {
                                mutableStateOf(false)
                            }

                            LaunchedEffect(key1 = "animate_${foodItem.id}") {
                                if (!isVisible) {
                                    delay(index * 50L)
                                    isVisible = true
                                }
                            }

                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInVertically(
                                    initialOffsetY = { it / 3 },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                ) + fadeIn(animationSpec = tween(400))
                            ) {
                                E3DFavouriteCard(
                                    foodItem = foodItem,
                                    onItemClick = {
                                        navController.navigate(Screen.Detail.createRoute(foodItem.id))
                                    },
                                    onRemoveClick = {
                                        viewModel.removeFavourite(foodItem.id)
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
fun FavouritesTopAppBar() {
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
                        Color(0xFFFF6B9D).copy(alpha = glowIntensity),
                        Color(0xFFFF8E8E).copy(alpha = glowIntensity)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Text(
            text = "My Favourites",
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
fun E3DFavouriteCard(
    foodItem: FoodItem,
    onItemClick: () -> Unit,
    onRemoveClick: () -> Unit
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
                                    Color(0xFFFF6B9D).copy(alpha = 0.3f),
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

                    // Reuse ShimmerOverlay from HomeScreen
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

                    // Price, rating and category with enhanced styling
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
                                            Color(0xFFFF6B9D),
                                            Color(0xFFFF8E8E)
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

                        // Rating
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

                    // Category chip
                    if (!foodItem.category.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = foodItem.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Animated remove button
                AnimatedRemoveButton(onClick = onRemoveClick)
            }
        }
    }
}

@Composable
fun AnimatedRemoveButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "remove_scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isPressed) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "remove_rotation"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF4757),
                        Color(0xFFFF6B7A)
                    )
                )
            )
            .clickable {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove from favourites",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    rotationZ = rotation
                }
        )
    }
}

@Composable
fun FavouritesEmptyState(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Empty state animation
            val infiniteTransition = rememberInfiniteTransition(label = "empty_pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 0.9f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "empty_pulse_scale"
            )

            val heartbeat by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "heartbeat"
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF6B9D).copy(alpha = 0.3f),
                                Color(0xFFFF8E8E).copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💔",
                    fontSize = 48.sp,
                    modifier = Modifier.scale(heartbeat)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No Favourites Yet",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start adding your favorite foods from the home screen and they'll appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            var isPressed by remember { mutableStateOf(false) }
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                label = "browse_button_scale"
            )

            Button(
                onClick = { navController.navigate(Screen.Home.route) },
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
                                    Color(0xFFFF6B9D),
                                    Color(0xFFFF8E8E)
                                )
                            )
                        )
                        .padding(horizontal = 32.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Browse Foods",
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