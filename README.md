<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### WorldTour PRO Braking Telemetry, Trail-Braking Radar, Descent Efficiency & Digital Pad Odometer for Hammerhead Karoo 2 & Karoo 3

<p align="center">
  <b>Language:</b> 
  <a href="README.md"><b>English</b></a> | 
  <a href="README_ES.md"><b>Español</b></a> | 
  <a href="README_DE.md"><b>Deutsch</b></a> | 
  <a href="README_FR.md"><b>Français</b></a> | 
  <a href="README_IT.md"><b>Italiano</b></a> | 
  <a href="README_PT.md"><b>Português</b></a>
</p>

<p align="center">
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases/latest"><img src="https://img.shields.io/github/v/release/DAVOE75/KarooBBrakers?style=flat-square&logo=android&logoColor=white&label=Version&color=00C853" alt="Latest Version" /></a>
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases"><img src="https://flat.badgen.net/github/assets-dl/DAVOE75/KarooBBrakers?label=Downloads&color=0091EA&icon=github" alt="Total Downloads" /></a>
  <img src="https://img.shields.io/badge/Device-Karoo_2_•_Karoo_3-1B1B1B?style=flat-square&logo=googlemaps&logoColor=00E676" alt="Karoo Compatibility" />
  <a href="https://github.com/DAVOE75/KarooBBrakers/blob/main/LICENSE"><img src="https://img.shields.io/github/license/DAVOE75/KarooBBrakers?style=flat-square&label=License&color=455A64" alt="License" /></a>
  <a href="https://buymeacoffee.com/hesiox"><img src="https://img.shields.io/badge/Donate-Buy_Me_a_Coffee-FF813F?style=flat-square&logo=buy-me-a-coffee&logoColor=white" alt="Buy Me A Coffee" /></a>
</p>

---

## Overview
**Karoo BBrakers** is a comprehensive cycling braking telemetry, inertial dynamics, and active safety extension engineered specifically for the **Hammerhead Karoo** platform (Karoo 2 and Karoo 3).

Modern cycling performance analysis has focused almost exclusively on power meters, heart rate, and cadence. Yet, in competitive road racing, alpine descents, gravel riding, and mountain biking, **races are won and crashes avoided through braking efficiency, late-apex trail braking, and thermal management**. Karoo BBrakers turns your Hammerhead cycling computer into a WorldTour telemetry cockpit.

---

## Core Physics & WorldTour Engineering

### 1. Dynamic Inertial Deceleration (50 Hz Hardware Sampling)
- Utilizes the Karoo's internal hardware IMU (`Sensor.TYPE_LINEAR_ACCELERATION` and `Sensor.TYPE_ACCELEROMETER`) running at **50 Hz (20 ms updates)**.
- **Continuous Decoupled Calculation**: Gravity pitch tilt and road grade are isolated using continuous complementary filters. Deceleration is measured directly in physical units ($\text{m/s}^2$ or $G$-Force) with instant responsiveness.
- **Aerodynamic & Rolling Resistance Baseline Filtering**: Natural coasting drag caused by wind resistance ($F_{aero} = \frac{1}{2}\rho C_d A v^2$) and tire rolling friction ($C_{rr}$) is mathematically decoupled so that coasting on flats is never falsely detected as braking.

### 2. Descent Efficiency Score (DES 0–100%)
- Alpine descents reward short, firm braking pulses before corner entry and penalize continuous lever dragging.
- Continuous dragging prevents rotor heat shedding and leads to thermal glazing (resin vitrification).
- The DES algorithm scores every descent from 0 to 100%, rewarding riders who brake cleanly and release the levers to allow convective rotor cooling.

### 3. Trail-Braking Radar & Corner Entry Telemetry
- Trail-braking carries braking force past the corner entry point, smoothly tapering deceleration as roll lean angle ($\theta_{lean}$) peaks at the apex.
- Karoo BBrakers tracks roll angle and longitudinal jerk ($da/dt$) in real time to grade modulation into 4 WorldTour classes:
  - **PERFECT**: High entry deceleration smoothly tapered as lean angle deepens to the apex.
  - **GOOD**: Well-controlled modulation with balanced speed reduction.
  - **EARLY RELEASE**: Brakes released prematurely before turn entry, losing momentum.
  - **PANIC LEAN**: Dangerous abrupt braking while already deeply leaned over in the corner.

### 4. Dynamic Load Transfer & Front/Rear Bias
- When a cyclist decelerates, inertia shifts weight forward onto the front tire contact patch:
  $$\Delta W = \frac{m \cdot a \cdot h_{CG}}{L_{wheelbase}}$$
- On downhill descents, front wheel normal load can surge up to **85%**. Karoo BBrakers computes real-time **Front vs Rear braking distribution**, helping riders optimize lever modulation and avoid rear-wheel lockups.

### 5. Kamm’s Friction Circle & Washout Prevention
- A tire possesses a finite vector adhesion limit:
  $$F_{total} = \sqrt{F_{braking}^2 + F_{cornering}^2} \le \mu \cdot N$$
- Combining high braking force with deep cornering lean dramatically increases the risk of catastrophic front-wheel washouts.
- Karoo BBrakers continuously calculates your **Grip Margin %** in real time, alerting riders before exceeding tire slip limits.

### 6. Digital Pad Service Odometer
- Tracks cumulative lifetime energy absorbed by your brake pads in kilojoules ($\text{kJ}$).
- Calibrated ratings per compound:
  - **Resin / Organic**: ~2500 kJ rated capacity. High bite, silent, lower thermal threshold.
  - **Semi-Metallic**: ~4500 kJ rated capacity. Balanced performance for all weather.
  - **Sintered / Metallic**: ~7000 kJ rated capacity. Extreme heat tolerance, maximum longevity.
