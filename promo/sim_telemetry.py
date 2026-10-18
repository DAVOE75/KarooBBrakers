import os
import sys
from PIL import Image, ImageDraw, ImageFont

FONT_BOLD = r'c:\KarooBrake\app\src\main\res\font\barlow_condensed_bold.ttf'
FONT_SEMIBOLD = r'c:\KarooBrake\app\src\main\res\font\barlow_condensed_semibold.ttf'
FONT_MEDIUM = r'c:\KarooBrake\app\src\main\res\font\barlow_condensed_medium.ttf'
FONT_REGULAR = r'c:\KarooBrake\app\src\main\res\font\barlow_condensed_regular.ttf'

BG_CARBON = (9, 12, 18, 255)
BG_WIDGET = (8, 11, 16, 255)
BG_DIVIDER = (72, 72, 72, 255)
COLOR_WHITE = (255, 255, 255, 255)
COLOR_CYAN = (0, 229, 255, 255)
COLOR_GREEN = (0, 230, 118, 255)
COLOR_AMBER = (255, 145, 0, 255)
COLOR_YELLOW = (255, 214, 0, 255)
COLOR_RED = (255, 23, 68, 255)
COLOR_MUTED = (120, 144, 156, 255)
COLOR_MUTED_LIGHT = (144, 164, 174, 255)
COLOR_INACTIVE_BAR = (30, 37, 48, 255)

def render_8cell_telemetry(base_im1_path, cell_data, output_path, keep_orig_header=True):
    base = Image.open(base_im1_path).convert('RGBA')
    orig_base = base.copy()
    draw_base = ImageDraw.Draw(base)
    
    # Grid definition:
    # Row 1: y=56..174 (h=118), Row 2: y=180..355 (h=175), Row 3: y=361..539 (h=178), Row 4: y=546..727 (h=181)
    # Left col: x=0..233 (w=234), Right col: x=237..477 (w=240)
    grid_bounds = [
        ((0, 56, 234, 175), (237, 56, 477, 175)),
        ((0, 180, 234, 356), (237, 180, 477, 356)),
        ((0, 361, 234, 540), (237, 361, 477, 540)),
        ((0, 546, 234, 728), (237, 546, 477, 728)),
    ]
    
    idx = 0
    for r in range(4):
        for c in range(2):
            box = grid_bounds[r][c]
            x0, y0, x1, y1 = box
            w = x1 - x0
            h = y1 - y0
            
            data = cell_data[idx]
            idx += 1
            
            # Fill background
            draw_base.rectangle([x0, y0, x1, y1], fill=BG_CARBON)
            
            # Tile drawing surface
            tile = Image.new('RGBA', (w, h), BG_CARBON)
            draw = ImageDraw.Draw(tile)
            
            label = data.get('label', '')
            val_str = data.get('val', '')
            unit_str = data.get('unit', '')
            val_color = data.get('val_color', COLOR_WHITE)
            border_color = data.get('border_color', COLOR_CYAN)
            
            # 1. Label
            if label:
                f_lbl = ImageFont.truetype(FONT_BOLD, 19 if r > 0 else 17)
                lbl_bbox = draw.textbbox((0, 0), label, font=f_lbl)
                lbl_w = lbl_bbox[2] - lbl_bbox[0]
                lbl_x = (w - lbl_w) // 2
                draw.text((lbl_x, 10 if r > 0 else 6), label, font=f_lbl, fill=COLOR_WHITE)
            
            # 2. Main value + Unit
            font_size = 96 if r > 0 else 76
            f_num = ImageFont.truetype(FONT_SEMIBOLD, font_size)
            f_unit = ImageFont.truetype(FONT_SEMIBOLD, int(font_size * 0.28))
            
            num_bbox = draw.textbbox((0, 0), val_str, font=f_num)
            num_w = num_bbox[2] - num_bbox[0]
            num_h = num_bbox[3] - num_bbox[1]
            
            unit_w = 0
            if unit_str:
                unit_bbox = draw.textbbox((0, 0), unit_str, font=f_unit)
                unit_w = unit_bbox[2] - unit_bbox[0]
                
            total_w = num_w + (4 if unit_str else 0) + unit_w
            num_x = (w - total_w) // 2
            num_y = (h - num_h) // 2 + (16 if r > 0 else 10)
            
            draw.text((num_x, num_y), val_str, font=f_num, fill=val_color)
            if unit_str:
                draw.text((num_x + num_w + 4, num_y + num_h - 22 if r > 0 else num_y + num_h - 16),
                          unit_str, font=f_unit, fill=COLOR_WHITE)
                
            # 3. Perimeter Bounding Border (v1.2.3 Full-cell architecture)
            draw.rounded_rectangle([2, 2, w - 3, h - 3], radius=6, outline=border_color, width=2)
            
            # Paste tile onto base
            base.paste(tile, (x0, y0))
            
    # Restore Header exactly from original
    if keep_orig_header:
        base.paste(orig_base.crop((0, 0, 477, 56)), (0, 0))
            
    # Restore FAB circular buttons from original
    for y in range(630, 734):
        for x in range(0, 105):
            p = orig_base.getpixel((x, y))
            if (p[0] < 180 and p[1] > 160 and p[2] > 170) or (p[:3] == (0, 0, 0) and x < 60 and y > 680):
                base.putpixel((x, y), p)
                
    for y in range(630, 734):
        for x in range(370, 477):
            p = orig_base.getpixel((x, y))
            if (p[0] > 200 and p[1] > 180 and p[2] < 50) or (p[:3] == (0, 0, 0) and x > 420 and y > 680):
                base.putpixel((x, y), p)
                
    base.save(output_path)
    print(f"Saved: {output_path}")

def render_overview_screen(base_im2_path, top_tile_img, thermal_widget_img, cockpit_widget_img, output_path):
    base = Image.open(base_im2_path).convert('RGBA')
    
    # 1. Top status bar & GPS bar remain 100% genuine and authentic from Karoo OS!
    # 2. Paste Top Tile at y=98..275 (h=177)
    base.paste(top_tile_img, (0, 98))
    
    # 3. Thermal section header remains intact at y=285..347, paste Thermal Widget at y=347
    base.paste(thermal_widget_img, (0, 347))
    
    # 4. Cockpit section header remains intact at y=525..587, paste Cockpit Widget at y=587
    base.paste(cockpit_widget_img, (0, 587))
    
    base.save(output_path)
    print(f"Saved: {output_path}")

print("Generators perfectly aligned!")
