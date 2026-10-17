<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="320" />
</p>

# Karoo BBrakers (v1.0.0)

<p align="center">
  <a href="README.md">Español</a> |
  <a href="README_en.md">English</a> |
  <a href="README_fr.md">Français</a> |
  <a href="README_de.md">Deutsch</a> |
  <a href="README_it.md">Italiano</a> |
  <a href="README_pt.md">Português</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Karoo_OS-KOS_1.634+-FF6D00?logo=android&logoColor=white" alt="Karoo OS">
  <img src="https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Units-Metric%20%26%20Imperial%20(UK%20%7C%20US%20%7C%20EU)-00E5FF" alt="Units">
  <img src="https://img.shields.io/badge/Languages-EN%20%7C%20ES%20%7C%20FR%20%7C%20IT%20%7C%20PT%20%7C%20DE-7F52FF" alt="i18n">
  <img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg" alt="License">
</p>

---

## 📖 Overview

**Karoo BBrakers** is an advanced active braking telemetry and safety extension engineered specifically for **Hammerhead Karoo (Karoo 2 & Karoo 3)** cycle computers.

Following the signature design language of the [DAVOE75](https://github.com/DAVOE75) family ([Karoo Sentinel](https://github.com/DAVOE75/KarooSentinel), [ALTGRAPH](https://github.com/DAVOE75/ALTGRAPH), [KarooTopoMaps](https://github.com/DAVOE75/KarooTopoMaps)), **Karoo BBrakers** fuses internal motion sensors with live Karoo speed, cadence, and power streams to calculate **instantaneous deceleration**, **kinetic braking power (Watts)**, **continuous thermodynamic temperature (°C / °F)**, and **cumulative brake pad wear**.

---

## ⚡ High-Precision Physics Processing ("Finísimo")

### 1. 💨 Coasting Inertia Drag Subtraction vs Real Braking
* **Slowing down does not equal braking:** When a rider stops pedaling (freewheeling/coasting), the bike naturally loses speed due to **aerodynamic drag** ($F_{aero} = \frac{1}{2} \rho C_d A v^2$) and **tire rolling resistance** ($F_{rr} = C_{rr} m g$).
* The sensor fusion engine continuously models this passive aerodynamic and rolling deceleration according to rider mass and airspeed, subtracting it from accelerometer readings:
  $$a_{net} = \max(0, a_{measured} - a_{drag}(v))$$
  Freewheeling on flats or gently rolling to a stop never triggers a false brake event.

### 2. 🎛️ "Brake Bite" Touch & "Brake Release" Detection
* **Brake Bite (Lever Touch):** The physical instant brake pads touch the rotor or rim track, it induces a distinct positive jerk ($\frac{da}{dt} > 0.65\text{ m/s}^3$) and a subtle chassis pitch-down moment on the handlebars. The algorithm catches the exact start of mechanical friction.
* **Brake Release:** Opening the brake levers triggers a sharp negative jerk ($\frac{da}{dt} < -1.2\text{ m/s}^3$) and fork rebound, cleanly closing the brake event and transitioning smoothly back to coasting (`COASTING`).

### 3. 🎯 Strict Ramp and Incline Rejection
* **Pedaling Lockout:** If pedaling cadence (> 5 RPM) or power (> 25 W) is detected, brake detection is strictly locked out to 0. Speed loss when grinding up a steep ramp will never register as braking.
* **Incline Tilt Decoupling:** Continuous low-pass gravity filtering isolates static road slope angles ($\theta$), ensuring that coasting onto an uphill pitch is not confused with forward deceleration.

### 4. 🔥 Thermodynamic Heat Model (Carbon Rims & Disc Rotors)
* **Continuous Energy Dissipation:** Models thermal input ($Q_{in} \approx 82\% \times P_{braking}$) balanced against convective airspeed cooling ($Q_{out} \propto v^{0.75} \cdot \Delta T$).
* **Carbon Rim Overheating Protection:**
  * Delivers both audible beeper chimes and visual banners upon reaching **145°C / 293°F** on rim brake carbon tracks, preventing resin glass transition temperature breakdown and rim delamination.
  * Dedicated thermal limits also configured for disc rotors (**260°C / 500°F**) and aluminum rims (**190°C / 374°F**).

---

## 🌍 Complete Regional Units Support

Tailored for cyclists worldwide with single-tap preset switching and auto-detection based on language/locale:

| Region | System | Temperature | Weight | Deceleration | Speed |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **Germany, Spain, France, Italy, EU** | **Metric** | **°C** (Celsius) | **kg** (40–160 kg) | **m/s²** | **km/h** |
| **United Kingdom (UK) & United States (USA)** | **Imperial** | **°F** (Fahrenheit) | **lbs** (90–350 lbs) | **G-Force (G) / ft/s²** | **mph** |

*Riders can select the complete regional preset or customize temperature, weight, and deceleration units individually in Settings.*

---

## 🎨 Logo & Visual Branding (DAVOE75 Style)

The heraldic emblem matches the aesthetic of **Karoo Sentinel**, **ALTGRAPH**, and **Karoo Topo Maps**:
* **Background:** Deep carbon fiber weave black texture.
* **Bicycle:** Clean white silhouette road bike geometry.
* **Heated Rotors:** Glowing orange/red thermal effect on wheel hubs.
* **Brake Intensity Scale:** Progressive segmented spectrum evolving from **White** (gentle initial pad bite), through **Pale Yellow**, **Pure Yellow**, **Orange**, up to **Fiery Red** (maximum deceleration and emergency heat load).

---

## 📊 Karoo Data Fields

| Field ID | Name | Unit | Description |
| :--- | :--- | :---: | :--- |
| `current_decel` | **Braking Force** | m/s², G or ft/s² | Real-time deceleration with aerodynamic drag removed. |
| `braking_power` | **Braking Power** | Watts (W) | Kinetic dissipation rate ($P = m \cdot a_{net} \cdot v$). |
| `max_decel` | **Peak Brake G** | G, m/s² or ft/s² | Highest braking force recorded during the ride. |
| `brake_events` | **Braking Events** | Count | Total count of distinct brake lever applications. |
| `brake_energy` | **Brake Energy** | kJ | Cumulative absorbed thermal energy in kilojoules. |
| `pad_wear` | **Pad Wear Score** | Points | Wear score based on compound type and energy. |
| `brake_temperature` | **Brake Temperature** | °C or °F | Real-time estimated rim/rotor temperature with alert limit. |
| `graphical_brake` | **Brake Gauge** | White to Red Spectrum | Dynamic Glance graphical meter with peak marker and state badge (`BRAKE`, `PEDAL`, `COAST`, `ARMED`). |

---

## 🛠️ Build & Installation

Prerequisites: **Android Studio**, **JDK 17**, and **Android SDK (API 34)**.

```bash
# Compile debug APK
./gradlew assembleDebug

# Install to Karoo over USB (ADB)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Compiled APK binary location:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🌐 Full Internationalization (6 Languages)
* 🇪🇸 **Español** (`res/values-es/`)
* 🇬🇧 **English** (`res/values/`)
* 🇩🇪 **Deutsch** (`res/values-de/`)
* 🇫🇷 **Français** (`res/values-fr/`)
* 🇮🇹 **Italiano** (`res/values-it/`)
* 🇵🇹 **Português** (`res/values-pt/`)

---

## 📄 License

Licensed under the **Apache License 2.0**. Compatible with Hammerhead Karoo 2 and Karoo 3.
GitHub Repository: [https://github.com/DAVOE75/KarooBBrakers](https://github.com/DAVOE75/KarooBBrakers)
