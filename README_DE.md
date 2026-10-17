<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### WorldTour PRO Brems-Telemetrie, Trail-Braking-Radar, Abfahrts-Effizienz & Digitaler Belag-Kilometerzähler für Hammerhead Karoo 2 & Karoo 3

<p align="center">
  <b>Sprache / Language:</b> 
  <a href="README.md"><b>English</b></a> | 
  <a href="README_ES.md"><b>Español</b></a> | 
  <a href="README_DE.md"><b>Deutsch</b></a> | 
  <a href="README_FR.md"><b>Français</b></a> | 
  <a href="README_IT.md"><b>Italiano</b></a> | 
  <a href="README_PT.md"><b>Português</b></a>
</p>

<p align="center">
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases/latest"><img src="https://img.shields.io/github/v/release/DAVOE75/KarooBBrakers?style=flat-square&logo=android&logoColor=white&label=Version&color=00C853" alt="Neueste Version" /></a>
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases"><img src="https://flat.badgen.net/github/assets-dl/DAVOE75/KarooBBrakers?label=Downloads&color=0091EA&icon=github" alt="Gesamte Downloads" /></a>
  <img src="https://img.shields.io/badge/Ger%C3%A4t-Karoo_2_•_Karoo_3-1B1B1B?style=flat-square&logo=googlemaps&logoColor=00E676" alt="Karoo Kompatibilität" />
  <a href="https://github.com/DAVOE75/KarooBBrakers/blob/main/LICENSE"><img src="https://img.shields.io/github/license/DAVOE75/KarooBBrakers?style=flat-square&label=Lizenz&color=455A64" alt="Lizenz" /></a>
  <a href="https://buymeacoffee.com/hesiox"><img src="https://img.shields.io/badge/Spenden-Buy_Me_a_Coffee-FF813F?style=flat-square&logo=buy-me-a-coffee&logoColor=white" alt="Buy Me A Coffee" /></a>
</p>

---

## Übersicht
**Karoo BBrakers** ist eine hochpräzise Brems-Telemetrie-, Trägheitsdynamik- und Sicherheitserweiterung, die speziell für die **Hammerhead Karoo** Plattform (Karoo 2 und Karoo 3) entwickelt wurde.

Moderne Leistungsanalysen beschränken sich meist auf Wattwerte, Herzfrequenz und Trittfrequenz. Bei anspruchsvollen Passabfahrten, Gravel-Rennen und Mountainbiken **werden Rennen jedoch beim Bremsen gewonnen und Stürze durch perfekte Dosierung verhindert**: durch spätes Trail-Braking am Kurvenscheitelpunkt, gezielte Bremsstöße und aktives Temperaturmanagement. Karoo BBrakers verwandelt Ihren Hammerhead-Radcomputer in ein vollwertiges WorldTour-Telemetrie-Cockpit.

---

## Physik, Wissenschaft und WorldTour-Algorithmen

### 1. Kontinuierliche Trägheitsverzögerung (50 Hz Hardware-Abtastung)
- Nutzt die internen IMU-Sensoren des Karoo (`Sensor.TYPE_LINEAR_ACCELERATION` und `Sensor.TYPE_ACCELEROMETER`) mit **50 Hz (20 ms Aktualisierungsrate)**.
- **Echtzeit-Trennung von Neigung & Hangabtriebskraft**: Durch komplementäre Filter wird die statische Straßensteigung isoliert. Die Verzögerung wird direkt in physikalischen Einheiten ($\text{m/s}^2$ oder $G$-Kraft) ohne Verzögerung gemessen.
- **Aero- & Rollwiderstands-Entkopplung**: Luftwiderstand ($F_{aero} = \frac{1}{2}\rho C_d A v^2$) und Rollwiderstand ($C_{rr}$) werden dynamisch herausgerechnet, sodass freies Rollen im Flachen niemals als Bremsen gewertet wird.

### 2. Abfahrts-Effizienz-Score (DES 0–100%)
- Auf Alpenpässen verlangt professionelle Fahrtechnik kurze, kräftige Bremsstöße vor Kurven und freies Laufenlassen auf Zwischengeraden, um kühlenden Fahrtwind zu nutzen.
- Dauerschleifen verhindert die Wärmeabfuhr und führt zur thermischen Verglasung der Belagharze.
- Der DES-Algorithmus bewertet Ihre Abfahrtstechnik von 0 bis 100%, belohnt saubere Modulation und bestraft Schleifphasen über 5 Sekunden.