- Provides real-time **Pad Life Remaining %** and one-touch in-app odometer reset after replacing pads.

### 7. Dual Thermodynamic Heat & Pad Glazing Warnings
- **Differential Rotor Cooling**: Front calipers receive clean laminar airflow ($h_{front} \propto v^{0.8}$), while rear calipers are shielded in turbulent chainstay air ($h_{rear} \approx 0.65 \cdot h_{front}$).
- **Material Safety Limits**:
  - **Disc Brakes**: Safe up to 350°C.
  - **Rim Brakes (Aluminium)**: Safe up to 150°C.
  - **Rim Brakes (Carbon Wheel Track)**: High warning at 145°C, critical delamination alarm at **180°C**.
- **Pad Glazing Acoustic Alarm**: Warns immediately if continuous dragging exceeds 22 seconds at temperatures above the compound vitrification threshold.

### 8. Involuntary Brake Drag Detection (Wasted Watts)
- Continuously identifies stuck caliper pistons, warped rotors, or pad rubbing while pedaling.
- Calculates instantaneous **Wasted Watts** ($P = F_{rub} \cdot v$) so riders can immediately detect parasitic power loss.

### 9. Glance RemoteViews Binder IPC Throttling (8 Hz)
- Data view renders to Hammerhead Karoo OS are throttled to 120 ms (~8 Hz), eliminating Binder transaction pressure and significantly extending battery runtime on long rides.

---

## Complete Data Fields Catalog (14 Karoo Types)

Karoo BBrakers registers 14 native DataTypes in Karoo OS (`extension_info.xml`):

| Type ID | Display Name (English) | Format | Description |
|---|---|---|---|
| `current_decel` | **Braking Deceleration** | Numeric | Instantaneous deceleration in $\text{m/s}^2$, $G$-Force, or $\text{ft/s}^2$. |
| `braking_power` | **Braking Power** | Numeric | Kinetic power dissipated by the brakes in Watts ($W$). |
| `max_decel` | **Max Decel G** | Numeric | Maximum peak deceleration recorded during the ride. |
| `brake_events` | **Brake Events** | Numeric | Total count of distinct braking and feathering actions. |
| `brake_energy` | **Energy Dissipated** | Numeric | Total cumulative energy absorbed by brakes in kilojoules ($\text{kJ}$). |
| `pad_wear` | **Pad Wear Index** | Numeric | Estimated wear score based on friction energy and compound type. |
| `pad_life` | **Brake Pad Life** | Numeric | Estimated remaining pad wear life percentage based on cumulative kJ. |
| `descent_efficiency` | **Descent Efficiency (DES)** | Numeric | WorldTour descent score (0–100%) rewarding firm pulses over dragging. |
| `wasted_watts` | **Wasted Watts (Drag)** | Numeric | Parasitic power lost to brake rubbing or stuck pistons in Watts. |
| `grip_margin` | **Cornering Grip Margin** | Numeric | Kamm friction circle safety buffer percentage before tire slip. |
| `brake_temperature`| **Brake Temperature** | Numeric | Real-time estimated rotor/rim temperature in °C or °F. |
| `graphical_brake` | **Brake Gauge (Graphical)**| Graphic | 5-segment color gauge (White to Yellow to Orange to Red). |
| `graphical_thermal`| **Thermal & Fade Monitor** | Graphic | Dual front/rear temperatures, safety zones, and material limits. |
| `graphical_cockpit`| **Brake Cockpit HUD** | Graphic | Comprehensive glance HUD with gauge, power, temp, lean, and trail badge. |

---

## In-Ride Visual Color Spectrum
The graphical widgets use high-contrast color indexing for instant legibility at 40–70 km/h:
- **White** ($< 1.0\text{ m/s}^2$): Initial pad contact / subtle feathering.
- **Pale Yellow** ($1.0 - 2.0\text{ m/s}^2$): Light modulation / distance trimming.
- **Pure Yellow** ($2.0 - 3.2\text{ m/s}^2$): Standard corner entry braking.
- **Orange** ($3.2 - 4.5\text{ m/s}^2$): Firm deceleration on steep descents.
- **Red** ($> 4.5\text{ m/s}^2$): Emergency hard braking / panic stop alert.
- **Violet**: Peloton feathering active in paceline.
- **Green**: Rotor temperature in optimal safety zone ($< 80^\circ\text{C}$).

---

## FIT Developer Data Fields & Post-Ride Analysis
During recorded rides, Karoo BBrakers injects standard FIT Developer Fields at 1 Hz:
- `BrakingDeceleration` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` ($0/1\text{ flag}$)
- `BrakeTemperature` ($^\circ\text{C}$)
- `DescentEfficiency` ($\%$)
- `GripMargin` ($\%$)
- Summary Session fields: Total Events, Peak G, Absorbed kJ, and Pad Wear Score.

Compatible with **Strava**, **Intervals.icu**, **GoldenCheetah**, and **WKO5**.

---

## Installation & OTA Updates

### Option 1: Direct Manifest Installation on Karoo
1. Copy this Manifest URL:
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. On your Hammerhead Karoo, open **Settings -> Extensions -> Add Extension**.
3. Paste the URL. Karoo OS will install Karoo BBrakers automatically and notify you whenever an update is released.

### Option 2: Sideload via ADB
```bash
adb install -r karoobbrakers.apk
```

---

## Developer & Credits
- **Author**: hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Target Hardware**: Hammerhead Karoo 2 / Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **License**: MIT License
