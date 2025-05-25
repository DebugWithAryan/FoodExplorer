package com.collegegraduate.foodexplorer.userInterface

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.collegegraduate.foodexplorer.nav.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodExplorerApp() {
    val navController = rememberNavController()

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF16213E),
            Color(0xFF0F3460)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                EnhancedBottomNavigationBar(navController = navController)
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(navController = navController)
                }

                composable(Screen.Favourites.route) {
                    FavouritesScreen(navController = navController)
                }

                composable(Screen.Detail.route) { backStackEntry ->
                    val foodId = backStackEntry.arguments?.getString("foodId")?.toIntOrNull() ?: 0
                    DetailScreen(
                        foodId = foodId.toString(),
                        navController = navController
                    )
                }
            }
        }

        FloatingParticles()
    }
}

@Composable
fun EnhancedBottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val glassBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.15f),
            Color.White.copy(alpha = 0.05f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(glassBrush)
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.clip(RoundedCornerShape(28.dp))
        ) {
            NavigationBarItem(
                icon = {
                    AnimatedNavIcon(
                        icon = Icons.Default.Home,
                        isSelected = currentRoute == Screen.Home.route,
                        contentDescription = "Home"
                    )
                },
                label = {
                    AnimatedVisibility(
                        visible = currentRoute == Screen.Home.route,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            "Home",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                selected = currentRoute == Screen.Home.route,
                onClick = {
                    if (currentRoute != Screen.Home.route) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00D4FF),
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    indicatorColor = Color(0xFF00D4FF).copy(alpha = 0.2f)
                )
            )

            NavigationBarItem(
                icon = {
                    AnimatedNavIcon(
                        icon = Icons.Default.Favorite,
                        isSelected = currentRoute == Screen.Favourites.route,
                        contentDescription = "Favourites"
                    )
                },
                label = {
                    AnimatedVisibility(
                        visible = currentRoute == Screen.Favourites.route,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            "Favourites",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                selected = currentRoute == Screen.Favourites.route,
                onClick = {
                    if (currentRoute != Screen.Favourites.route) {
                        navController.navigate(Screen.Favourites.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFF6B9D),
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    indicatorColor = Color(0xFFFF6B9D).copy(alpha = 0.2f)
                )
            )
        }
    }
}

@Composable
fun AnimatedNavIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    contentDescription: String
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "icon_scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 360f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "icon_rotation"
    )

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = Modifier
            .scale(scale)
            .graphicsLayer {
                rotationY = if (isSelected) rotation else 0f
            }
    )
}

@Composable
fun FloatingParticles() {
    val particles = remember {
        List(15) { index ->
            ParticleState(
                x = (index * 67) % 1000f,
                y = (index * 143) % 2000f,
                size = 2f + (index % 6),
                speed = 0.5f + (index % 3) * 0.5f,
                color = when (index % 4) {
                    0 -> Color(0xFF00D4FF)
                    1 -> Color(0xFFFF6B9D)
                    2 -> Color(0xFF9B59B6)
                    else -> Color(0xFF3498DB)
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            particles.forEach { particle ->
                particle.y -= particle.speed
                if (particle.y < -50f) {
                    particle.y = 2000f
                    particle.x = ((particle.x + 100) % 1000f)
                }
            }
            kotlinx.coroutines.delay(50)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawCircle(
                color = particle.color.copy(alpha = 0.3f),
                radius = particle.size,
                center = Offset(particle.x, particle.y)
            )
        }
    }
}

data class ParticleState(
    var x: Float,
    var y: Float,
    val size: Float,
    val speed: Float,
    val color: Color
)