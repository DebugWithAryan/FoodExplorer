# 🍽️ Food Explorer

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material%203-orange.svg)](https://m3.material.io)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern, visually stunning Android application for exploring and discovering delicious food items. Built with cutting-edge technologies and featuring immersive animations, glassmorphism design, and an intuitive user experience.

## 📱 Screenshots

<!-- Add your app screenshots here -->
<div align="center">
  <img src="screenshots/home_screen.png" width="250" alt="Home Screen">
  <img src="screenshots/detail_screen.png" width="250" alt="Detail Screen">
  <img src="screenshots/favorites_screen.png" width="250" alt="Favorites Screen">
</div>

## 🎥 Demo Video

<!-- Add your demo video here -->
<div align="center">
  <a href="demo/food_explorer_demo.mp4">
    <img src="demo/video_thumbnail.png" width="600" alt="Demo Video Thumbnail">
  </a>
  <p><em>Click to watch the full demo video</em></p>
</div>

## ✨ Features

### 🎨 **Stunning Visual Design**
- **Glassmorphism UI** with translucent backgrounds and blur effects
- **Dynamic gradients** that animate smoothly across the interface
- **Floating particle animations** creating an immersive atmosphere
- **3D card interactions** with depth and shadow effects

### 🚀 **Advanced Animations**
- **Staggered entrance animations** for smooth content loading
- **Spring-based interactions** for natural, bouncy feedback
- **Infinite gradient transitions** creating living backgrounds
- **Micro-interactions** on every touch point

### 🍔 **Core Functionality**
- **Browse food items** with rich details and high-quality images
- **Detailed food information** including price, calories, and cooking time
- **Favorites system** to save your preferred dishes
- **Smooth navigation** between screens with animated transitions

### 📱 **Modern Architecture**
- **MVVM pattern** with ViewModels and StateFlow
- **Jetpack Compose** for declarative UI development
- **Navigation Component** for seamless screen transitions
- **Reactive programming** with Kotlin Coroutines

## 🛠️ Tech Stack

### **Frontend**
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Latest Material Design components
- **Coil** - Efficient image loading library

### **Architecture & Libraries**
- **MVVM** - Model-View-ViewModel architecture pattern
- **StateFlow** - Reactive state management
- **Navigation Component** - Type-safe navigation
- **Kotlin Coroutines** - Asynchronous programming
- **ViewModels** - UI-related data management

### **UI/UX Features**
- **Custom animations** with Compose Animation APIs
- **Glassmorphism effects** using blur and transparency
- **Dynamic theming** with Material 3 color system
- **Responsive layouts** adapting to different screen sizes

## 📂 Project Structure

```
app/src/main/java/com/collegegraduate/foodexplorer/
├── 📁 dataModels/
│   └── FoodItem.kt              # Data model for food items
├── 📁 nav/
│   └── Screen.kt                # Navigation destinations
├── 📁 userInterface/
│   ├── DetailScreen.kt          # Food detail screen with animations
│   ├── FoodExplorerApp.kt       # Main app container with navigation
│   ├── HomeScreen.kt            # Home screen with food list
│   └── FavouritesScreen.kt      # Favorites management screen
└── 📁 viewModels/
    ├── DetailViewModel.kt       # Detail screen business logic
    └── HomeViewModel.kt         # Home screen state management
```

## 🎯 Key Components

### **HomeScreen**
- Displays food items in an elegant scrollable list
- Features staggered animations for smooth content appearance
- Interactive food cards with 3D hover effects
- Real-time favorite status updates

### **DetailScreen**
- Comprehensive food information display
- Hero image section with overlay gradients
- Animated information chips showing price, calories, and cooking time
- Smooth favorite toggle with spring animations

### **Enhanced Navigation**
- Glassmorphic bottom navigation bar
- Animated icons with scale and rotation effects
- Smooth transitions between screens
- Particle effects throughout the app

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Arctic Fox or later
- **Android SDK** 21 or higher
- **Kotlin** 1.8.0 or later

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/food-explorer.git
   cd food-explorer
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Click "Open an existing project"
   - Select the cloned directory

3. **Sync the project**
   - Wait for Gradle sync to complete
   - Resolve any dependency issues if prompted

4. **Run the application**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Ctrl+R`

## 🔧 Configuration

### **Adding Food Data**
The app uses a data model structure for food items. To add your own food data:

```kotlin
data class FoodItem(
    val id: Int,
    val name: String?,
    val shortDescription: String?,
    val fullDescription: String?,
    val price: Double?,
    val rating: Double?,
    val calories: Int?,
    val cookingTime: String?,
    val category: String?,
    val imageUrl: String?
)
```

### **Customizing Animations**
Animation parameters can be adjusted in each screen component:

```kotlin
// Example: Modify entrance animation timing
LaunchedEffect(key1 = index) {
    delay(index * 100L) // Adjust stagger delay
    isVisible = true
}
```

## 🎨 Design System

### **Color Palette**
- **Primary Gradient**: `#667eea` to `#f093fb`
- **Background**: `#0F0F23`, `#1A1A2E`, `#16213E`
- **Accent Colors**: `#00D4FF`, `#FF6B9D`
- **Text**: White with various opacity levels

### **Animation Principles**
- **Spring-based** interactions for natural feel
- **Staggered** animations for content discovery
- **Particle systems** for ambient atmosphere
- **Glassmorphism** for depth and elegance

## 🤝 Contributing

We welcome contributions to make Food Explorer even better! Here's how you can help:

### **Ways to Contribute**
1. 🐛 **Report bugs** by opening an issue
2. 💡 **Suggest features** or improvements
3. 🔧 **Submit pull requests** with enhancements
4. 📖 **Improve documentation** and examples

### **Development Guidelines**
1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### **Code Style**
- Follow **Kotlin coding conventions**
- Use **meaningful variable names**
- Add **comments** for complex logic
- Ensure **consistent formatting**

## 📊 Performance

### **Optimization Features**
- **Lazy loading** for smooth scrolling
- **Efficient image caching** with Coil
- **State management** with minimal recomposition
- **Memory-conscious** animation handling

### **Benchmarks**
- **Smooth 60fps** animations on modern devices
- **Fast startup time** under 2 seconds
- **Minimal memory footprint** with efficient resource usage

## 🐛 Known Issues

- [ ] Image loading may take time on slower connections
- [ ] Particle animations might impact battery on older devices
- [ ] Some animations may not render on devices with API < 21

## 🔮 Future Enhancements

### **Planned Features**
- [ ] **Search functionality** with real-time filtering
- [ ] **User reviews** and rating system
- [ ] **Recipe details** with step-by-step instructions
- [ ] **Dietary filters** (vegetarian, vegan, gluten-free)
- [ ] **Shopping list** integration
- [ ] **Dark/Light theme** toggle
- [ ] **Offline support** with local database
- [ ] **Social sharing** of favorite foods

### **Technical Improvements**
- [ ] **Unit tests** coverage expansion
- [ ] **UI tests** with Compose testing
- [ ] **Performance profiling** and optimization
- [ ] **Accessibility** improvements
- [ ] **Localization** support


## 👨‍💻 Author

**Your Name**
- GitHub: [@DebugWithAryan](https://github.com/DebugWithAryan)
- LinkedIn: [Your LinkedIn](https://linkedin.com/in/aryanjaiswal1)
- Email: your.email@example.com

## 🙏 Acknowledgments

- **Material Design** team for the beautiful design system
- **Jetpack Compose** team for the amazing UI toolkit
- **Android Developer** community for continuous inspiration
- **Open source** contributors who make development possible

## 📞 Support

If you found this project helpful, please consider:

- ⭐ **Starring** the repository
- 🍴 **Forking** for your own modifications
- 📢 **Sharing** with fellow developers
- 💝 **Contributing** to make it better

---

<div align="center">
  <p><strong>Built with ❤️ using Kotlin & Jetpack Compose</strong></p>
  <p><em>Making food discovery a delightful experience</em></p>
</div>