### 3. Trail-Braking-Radar & Kurveneingangs-Telemetrie
- Beim Trail-Braking wird die Bremskraft bis tief in den Kurveneingang mitgeführt und mit zunehmender Schräglage ($\theta_{lean}$) bis zum Scheitelpunkt dosiert abgebaut.
- Karoo BBrakers erfasst Rollwinkel und Beschleunigungsruck ($da/dt$) in Echtzeit und stuft die Technik in 4 Klassen ein:
  - **PERFECT**: Hohe Eingangsverzögerung, die sich mit zunehmender Schräglage harmonisch abbaut.
  - **GOOD**: Gut kontrollierte Modulation mit sicherer Geschwindigkeitsanpassung.
  - **EARLY RELEASE**: Zu frühes Lösen der Bremse vor der Kurve, Verlust von Schwung.
  - **PANIC LEAN**: Gefährliches abruptes Bremsen in bereits tiefer Schräglage.

### 4. Dynamische Lastverlagerung & Bremsbalance
- Bei starker Verzögerung wandern bis zu **85% der Radlast** auf das Vorderrad:
  $$\Delta W = \frac{m \cdot a \cdot h_{CG}}{L_{Radstand}}$$
- Karoo BBrakers berechnet die prozentuale Lastverteilung zwischen Vorder- und Hinterrad in Echtzeit, um Überbremsen des Hecks zu verhindern.

### 5. Kammscher Reibungskreis & Sturzprävention
- Die Haftgrenze des Reifens ist physikalisch begrenzt:
  $$F_{gesamt} = \sqrt{F_{bremsen}^2 + F_{kurve}^2} \le \mu \cdot N$$
- Starkes Bremsen in Schräglage führt leicht zum plötzlichen Wegrutschen des Vorderrads (*Washout*).
- Karoo BBrakers berechnet die **Grip-Marge %** in Echtzeit und warnt vor Erreichen der Haftungsgrenze.

### 6. Digitaler Belag-Service-Kilometerzähler
- Zählt die gesamte von Ihren Bremsbelägen aufgenommene Reibungsenergie in Kilojoule ($\text{kJ}$).
- Kalibrierte Nennkapazitäten nach Belagmischung:
  - **Organisch / Resin**: ~2500 kJ Kapazität. Direkter Biss, geräuschlos, geringere Hitzetoleranz.
  - **Semi-Metallisch**: ~4500 kJ Kapazität. Ausgewogene Allwetter-Performance.
  - **Sintermetall / Metallisch**: ~7000 kJ Kapazität. Höchste Temperaturbeständigkeit und maximale Lebensdauer.
- Zeigt die **verbleibende Belag-Lebensdauer in %** an und erlaubt das Zurücksetzen des Zählers mit einem Klick nach Belagwechsel.

### 7. Dualer Thermomonitor & Verglasungswarnung
- **Differenzielle Bremssattel-Kühlung**: Der vordere Bremssattel wird laminar angeströmt ($h_{vorn} \propto v^{0.8}$), während der hintere im Windschatten der Kettenstreben liegt ($h_{hinten} \approx 0.65 \cdot h_{vorn}$).
- **Material-Grenzwerte**:
  - **Scheibenbremsen**: Sicher bis 350°C.
  - **Felgenbremsen (Aluminium)**: Sicher bis 150°C.
  - **Felgenbremsen (Carbonflanke)**: Vorwarnung bei 145°C, kritischer Alarm bei **180°C**.
- **Akustischer Verglasungsalarm**: Warnt sofort, wenn Dauerbremsen über 22 Sekunden bei Temperaturen oberhalb der Verglasungsgrenze der Mischung anhält.

### 8. Schleiferkennung (Verschwendete Watt / Wasted Watts)
- Erkennt festsitzende Kolben, verzogene Scheiben oder schleifende Beläge beim Pedalieren.
- Berechnet die **verlorene Watt-Leistung** ($P = F_{rub} \cdot v$) in Echtzeit.

### 9. Throttling der RemoteViews (8 Hz)
- Die Datenübertragung an Karoo OS ist auf 120 ms (~8 Hz) optimiert, was die Binder-IPC-Last minimiert und die Akkulaufzeit auf langen Bergetappen deutlich verlängert.

---

## Vollständiger Datenfeld-Katalog (14 Karoo-Typen)

Karoo BBrakers registriert 14 native Datentypen im Karoo OS (`extension_info.xml`):

