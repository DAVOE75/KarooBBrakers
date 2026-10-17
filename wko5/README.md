# Karoo BBrakers: WorldTour PRO Telemetry & Analytics Guide

## WKO5 & GoldenCheetah Integration

Karoo BBrakers records 1Hz high-frequency sensor fusion channels directly into the `.fit` activity file as standard FIT Developer Fields (FIT Protocol 2.0).

---

## 1. FIT Developer Data Dictionary

| Field ID | Name | Type | Units | Target Record | Description |
|:---|:---|:---|:---|:---|:---|
| `0` | `BrakeDecel` | `Float32` | `m/s²` | `record` (1Hz) | Real-time net forward deceleration (gravity decoupled) |
| `1` | `BrakingPower` | `UInt16` | `Watts` | `record` (1Hz) | Instantaneous kinetic dissipation rate ($P = m \cdot v \cdot a$) |
| `2` | `IsBraking` | `UInt8` | `flag` | `record` (1Hz) | Boolean brake bite state (`0` = coasting/pedaling, `1` = active bite) |
| `7` | `BrakeTemperature` | `Float32` | `°C` | `record` (1Hz) | Dynamic rotor surface temperature model |
| `8` | `DescentEfficiency` | `UInt8` | `%` | `record` (1Hz) | Descent Efficiency Score (DES 0-100%) |
| `9` | `GripMargin` | `UInt8` | `%` | `record` (1Hz) | Kamm circle adhesion margin ($1 - \frac{a_{\text{comb}}}{\mu \cdot g}$) |
| `10` | `MetabolicBurn` | `UInt16` | `kJ` | `record` (1Hz) | Cumulative metabolic energy lost ($E_{\text{kinetic}} / 0.215$) |
| `11` | `PelotonSmoothness` | `UInt8` | `%` | `record` (1Hz) | Peloton drafting fluidity index (PEI 0-100%) |
| `12` | `ApexRetention` | `UInt8` | `%` | `record` (1Hz) | Corner apex speed retention percentage ($\frac{v_{\text{apex}}}{v_{\text{entry}}}$) |
| `3` | `TotalBrakeEvents` | `UInt16` | `count` | `session` | Total discrete deceleration events in session |
| `4` | `MaxBrakeDecel` | `Float32` | `m/s²` | `session` | Peak recorded deceleration during the ride |
| `5` | `TotalBrakeEnergy` | `Float32` | `kJ` | `session` | Total mechanical kinetic energy absorbed by rotors |
| `6` | `PadWearScore` | `Float32` | `score` | `session` | Cumulative pad wear points |

---

## 2. WKO5 Custom Chart Expressions

### Chart 1: Wasted Metabolic Energy vs. Normalized Power (NP)
*Identifies how many kilojoules of aerobic/anaerobic work were destroyed by brake hesitation.*

```text
[Expression]
metabolic_rate = ddt(developer("MetabolicBurn")) * 1000
smooth_power = sdate(power, 5)

[Chart Configuration]
Series 1: power (Line, Color: Gold, Axis: Left)
Series 2: metabolic_rate (Line, Color: Red, Axis: Left)
Series 3: developer("MetabolicBurn") (Area, Color: Deep Orange, Alpha: 0.25, Axis: Right)
```

### Chart 2: Descent Efficiency & Rotor Thermal Saturation
*Analyzes brake dragging on alpine descents against pad glazing thresholds.*

```text
[Expression]
rotor_temp = developer("BrakeTemperature")
des_score = developer("DescentEfficiency")

[Chart Configuration]
Series 1: elevation (Filled Curve, Color: Charcoal, Axis: Left)
Series 2: speed (Line, Color: Cyan, Axis: Right)
Series 3: rotor_temp (Line, Color: Crimson, Axis: Right 2, Threshold: 220)
Series 4: des_score (Bar, Color: Green/Orange, Axis: Left 2)
```

### Chart 3: Corner Apex Speed Retention & Traction Limit (Kamm Circle)
*Quantifies technical cornering speed carry through sharp hairpins.*

```text
[Expression]
retention = developer("ApexRetention")
grip_reserve = developer("GripMargin")

[Chart Configuration]
Series 1: speed (Line, Color: Cyan, Axis: Left)
Series 2: retention (Scatter/Points, Color: Bright Green, Axis: Right)
Series 3: grip_reserve (Line, Color: Amber, Axis: Right 2, Warning: < 20)
```

### Chart 4: Peloton Fluidity & Drafting Shockwave (PEI)
*Measures position economy and draft stability within the peloton.*

```text
[Expression]
draft_smoothness = developer("PelotonSmoothness")
cadence_smooth = sdate(cadence, 3)

[Chart Configuration]
Series 1: speed (Line, Color: Light Blue, Axis: Left)
Series 2: draft_smoothness (Area, Color: Violet, Axis: Right, Min: 0, Max: 100)
Series 3: power (Line, Color: Yellow, Axis: Left 2)
```

---

## 3. GoldenCheetah Custom Metrics (Python / R)

In GoldenCheetah, create an R or Python data filter:

```python
# GoldenCheetah Data Processing Script
# Computes Brake Hesitation Index (BHI)
import numpy as np

decel = data['BrakeDecel']
speed = data['Speed']
power = data['Power']

# Filter only moving segments
moving_mask = speed > 4.0

# Calculate hesitation events (micro-brakes with deceleration < 1.2 m/s2 while speed > 30 km/h)
hesitations = np.sum((decel > 0.4) & (decel < 1.2) & (speed > 8.33))
total_moving_time = np.sum(moving_mask)

# Hesitation frequency per hour of descending/high-speed riding
hesitation_index = (hesitations / total_moving_time) * 3600.0
print(f"Brake Hesitation Index (BHI): {hesitation_index:.2f} events/hour")
```

---

## 4. WorldTour Coaching Guidelines

1. **Descent Efficiency (DES > 85%)**: High DES indicates late, short, and assertive braking points before turn-in. Low DES (< 65%) flags continuous brake dragging, resulting in premature pad glazing and thermal fade.
2. **Metabolic Energy Preservation**: On a 2,000 kJ mountain stage, top descending riders waste less than 45 kJ of kinetic energy needlessly, compared to > 180 kJ for inexperienced climbers.
3. **Peloton Smoothness Index (PEI > 80%)**: High PEI reflects smooth line selection and distance management without disruptive accordion braking. Low PEI (< 50%) signifies reactive micro-braking followed by high-wattage sprint closures.
