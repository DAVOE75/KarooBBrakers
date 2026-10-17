# 🛑 Karoo BBrakers
### *Braking Telemetry, Dynamic Safety & Wear Analytics for Hammerhead Karoo*

<p align="center">
  <img src="app/src/main/res/drawable/app_logo.jpg" alt="Karoo BBrakers Logo" width="220" style="border-radius: 24px; box-shadow: 0 8px 24px rgba(0,0,0,0.5);"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Karoo_OS-KOS_1.634+-FF6D00?logo=android&logoColor=white" alt="Karoo OS">
  <img src="https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Languages-EN%20%7C%20ES%20%7C%20FR%20%7C%20IT%20%7C%20PT%20%7C%20DE-00E5FF" alt="i18n">
  <img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg" alt="License">
</p>

---

## 📖 Overview

**Karoo BBrakers** is an advanced cycling telemetry and safety extension designed for **Hammerhead Karoo (Karoo 2 & Karoo 3)** bike computers. By merging internal high-frequency inertial sensors (accelerometer / linear acceleration) with Karoo speed, cadence, and power telemetry, **Karoo BBrakers** accurately measures real-time deceleration force, kinetic power dissipation, and brake pad wear—while preventing false alarms on climbs and rough roads.

Complementing safety tools like [Karoo Sentinel](https://github.com/DAVOE75/KarooSentinel), **Karoo BBrakers** transforms your ride data with professional telemetry, automatic FIT developer fields, and live audible/visual alerts.

---

## ⚡ Key Features

### 1. 🎯 Smart Sensor Fusion & Incline Rejection
* **Zero False Positives on Climbs:** If the rider is actively pedaling (Cadence > 5 RPM or Power > 25W), brake detection is strictly locked out. Velocity drops caused by approaching an uphill ramp or steep grade are never confused with braking.
* **Vibration Suppression:** Adaptive low-pass filters eliminate high-frequency spikes caused by road chatter, potholes, cobblestones, and trail chatter.
* **Jerk & Onset Analysis:** Recognizes the physical "bite" signature ($\Delta a / \Delta t$) unique to disc and rim brake pads.

### 2. ⚖️ Vehicle Weight & Kinetic Physics ($P = m \cdot a \cdot v$)
* Real braking power is directly proportional to the total vehicle mass ($M = m_{cyclist} + m_{bike} + m_{gear}$).
* Configurable total mass (40 kg to 160 kg) enables precise calculations of:
  * **Braking Force ($F$):** $F = M \cdot a$ (Newtons)
  * **Instantaneous Braking Power ($P$):** $P = F \cdot v = M \cdot a \cdot v$ (Watts)
  * **Dissipated Energy ($E$):** $E = \int P \, dt$ (Kilojoules)

### 3. 🚴 Brake System Customization & Wear Analytics
Configure your specific brake hardware:
* **Disc Brakes (Hydraulic / Mechanical):** Benchmark high friction & heat resistance.
* **Rim Brakes (Aluminum Track):** Adjusted wear factor (1.35x) and heat thresholds.
* **Rim Brakes (Carbon Track):** Sensitive thermal threshold (16 kJ) to protect carbon fiber rims from delamination during sustained Alpine descents.
* **Pad Wear Index:** Continuously accumulates wear points based on absorbed energy and pad/rim material friction rates.

### 4. 🚨 Active In-Ride Safety Alerts
* **Emergency Braking Alarm:** When deceleration exceeds safety thresholds ($> 4.2 \text{ m/s}^2$ / $0.43\text{ G}$), Karoo triggers a high-priority audible hardware beeper melody and an on-screen warning banner.
* **Thermal Fading Risk Warning:** Alerts riders during long continuous descents when heat dissipation reaches critical levels, advising periodic brake release for cooling.

### 5. 📊 Real-Time Karoo Data Fields
Add any of these custom metrics to your Karoo ride screens:
* **Braking Force:** Instantaneous deceleration in $\text{m/s}^2$ or $\text{G}$.
* **Braking Power:** Live wattage dissipated by brakes (W).
* **Peak Brake G:** Highest deceleration recorded in the ride or current lap.
* **Braking Events:** Counter of brake applications (helps assess cornering technique and pacing).
* **Brake Energy:** Total cumulative kinetic energy absorbed (kJ).
* **Pad Wear Score:** Wear indicator to track maintenance intervals.
* **Brake Gauge (Graphical):** Dynamic 5-segment color-coded intensity bar (Green $\to$ Amber $\to$ Red) with peak marker.

### 6. 📁 Garmin FIT Developer Fields
All braking metrics are embedded automatically into your `.fit` activity file at 1Hz (`WriteToRecordMesg`) and summarized in session records (`WriteToSessionMesg`), making your deceleration graphs visible in **Strava**, **Intervals.icu**, **GoldenCheetah**, and FIT telemetry analyzers.

---

## 🌍 Multi-Language Support (6 Languages)

Karoo BBrakers is localized in:
* 🇪🇸 **Español**
* 🇬🇧 **English**
* 🇫🇷 **Français**
* 🇮🇹 **Italiano**
* 🇵🇹 **Português**
* 🇩🇪 **Deutsch**

---

## 🛠️ Installation & Setup

### Requirements
* Hammerhead Karoo 2 or Karoo 3
* Karoo OS version `1.634.2440` or later

### Steps
1. Download the latest `KarooBBrakers-vX.X.X.apk` from [Releases](https://github.com/DAVOE75/KarooBBrakers/releases).
2. Install the APK via sideloading (ADB or Karoo sideload tools).
3. Open the **Karoo BBrakers** app from the Karoo App Launcher.
4. Set your **Brake System Type**, **Total Weight** (Rider + Bike + Gear), and **Surface Profile**.
5. Go to your **Ride Profiles** $\to$ **Edit Screens**, tap an empty slot, select **Karoo BBrakers**, and add your preferred data fields.

---

## 🏗️ Building from Source

```bash
# Clone the repository
git clone https://github.com/DAVOE75/KarooBBrakers.git
cd KarooBBrakers

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🤝 Author & Credits

Developed with passion by **[DAVOE75](https://github.com/DAVOE75)**  
Creator of [Karoo Sentinel](https://github.com/DAVOE75/KarooSentinel).

---

## 📄 License

Licensed under the [Apache License, Version 2.0](LICENSE).