| Type ID | Display-Name | Format | Beschreibung |
|---|---|---|---|
| `current_decel` | **Bremsverzögerung** | Numerisch | Momentane Verzögerung in $\text{m/s}^2$, $G$-Kraft oder $\text{ft/s}^2$. |
| `braking_power` | **Bremsleistung** | Numerisch | Kinetische Verlustleistung an den Bremsen in Watt ($W$). |
| `max_decel` | **Spitzenverzögerung G** | Numerisch | Maximale Bremsverzögerung während der Fahrt. |
| `brake_events` | **Bremsereignisse** | Numerisch | Gesamtzahl aller Brems- und Feinbremsaktionen. |
| `brake_energy` | **Aufgenommene Energie** | Numerisch | Kumulierte Bremsenergie in Kilojoule ($\text{kJ}$). |
| `pad_wear` | **Belagverschleiß** | Numerisch | Geschätzter Verschleißindex basierend auf Reibungsenergie und Mischung. |
| `pad_life` | **Belag-Restlebensdauer**| Numerisch | Geschätzte verbleibende Lebensdauer der Bremsbeläge in Prozent. |
| `descent_efficiency`| **Abfahrts-Effizienz (DES)** | Numerisch | WorldTour-Score (0–100%), der Bremsstöße gegenüber Schleifen belohnt. |
| `wasted_watts` | **Verschwendete Watt** | Numerisch | Verlustleistung durch schleifende Bremsen oder klemmende Kolben in Watt. |
| `grip_margin` | **Kurven-Grip-Marge** | Numerisch | Sicherheitsmarge im Kammschen Reibungskreis vor Gripabriss. |
| `brake_temperature`| **Bremstemperatur** | Numerisch | Geschätzte Echtzeit-Temperatur der Scheibe/Flanke in °C oder °F. |
| `graphical_brake` | **Brems-Manometer** | Grafisch | 5-Segment-Farbskala (Weiß über Gelb und Orange bis Rot). |
| `graphical_thermal`| **Wärme- & Fade-Monitor**| Grafisch | Zweifache Temperaturanzeige, Wärmezonen und Materialgrenzen. |
| `graphical_cockpit`| **Brems-Cockpit-HUD** | Grafisch | All-in-One Cockpit-HUD mit Manometer, Watt, Wärme, Schräglage und Trail-Badge. |

---

## Visueller Farbfächer während der Fahrt
- **Weiß** ($< 1.0\text{ m/s}^2$): Erster Belagkontakt / leichtes Schleifenlassen.
- **Hellgelb** ($1.0 - 2.0\text{ m/s}^2$): Feinmodulation / Distanzkorrektur.
- **Reingelb** ($2.0 - 3.2\text{ m/s}^2$): Standard-Kurvenanbremsung.
- **Orange** ($3.2 - 4.5\text{ m/s}^2$): Kräftige Verzögerung im Gefälle.
- **Rot** ($> 4.5\text{ m/s}^2$): Notbremsung / Warnstufe Rot.
- **Violett**: Peloton-Feinbremsung im Gruppenverband aktiv.
- **Grün**: Bremstemperatur im optimalen Betriebsbereich ($< 80^\circ\text{C}$).

---

## FIT Developer Datenfelder & Post-Ride-Analyse
Während der Aufzeichnung schreibt Karoo BBrakers FIT-Entwicklerdaten mit 1 Hz:
- `BrakingDeceleration` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` (Flag $0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- `DescentEfficiency` ($\%$)
- `GripMargin` ($\%$)
- Zusammenfassungsdaten: Bremsereignisse, Maximal-G, absorbierte kJ und Belagverschleiß.

Kompatibel mit **Strava**, **Intervals.icu**, **GoldenCheetah** und **WKO5**.

---

## Installation & OTA-Updates

### Option 1: Direkte Manifest-Installation auf dem Karoo
1. Kopieren Sie die folgende Manifest-URL:
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. Öffnen Sie auf dem Hammerhead Karoo **Einstellungen -> Erweiterungen -> Erweiterung hinzufügen**.
3. Fügen Sie die URL ein. Das Karoo OS installiert Karoo BBrakers automatisch und informiert über Updates.

### Option 2: Manuelle Installation via ADB
```bash
adb install -r karoobbrakers.apk
```

---

## Entwickler & Credits
- **Autor**: hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Zielhardware**: Hammerhead Karoo 2 / Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Lizenz**: MIT-Lizenz
