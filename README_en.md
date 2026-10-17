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
  <img src="https://img.shields.io/badge/Languages-EN%20%7C%20ES%20%7C%20FR%20%7C%20IT%20%7C%20PT%20%7C%20DE-00E5FF" alt="i18n">
  <img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg" alt="License">
</p>

---

## 📖 Overview

**Karoo BBrakers** is an advanced cycling telemetry, dynamic safety, and thermal wear analysis extension created for **Hammerhead Karoo (Karoo 2 & Karoo 3)** cycle computers.

Following the signature design and ecosystem of [DAVOE75](https://github.com/DAVOE75) ([Karoo Sentinel](https://github.com/DAVOE75/KarooSentinel), [ALTGRAPH](https://github.com/DAVOE75/ALTGRAPH), [KarooTopoMaps](https://github.com/DAVOE75/KarooTopoMaps)), **Karoo BBrakers** combines internal high-rate inertial accelerometer sensors with live Karoo speed, cadence, and power streams to measure real-time **deceleration ($m/s^2$ or $G$)**, **kinetic braking power (W)**, **continuous brake track temperature estimation (°C)**, and **brake pad wear points**.

---

## ⚡ Key Highlights

### 1. 🎯 Smart Pedaling & Incline Rejection
* **Zero False Alarms on Climbs:** If the rider is pedaling (Cadence > 5 RPM or Power > 25 W), braking is strictly locked to 0. Speed loss caused by heading into an uphill ramp is never confused with braking thanks to physical bite jerk ($\Delta a / \Delta t$) profiling.
* **Surface Profiles:** Road, Gravel, and MTB filtering profiles to eliminate high-frequency chatter from cobblestones, potholes, and trails.

### 2. 🔥 Thermodynamic Temperature Model (Carbon Rim & Disc Protection)
* Continuous integration of thermal input ($Q_{in} = \eta \cdot P_{brake}$) and convective cooling by airflow speed ($Q_{out} = (k_0 + k_1 \cdot v^{0.75}) \cdot \Delta T$).
* **Carbon Rim Overheating Protection:** Warns at 105°C and alerts critically at 145°C to protect delicate carbon resin from heat softening and delamination during long Alpine descents.
* Tailored thermal mass models for **Disc Rotors**, **Aluminum Rims**, and **Carbon Rims**.

### 3. ⚖️ Vehicle Weight & Kinetic Power ($P = m \cdot a \cdot v$)
* Enter total mass (Rider + Bike + Gear, 40 to 160 kg) for authentic kinetic force (N), power (W), and absorbed energy (kJ).

### 4. 🚨 Active In-Ride Alarms
* **Hard Braking:** Hardware beeper pattern and on-screen banner on sudden stops ($> 4.2 \text{ m/s}^2$).
* **Brake Overheating:** Timely warning to pulse/release brakes intermittently for convective cooling.

### 5. 📁 FIT Developer Fields (Strava & Intervals.icu)
Automatic 1Hz record and session metrics embedded directly into your ride `.fit` files.

---

## 🤝 Author

Developed by **[DAVOE75](https://github.com/DAVOE75)**  
Creator of [Karoo Sentinel](https://github.com/DAVOE75/KarooSentinel), [ALTGRAPH](https://github.com/DAVOE75/ALTGRAPH), and [KarooTopoMaps](https://github.com/DAVOE75/KarooTopoMaps).

---

## 📄 License

Licensed under the [Apache License, Version 2.0](LICENSE).
