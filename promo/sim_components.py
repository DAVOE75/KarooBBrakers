import os
from PIL import Image, ImageDraw, ImageFont

FONT_BOLD = r'c:\KarooBrake\app\src\main\res\font\barlow_condensed_bold.ttf'
FONT_SEMIBOLD = r'c:\KarooBrake\app\src\main\res\font\barlow_condensed_semibold.ttf'
FONT_MEDIUM = r'c:\KarooBrake\app\src\main\res\font\barlow_condensed_medium.ttf'
FONT_REGULAR = r'c:\KarooBrake\app\src\main\res\font\barlow_condensed_regular.ttf'

BG_CARBON = (9, 12, 18, 255)
BG_WIDGET = (8, 11, 16, 255)
COLOR_CYAN = (0, 229, 255, 255)
COLOR_GREEN = (0, 230, 118, 255)
COLOR_AMBER = (255, 145, 0, 255)
COLOR_YELLOW = (255, 214, 0, 255)
COLOR_RED = (255, 23, 68, 255)
COLOR_WHITE = (255, 255, 255, 255)
COLOR_MUTED = (120, 144, 156, 255)
COLOR_MUTED_LIGHT = (144, 164, 174, 255)
COLOR_INACTIVE_BAR = (30, 37, 48, 255)

def render_top_tile(decel_str="0.0", unit="m/s²", status="ARMADO", peak="0.00G",
                    front_bias=60, rear_bias=40, is_braking=False, intensity_pct=0):
    img = Image.new('RGBA', (480, 177), BG_CARBON)
    draw = ImageDraw.Draw(img)
    
    # 1. Header: Status on left, Peak on right
    f_header = ImageFont.truetype(FONT_MEDIUM, 22)
    status_color = COLOR_RED if is_braking else COLOR_CYAN
    draw.text((12, 24), status, font=f_header, fill=status_color)
    
    peak_text = f"PICO: {peak}"
    peak_bbox = draw.textbbox((0, 0), peak_text, font=f_header)
    peak_w = peak_bbox[2] - peak_bbox[0]
    draw.text((468 - peak_w, 24), peak_text, font=f_header, fill=COLOR_YELLOW)
    
    # 2. Main Deceleration Number + Unit (centered)
    f_num = ImageFont.truetype(FONT_SEMIBOLD, 56)
    f_unit = ImageFont.truetype(FONT_MEDIUM, 18)
    
    num_color = COLOR_WHITE if is_braking else COLOR_MUTED
    num_bbox = draw.textbbox((0, 0), decel_str, font=f_num)
    num_w = num_bbox[2] - num_bbox[0]
    num_h = num_bbox[3] - num_bbox[1]
    
    unit_bbox = draw.textbbox((0, 0), unit, font=f_unit)
    unit_w = unit_bbox[2] - unit_bbox[0]
    
    total_w = num_w + 6 + unit_w
    start_x = (480 - total_w) // 2
    
    draw.text((start_x, 50), decel_str, font=f_num, fill=num_color)
    draw.text((start_x + num_w + 6, 50 + num_h - 18), unit, font=f_unit, fill=COLOR_CYAN if is_braking else COLOR_CYAN)
    
    # 3. Bar: Progress bar across width
    bar_y0, bar_y1 = 111, 121
    draw.rectangle([11, bar_y0, 466, bar_y1], fill=COLOR_INACTIVE_BAR)
    
    if intensity_pct > 0:
        fill_w = int((466 - 11) * (min(100, intensity_pct) / 100.0))
        # 5 stage colors: white, pale yellow, yellow, orange, red
        if intensity_pct < 20: bar_color = (255, 255, 255, 255)
        elif intensity_pct < 40: bar_color = (255, 241, 118, 255)
        elif intensity_pct < 65: bar_color = COLOR_YELLOW
        elif intensity_pct < 85: bar_color = COLOR_AMBER
        else: bar_color = COLOR_RED
        draw.rectangle([11, bar_y0, 11 + fill_w, bar_y1], fill=bar_color)
        
    # 4. Bottom Row: Front & Rear distribution
    f_sub = ImageFont.truetype(FONT_BOLD, 16)
    draw.text((12, 134), f"D: {front_bias}%", font=f_sub, fill=COLOR_CYAN)
    
    rear_str = f"T: {rear_bias}%"
    rear_bbox = draw.textbbox((0, 0), rear_str, font=f_sub)
    rear_w = rear_bbox[2] - rear_bbox[0]
    draw.text((468 - rear_w, 134), rear_str, font=f_sub, fill=COLOR_AMBER)
    
    # Mini split bar in middle
    split_start = 12 + 65
    split_end = 468 - rear_w - 12
    draw.rectangle([split_start, 138, split_end, 142], fill=(44, 52, 64, 255))
    front_split_w = int((split_end - split_start) * (front_bias / 100.0))
    draw.rectangle([split_start, 138, split_start + front_split_w, 142], fill=COLOR_CYAN if is_braking else (44, 52, 64, 255))
    draw.rectangle([split_start + front_split_w, 138, split_end, 142], fill=COLOR_AMBER if is_braking else (44, 52, 64, 255))
    
    return img

