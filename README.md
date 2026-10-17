<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### WorldTour PRO Braking Telemetry, Corner Radar & Apex Speed, Kamm Grip Limit, Metabolic Energy & Peloton Smoothness (PEI) for Hammerhead Karoo 2 & Karoo 3

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
**Karoo BBrakers** is a comprehensive cycling braking telemetry, inertial dynamics, predictive safety, and race intelligence extension engineered specifically for the **Hammerhead Karoo** platform (Karoo 2 and Karoo 3).

Modern cycling performance analysis has focused almost exclusively on power meters, heart rate, and cadence. Yet, in competitive road racing, alpine descents, gravel riding, and mountain biking, **races are won and crashes avoided through braking efficiency, late-apex trail braking, corner exit speed carry, and thermal management**. Karoo BBrakers turns your Hammerhead cycling computer into a WorldTour telemetry cockpit.

---

## Core Physics & WorldTour Engineering

### 1. Stationary Zero-Lockout & False Positive Immunity (< 4 km/h)
- Without physical micro-switches or lever hydraulic transducers, a desk test or stationary bike must never register braking.
- When GPS or wheel sensor speed is under $1.11\text{ m/s}$ ($4.0\text{ km/h}$), all deceleration vectors are strictly zeroed ($0.00\text{ m/s}^2$). The system remains in the rock-solid **ARMED** state and instant detection activates the millisecond the bike begins rolling.

### 2. Gravity Pitch Decoupling on Steep Climbs
- On steep uphill gradients, gravity pulls backward on the internal accelerometer along the bike's longitudinal axis ($g \cdot \sin(\theta)$).
- Karoo BBrakers calculates instantaneous pitch angle $\theta = \arcsin\left(\frac{-g_y}{9.80665}\right)$ and subtracts the uphill gravity component:
  $$a_{\text{forward, net}} = a_{\text{measured, y}} - \max(0, g \cdot \sin(\theta))$$
- Steep $15\% - 25\%$ climbs never register false deceleration.

### 3. Corner Radar & Apex Speed Retention (0–100%)
- Tracks entry speed ($v_{\text{entry}}$), apex minimum speed ($v_{\text{apex}}$), and exit acceleration ($\Delta v_{\text{exit}}$).
- Calculates the **Apex Speed Retention %**:
  $$\text{Apex Retention} = \left(\frac{v_{\text{apex}}}{v_{\text{entry}}}\right) \cdot 100\%$$
- WorldTour technical descenders aim for $> 75\%$ retention on sweeping bends, carrying kinetic energy rather than over-braking.

### 4. Predictive Corner Braking Warnings
- Using upcoming turn curvature and speed, the extension computes real-time stopping distance:
  $$d_{\text{stop}} = \frac{v_{\text{current}}^2 - v_{\text{safe}}^2}{2 \cdot a_{\text{safe}}}$$
- Triggers a proactive audio beep and Karoo `InRideAlert` with target deceleration distance ahead of tight bends.

### 5. Kamm’s Friction Circle & Washout Alarm
- Total tire adhesion is limited by friction:
  $$F_{\text{total}} = \sqrt{F_{\text{braking}}^2 + F_{\text{cornering}}^2} \le \mu \cdot N$$
- In wet conditions (`WET_ROAD` profile, $\mu \approx 0.45$), combining hard braking with roll lean risks instantaneous front-wheel washout.
- Karoo BBrakers continuously calculates your **Grip Margin %** and triggers an emergency alarm when margin drops below critical thresholds.

### 6. Wasted Metabolic Energy Burned (kJ & kcal)
- Every brake event destroys mechanical kinetic energy ($\Delta E = \frac{1}{2} m (v_1^2 - v_2^2)$).
- Based on human gross muscular efficiency ($\eta \approx 21.5\%$), recovering that momentum requires burning nearly 5 times more metabolic glycogen:
  $$E_{\text{metabolic}} = \frac{\Delta E_{\text{kinetic}}}{0.215}$$
- Tracks total kilojoules and kilocalories burned due to braking over the course of the ride.

### 7. Peloton Smoothness Index (PEI 0–100%)
- Micro-braking in a paceline triggers backward accordion shockwaves, forcing trailing riders into repeated 800W sprint closures.
- Over a rolling 60-second window, the PEI scores drafting fluidity from 0 to 100%, coaching riders to maintain position through aerodynamic drag modulation rather than brake snatching.

