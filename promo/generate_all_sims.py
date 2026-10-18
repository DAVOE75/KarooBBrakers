import os
import sys
sys.path.append(r'c:\KarooBrake\promo')
import sim_components
import sim_telemetry

BASE_IM1 = r'C:\Users\davoe\.gemini\antigravity-ide\brain\fb693ae3-5968-42ff-9e06-d242b8372b84\.user_uploaded\media_1792319581769.png'
BASE_IM2 = r'C:\Users\davoe\.gemini\antigravity-ide\brain\fb693ae3-5968-42ff-9e06-d242b8372b84\.user_uploaded\media_1792319581776.png'
OUT_DIR = r'c:\KarooBrake\promo'

# -------------------------------------------------------------
# SIMULATION 1: HARD BRAKING INTO HAIRPIN / CURVA 180° (DESCENSO PUERTO)
# -------------------------------------------------------------
data_hard_brake = [
    {'label': '', 'val': '168', 'unit': '°C', 'val_color': (255, 145, 0, 255), 'border_color': (0, 230, 118, 255)}, # Front Temp warm/hot
    {'label': '', 'val': '32.4', 'unit': 'km/h', 'val_color': (0, 229, 255, 255), 'border_color': (0, 229, 255, 255)}, # Speed dropping
    {'label': 'EVENTOS DE FRENADA', 'val': '24', 'unit': '', 'val_color': (255, 255, 255, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'FUERZA DE FRENADA', 'val': '4.8', 'unit': 'm/s²', 'val_color': (255, 23, 68, 255), 'border_color': (255, 23, 68, 255)}, # Fiery red
    {'label': 'POTENCIA DE FRENADA', 'val': '1120', 'unit': 'W', 'val_color': (0, 229, 255, 255), 'border_color': (255, 145, 0, 255)},
    {'label': 'PICO DE FRENADA G', 'val': '0.58', 'unit': 'm/s²', 'val_color': (255, 214, 0, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'ENERGÍA DISIPADA', 'val': '194.2', 'unit': 'kJ', 'val_color': (255, 255, 255, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'DESGASTE ESTIMADO', 'val': '7.3', 'unit': '', 'val_color': (255, 255, 255, 255), 'border_color': (0, 229, 255, 255)},
]
sim_telemetry.render_8cell_telemetry(
    BASE_IM1, data_hard_brake,
    os.path.join(OUT_DIR, '01_grid8_hard_braking.png')
)

top1 = sim_components.render_top_tile(
    decel_str="4.8", unit="m/s²", status="FRENANDO", peak="0.58G",
    front_bias=65, rear_bias=35, is_braking=True, intensity_pct=85
)
therm1 = sim_components.render_thermal_widget(
    front_temp=168, rear_temp=115, peak_temp=185,
    status="CALIENTE", critical_temp=350, brake_name="Disco", temp_status="HOT"
)
cock1 = sim_components.render_cockpit_widget(
    decel_str="4.8", unit="m/s²", power_w=1120,
    status="BRAKE", peak="0.58G", event_no=24,
    front_temp=168, rear_temp=115, is_braking=True,
    intensity_pct=85, is_descending=True, des_score=92
)
sim_telemetry.render_overview_screen(
    BASE_IM2, top1, therm1, cock1,
    os.path.join(OUT_DIR, '02_overview_hard_braking.png')
)

# -------------------------------------------------------------
# SIMULATION 2: HIGH SPEED DESCENT & TRAIL-BRAKING (WORLDTOUR DES)
# -------------------------------------------------------------
data_descent = [
    {'label': '', 'val': '124', 'unit': '°C', 'val_color': (0, 229, 255, 255), 'border_color': (0, 230, 118, 255)},
    {'label': '', 'val': '58.6', 'unit': 'km/h', 'val_color': (0, 229, 255, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'EVENTOS DE FRENADA', 'val': '42', 'unit': '', 'val_color': (255, 255, 255, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'FUERZA DE FRENADA', 'val': '2.1', 'unit': 'm/s²', 'val_color': (255, 145, 0, 255), 'border_color': (255, 145, 0, 255)},
    {'label': 'POTENCIA DE FRENADA', 'val': '580', 'unit': 'W', 'val_color': (0, 229, 255, 255), 'border_color': (255, 145, 0, 255)},
    {'label': 'PICO DE FRENADA G', 'val': '0.64', 'unit': 'm/s²', 'val_color': (255, 214, 0, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'ENERGÍA DISIPADA', 'val': '342.8', 'unit': 'kJ', 'val_color': (255, 255, 255, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'DESGASTE ESTIMADO', 'val': '14.2', 'unit': '', 'val_color': (255, 255, 255, 255), 'border_color': (0, 229, 255, 255)},
]
sim_telemetry.render_8cell_telemetry(
    BASE_IM1, data_descent,
    os.path.join(OUT_DIR, '03_grid8_trail_braking.png')
)

top2 = sim_components.render_top_tile(
    decel_str="2.1", unit="m/s²", status="TRAIL", peak="0.64G",
    front_bias=55, rear_bias=45, is_braking=True, intensity_pct=45
)
therm2 = sim_components.render_thermal_widget(
    front_temp=124, rear_temp=98, peak_temp=145,
    status="TEMPLADO", critical_temp=350, brake_name="Disco", temp_status="WARM"
)
cock2 = sim_components.render_cockpit_widget(
    decel_str="2.1", unit="m/s²", power_w=580,
    status="TRAIL", peak="0.64G", event_no=42,
    front_temp=124, rear_temp=98, is_braking=True,
    intensity_pct=45, is_descending=True, des_score=97
)
sim_telemetry.render_overview_screen(
    BASE_IM2, top2, therm2, cock2,
    os.path.join(OUT_DIR, '04_overview_trail_braking.png')
)

# -------------------------------------------------------------
# SIMULATION 3: PELOTON PACELINE / GRUPETA SMOOTHNESS & POST-RIDE STATS
# -------------------------------------------------------------
data_peloton = [
    {'label': '', 'val': '68', 'unit': '°C', 'val_color': (0, 230, 118, 255), 'border_color': (0, 230, 118, 255)},
    {'label': '', 'val': '44.8', 'unit': 'km/h', 'val_color': (0, 229, 255, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'EVENTOS DE FRENADA', 'val': '58', 'unit': '', 'val_color': (255, 255, 255, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'FUERZA DE FRENADA', 'val': '0.0', 'unit': 'm/s²', 'val_color': (255, 255, 255, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'POTENCIA DE FRENADA', 'val': '0', 'unit': 'W', 'val_color': (255, 255, 255, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'PICO DE FRENADA G', 'val': '0.71', 'unit': 'm/s²', 'val_color': (255, 214, 0, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'ENERGÍA DISIPADA', 'val': '512.6', 'unit': 'kJ', 'val_color': (255, 255, 255, 255), 'border_color': (0, 229, 255, 255)},
    {'label': 'DESGASTE ESTIMADO', 'val': '19.8', 'unit': '', 'val_color': (255, 255, 255, 255), 'border_color': (0, 229, 255, 255)},
]
sim_telemetry.render_8cell_telemetry(
    BASE_IM1, data_peloton,
    os.path.join(OUT_DIR, '05_grid8_peloton_smoothness.png')
)

top3 = sim_components.render_top_tile(
    decel_str="0.0", unit="m/s²", status="ARMADO", peak="0.71G",
    front_bias=60, rear_bias=40, is_braking=False, intensity_pct=0
)
therm3 = sim_components.render_thermal_widget(
    front_temp=68, rear_temp=54, peak_temp=210,
    status="ÓPTIMO", critical_temp=350, brake_name="Disco", temp_status="NORMAL"
)
cock3 = sim_components.render_cockpit_widget(
    decel_str="0.0", unit="m/s²", power_w=0,
    status="ARMADO", peak="0.71G", event_no=58,
    front_temp=68, rear_temp=54, is_braking=False,
    intensity_pct=0, is_descending=False, des_score=94
)
sim_telemetry.render_overview_screen(
    BASE_IM2, top3, therm3, cock3,
    os.path.join(OUT_DIR, '06_overview_cruising.png')
)

print("\n=== ALL 6 REALISTIC SIMULATION SCREENS GENERATED! ===")
