# 🧮 Abacus AI (Master Soroban)

An interactive, hyper-realistic Japanese Soroban (Abacus) application with native Android and Web experiences.

---

## ✨ Features

- **Hyper-Realistic Japanese Soroban (1:4 beads):**
  - 13 rods supporting numbers up to 9,999,999,999,999 (~10 Trillion).
  - 3D bi-conical diamond beads with dual-facet specular reflections, ambient bounce light, and central rod boreholes.
  - Procedural wood-grain timber frame with deep bevels, inner shadow, and antique brass corner reinforcement brackets.
  - Ebony reckoning beam with inlaid mother-of-pearl unit alignment dots (*Hoshi* markers).
- **Physical Acoustics & Haptics:**
  - Procedural Web Audio synthesis of realistic dry wood clacks with variable pitch based on impact dynamics.
  - Native phone haptic vibration on every bead slide.
- **Fluid Spring Physics:**
  - Touch drag-and-flick gestures with smooth 60fps spring sliding animation.
  - One-touch quick-clearing mechanism button.
- **Dual Modes:**
  - **🔢 3-Step Calculator:** Save first number, choose operation (`+`, `−`, `×`, `÷`), enter second number, and watch the abacus animate the solution automatically.
  - **📚 Learn Mode:** 20 progressive challenges ranging from basic units up to 1,000,000 with validation, hints, and score tracking.
- **Multiple Wood Finishes:**
  - Dark Rosewood (*Shitan*)
  - American Walnut
  - Antique Ebony (*Kokutan*)
  - Japanese Cherry (*Sakura*)

---

## 📱 Platforms

1. **Android Application (`/app`):**
   - Built with Jetpack Compose & hardware-accelerated WebView engine.
   - Compiled for Android 7.0+ (API 24–35).
   - Supports both Portrait and full-screen Landscape orientation.
2. **Web Application (`index.html`):**
   - 100% self-contained HTML5 Canvas application.
   - Zero external dependencies — works completely offline.

---

## 🛠️ Build & Run

### Android
Open the project directory in **Android Studio** and click **Run (▶️)**, or compile via command line:
```bash
./gradlew assembleDebug
```

### Web
Open `index.html` in any modern desktop or mobile web browser.