### 8. Descent Efficiency Score (DES 0–100%)
- Alpine descents reward short, firm braking pulses before corner entry and penalize continuous lever dragging.
- Continuous dragging prevents rotor heat shedding and leads to thermal glazing (resin vitrification).
- The DES algorithm scores every descent from 0 to 100%, rewarding riders who brake cleanly and release levers to allow convective cooling.

### 9. Dynamic Load Transfer & Front/Rear Bias
- When a cyclist decelerates, inertia shifts weight forward onto the front tire contact patch:
  $$\Delta W = \frac{m \cdot a \cdot h_{\text{CG}}}{L_{\text{wheelbase}}}$$
- On downhill descents, front wheel normal load can surge up to **85%**. Karoo BBrakers computes real-time **Front vs Rear braking distribution**, helping riders optimize lever modulation.

### 10. Digital Pad Service Odometer
- Tracks cumulative lifetime energy absorbed by brake pads in kilojoules ($\text{kJ}$):
  - **Resin / Organic**: ~2500 kJ rated capacity.
  - **Semi-Metallic**: ~4500 kJ rated capacity.
  - **Sintered / Metallic**: ~7000 kJ rated capacity.
- Displays **Pad Life Remaining %** with one-touch odometer reset after servicing.

### 11. Dual Thermodynamic Heat & Pad Glazing Warnings
- **Differential Rotor Cooling**: Front calipers receive clean laminar airflow ($h_{\text{front}} \propto v^{0.8}$), while rear calipers are shielded in turbulent chainstay air ($h_{\text{rear}} \approx 0.65 \cdot h_{\text{front}}$).
- **Material Safety Limits**:
  - **Disc Brakes**: Safe up to 350°C.
  - **Rim Brakes (Aluminium)**: Safe up to 150°C.
  - **Rim Brakes (Carbon Wheel Track)**: High warning at 145°C, critical delamination alarm at **180°C**.

### 12. Involuntary Brake Drag Detection (Wasted Watts)
- Continuously identifies stuck caliper pistons, warped rotors, or pad rubbing while pedaling.
- Calculates instantaneous **Wasted Watts** ($P = F_{\text{rub}} \cdot v$) so riders can immediately detect parasitic power loss.

---

## Complete Data Fields Catalog (17 Native Karoo Types)

Karoo BBrakers registers 17 native DataTypes in Karoo OS (`extension_info.xml`):

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
| `corner_radar` | **Corner Radar & Apex Speed** | Numeric | Live corner entry, apex speed retention and exit acceleration telemetry. |
| `metabolic_burn` | **Wasted Metabolic Energy** | Numeric | Metabolic kilojoules wasted in braking based on 21.5% muscular efficiency. |
| `peloton_smoothness`| **Peloton Smoothness Index** | Numeric | Group ride drafting efficiency (0-100%) and accordion shockwave resistance. |
| `graphical_brake` | **Brake Gauge (Graphical)**| Graphic | 5-segment color gauge (White to Yellow to Orange to Red). |
| `graphical_thermal`| **Thermal & Fade Monitor** | Graphic | Dual front/rear temperatures, safety zones, and material limits. |
| `graphical_cockpit`| **Brake Cockpit HUD** | Graphic | Comprehensive glance HUD with gauge, power, temp, lean, and trail badge. |

---

## FIT Developer Data Fields & Analytics (WKO5 & GoldenCheetah)

During recorded rides, Karoo BBrakers injects 12 standard FIT Developer Fields at 1 Hz:
- `BrakeDecel` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` ($0/1\text{ flag}$)
- `BrakeTemperature` ($^\circ\text{C}$)
- `DescentEfficiency` ($\%$)
- `GripMargin` ($\%$)
- `MetabolicBurn` ($\text{kJ}$)
- `PelotonSmoothness` ($\%$)
- `ApexRetention` ($\%$)
- Summary Session fields: `TotalBrakeEvents`, `MaxBrakeDecel`, `TotalBrakeEnergy`, and `PadWearScore`.

For detailed WKO5 chart formulas, GoldenCheetah scripts, and WorldTour coaching models, see the [WKO5 Integration Guide](wko5/README.md).

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