def render_thermal_widget(front_temp=22, rear_temp=22, peak_temp=22,
                          status="SEGURO", critical_temp=350, brake_name="Disco",
                          temp_status="NORMAL"):
    img = Image.new('RGBA', (480, 168), BG_WIDGET)
    draw = ImageDraw.Draw(img)
    
    # Colors for status
    if temp_status == "CRITICAL": overall_color = COLOR_RED
    elif temp_status == "HOT": overall_color = COLOR_AMBER
    elif temp_status == "WARM": overall_color = COLOR_CYAN
    else: overall_color = COLOR_GREEN
    
    # 1. Header Row
    f_hdr = ImageFont.truetype(FONT_MEDIUM, 20)
    draw.text((10, 18), f"CALOR | {status}", font=f_hdr, fill=overall_color)
    
    max_str = f"MÁX: {peak_temp:.0f}°C"
    max_bbox = draw.textbbox((0, 0), max_str, font=f_hdr)
    max_w = max_bbox[2] - max_bbox[0]
    draw.text((470 - max_w, 18), max_str, font=f_hdr, fill=COLOR_YELLOW if peak_temp > 150 else COLOR_WHITE)
    
    # 2. Dual Channel Large Temps
    f_lbl = ImageFont.truetype(FONT_BOLD, 17)
    f_val = ImageFont.truetype(FONT_BOLD, 46)
    
    # Front
    draw.text((70, 52), "DELANTERO", font=f_lbl, fill=COLOR_CYAN)
    f_temp_str = f"{front_temp:.0f}°C"
    f_bbox = draw.textbbox((0, 0), f_temp_str, font=f_val)
    f_w = f_bbox[2] - f_bbox[0]
    draw.text((115 - f_w // 2, 70), f_temp_str, font=f_val, fill=overall_color if front_temp > 120 else COLOR_GREEN)
    
    # Divider
    draw.line([(240, 60), (240, 115)], fill=(44, 52, 64, 255), width=2)
    
    # Rear
    draw.text((320, 52), "TRASERO", font=f_lbl, fill=COLOR_AMBER)
    r_temp_str = f"{rear_temp:.0f}°C"
    r_bbox = draw.textbbox((0, 0), r_temp_str, font=f_val)
    r_w = r_bbox[2] - r_bbox[0]
    draw.text((360 - r_w // 2, 70), r_temp_str, font=f_val, fill=COLOR_CYAN if rear_temp > 150 else COLOR_GREEN)
    
    # 3. 4-Segment Heat Progress Bar
    bar_y = 125
    seg_w = (460 - 3 * 4) // 4
    seg_colors = [COLOR_GREEN, COLOR_CYAN, COLOR_AMBER, COLOR_RED]
    ratio = min(1.0, max(0.1, front_temp / critical_temp))
    active_segs = max(1, int(ratio * 4))
    
    for i in range(4):
        sx = 10 + i * (seg_w + 4)
        c = seg_colors[i] if i < active_segs else COLOR_INACTIVE_BAR
        draw.rectangle([sx, bar_y, sx + seg_w, bar_y + 8], fill=c)
        
    # 4. Sub-Info
    f_sub = ImageFont.truetype(FONT_BOLD, 15)
    draw.text((10, 142), f"Límite Seguridad: {critical_temp:.0f}°C", font=f_sub, fill=COLOR_MUTED)
    
    name_bbox = draw.textbbox((0, 0), brake_name, font=f_sub)
    name_w = name_bbox[2] - name_bbox[0]
    draw.text((470 - name_w, 142), brake_name, font=f_sub, fill=COLOR_MUTED_LIGHT)
    
    return img

def render_cockpit_widget(decel_str="0.0", unit="m/s²", power_w=0,
                          status="ARMADO", peak="0.00G", event_no=0,
                          front_temp=22, rear_temp=22, is_braking=False,
                          intensity_pct=0, is_descending=False, des_score=94):
    img = Image.new('RGBA', (480, 180), BG_WIDGET)
    draw = ImageDraw.Draw(img)
    
    # Status color
    status_color = COLOR_RED if is_braking else COLOR_CYAN
    if status == "TRAIL": status_color = COLOR_AMBER
    
    # 1. Header Row
    f_hdr = ImageFont.truetype(FONT_MEDIUM, 20)
    draw.text((10, 32), f"HUD | {status}", font=f_hdr, fill=status_color)
    
    # Event or DES in center-right
    if is_descending:
        des_text = f"DES {des_score}%"
        des_color = COLOR_GREEN if des_score >= 85 else COLOR_AMBER
        draw.text((310, 32), des_text, font=f_hdr, fill=des_color)
    else:
        draw.text((325, 32), f"#{event_no}", font=f_hdr, fill=COLOR_WHITE)
        
    peak_text = f"PICO: {peak}"
    peak_bbox = draw.textbbox((0, 0), peak_text, font=f_hdr)
    peak_w = peak_bbox[2] - peak_bbox[0]
    draw.text((470 - peak_w, 32), peak_text, font=f_hdr, fill=COLOR_YELLOW)
    
    # 2. Central Deceleration Number
    f_num = ImageFont.truetype(FONT_SEMIBOLD, 56)
    f_unit = ImageFont.truetype(FONT_MEDIUM, 18)
    
    num_color = COLOR_WHITE if is_braking else COLOR_MUTED
    num_bbox = draw.textbbox((0, 0), decel_str, font=f_num)
    num_w = num_bbox[2] - num_bbox[0]
    num_h = num_bbox[3] - num_bbox[1]
    
    unit_bbox = draw.textbbox((0, 0), unit, font=f_unit)
    unit_w = unit_bbox[2] - unit_bbox[0]
    
    total_w = num_w + 6 + unit_w
    start_x = (480 - total_w) // 2
    
    draw.text((start_x, 62), decel_str, font=f_num, fill=num_color)
    draw.text((start_x + num_w + 6, 62 + num_h - 18), unit, font=f_unit, fill=COLOR_CYAN if is_braking else COLOR_CYAN)
    
    # 3. 5-Segment Progressive Intensity Spectrum Bar
    bar_y = 124
    seg_w = (460 - 4 * 4) // 5
    spectrum_colors = [
        (255, 255, 255, 255),
        (255, 241, 118, 255),
        COLOR_YELLOW,
        COLOR_AMBER,
        COLOR_RED
    ]
    filled_segs = max(0, min(5, int((intensity_pct / 100.0) * 5))) if is_braking else 0
    
    for i in range(5):
        sx = 10 + i * (seg_w + 4)
        c = spectrum_colors[i] if i < filled_segs else COLOR_INACTIVE_BAR
        draw.rectangle([sx, bar_y, sx + seg_w, bar_y + 8], fill=c)
        
    # 4. Bottom Telemetry Row: Power & Dual Temp
    f_sub = ImageFont.truetype(FONT_BOLD, 17)
    power_str = f"POT: {power_w:.0f}W"
    draw.text((10, 146), power_str, font=f_sub, fill=COLOR_CYAN if is_braking and power_w > 0 else COLOR_MUTED_LIGHT)
    
    # Dual temp on right
    front_c = COLOR_RED if front_temp > 250 else (COLOR_AMBER if front_temp > 180 else (COLOR_CYAN if front_temp > 90 else COLOR_GREEN))
    rear_c = COLOR_RED if rear_temp > 250 else (COLOR_AMBER if rear_temp > 180 else (COLOR_CYAN if rear_temp > 90 else COLOR_GREEN))
    
    temp_draw_x = 365
    draw.text((temp_draw_x, 146), "D:", font=f_sub, fill=COLOR_MUTED_LIGHT)
    draw.text((temp_draw_x + 18, 146), f"{front_temp:.0f}°", font=f_sub, fill=front_c)
    
    draw.text((temp_draw_x + 60, 146), "T:", font=f_sub, fill=COLOR_MUTED_LIGHT)
    draw.text((temp_draw_x + 78, 146), f"{rear_temp:.0f}°", font=f_sub, fill=rear_c)
    
    return img

print('Component functions defined successfully!')
