<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### Bremstelemetrie auf WorldTour-Niveau, dynamische Lastverteilung, Kammscher Reibungskreis & dualer Wärmemonitor für Hammerhead Karoo 2 und Karoo 3

<p align="center">
  <b>Sprache / Language:</b> 
  <a href="README.md"><b>English</b></a> | 
  <a href="README_ES.md"><b>Español</b></a> | 
  <a href="README_DE.md"><b>Deutsch</b></a> | 
  <a href="README_FR.md"><b>Français</b></a> | 
  <a href="README_IT.md"><b>Italiano</b></a> | 
  <a href="README_PT.md"><b>Português</b></a>
</p>

---

## Übersicht
**Karoo BBrakers** ist eine umfassende Erweiterung für Bremstelemetrie, Trägheitsdynamik und Fahrsicherheit, entwickelt für das **Hammerhead Karoo** Ökosystem (Karoo 2 und Karoo 3).

In der modernen Radsport-Leistungsdiagnostik lag der Fokus fast ausschließlich auf Wattmessern, Herzfrequenz und Trittfrequenz. Bei anspruchsvollen Straßenrennen, Gravel-Abfahrten und im Mountainbike-Bereich gilt jedoch: **Rennen werden auf der Bremse gewonnen und Stürze durch kontrolliertes Verzögern vermieden** – durch das Hinauszögern des Bremspunkts, präzises Trail-Braking im Scheitelpunkt und effektives Wärmemanagement der Scheiben und Carbonflanken. Karoo BBrakers verwandelt Ihren Fahrradcomputer in ein professionelles Telemetrie-Cockpit auf dem Niveau der Formel 1 oder MotoGP.

---

## Wissenschaft, Physik & WorldTour-Algorithmen

### 1. Kontinuierliche Trägheitsverzögerung (50-Hz-Hardware-Abtastung)
- Nutzt die internen IMU-Sensoren des Karoo (`Sensor.TYPE_LINEAR_ACCELERATION` und `Sensor.TYPE_ACCELEROMETER`) mit **50 Hz (Messung alle 20 ms)**.
- **Echtzeit-Kompensation von Schwerkraft und Steigung**: Statische Hangabtriebskräfte werden über kontinuierliche Komplementärfilter entkoppelt. Die Verzögerung wird direkt in physikalischen Einheiten ($\text{m/s}^2$ oder $G$-Kräfte) mit unmittelbarem Ansprechverhalten berechnet.
- **Dynamische Entkopplung von Roll- und Luftwiderstand**: Der natürliche aerodynamische Widerstand ($F_{aero} = \frac{1}{2}\rho C_d A v^2$) und der Reifenrollwiderstand ($C_{rr}$) werden laufend herausgefiltert, sodass freies Rollen auf flacher Strecke niemals fälschlicherweise als Bremsung gewertet wird.

### 2. Dynamische Lastverteilung & Bremskraftbalance (Vorne / Hinten)
- Beim Bremsen verlagert die Trägheit des Gesamtsystems Gewicht dynamisch auf den vorderen Reifenaufstandspunkt:
  $$\Delta W = \frac{m \cdot a \cdot h_{SP}}{L_{Radstand}}$$
- Bei steilen Abfahrten oder harten Anbremsphasen können bis zu **85 % der Normalkraft** auf dem Vorderrad lasten. Karoo BBrakers berechnet in Echtzeit die Verteilung zwischen Vorder- und Hinterradbremse, um die Hebelmodulation zu optimieren und ein Blockieren des Hecks zu verhindern.

### 3. Kammscher Reibungskreis & Sturzprävention (Washout)
- Die Haftung des Reifens auf dem Untergrund ist eine limitierte vektorielle Gesamtkraft:
  $$F_{gesamt} = \sqrt{F_{bremsen}^2 + F_{kurve}^2} \le \mu \cdot N$$
- Abruptes Anbremsen in Schräglage ($\theta_{Schr\ddot{a}glage}$) steigert das Risiko eines plötzlichen Wegrutschens des Vorderrads (*Front-Wheel Washout*) dramatisch.
- Karoo BBrakers überwacht kontinuierlich die **Grip-Reserve %** in Echtzeit und warnt vor dem Erreichen des Reibungslimits.

### 4. Dualer thermodynamischer Rotor-/Felgen-Fade-Monitor & Carbon-Schutz
- **Differenzierte Kühlung der Bremszangen**: Die Vorderradbremse wird durch saubere, laminare Anströmung gekühlt ($h_{vorne} \propto v^{0.8}$), während die Hinterradbremse im turbulenten Windschatten der Kettenstreben liegt ($h_{hinten} \approx 0.65 \cdot h_{vorne}$).
- **Materialspezifische Sicherheitsgrenzen**:
  - **Scheibenbremsen**: Sicher bis 350 °C.
  - **Felgenbremsen (Aluminiumflanke)**: Sicher bis 150 °C.
  - **Felgenbremsen (Carbonflanke)**: Warnhinweis ab 145 °C, kritischer Alarm bei **180 °C**, um ein Delaminieren der Harzmatrix und Reifenschäden zu verhindern.

### 5. Schleiferkennung beim Treten (Verlorene Watt / Wasted Watts)
- Erkennt festsitzende Kolben, verzogene Bremsscheiben oder schleifende Beläge während des Pedalierens.
- Berechnet in Echtzeit die **verlorenen Watt** ($P = F_{Reibung} \cdot v$), um mechanische Leistungsverluste sofort anzuzeigen.

### 6. Peloton-Modus (Feinmodulation im Feld)
- Im dichten Fahrerfeld nutzen Fahrer oft minimalen Bremsdruck am Hinterrad, um Abstände millimetergenau anzupassen, ohne Unruhe zu stiften.
- Die Erweiterung isoliert dieses Mikro-Schleifen (0,2–1,2 $\text{m/s}^2$) und kennzeichnet es mit einem lilafarbenen Status-Badge, ohne unnötige Bremsalarme auszulösen.

---

## Vollständiger Datenfeld-Katalog (10 Karoo-Typen)

Karoo BBrakers registriert 10 native Datentypen im Hammerhead Karoo OS (`extension_info.xml`):

| Typ-ID | Name im Karoo OS | Format | Beschreibung |
|---|---|---|---|
| `current_decel` | **Bremskraft** | Numerisch | Momentane Verzögerungskraft in $\text{m/s}^2$, $G$-Kraft oder $\text{ft/s}^2$. |
| `braking_power` | **Bremsleistung** | Numerisch | Echtzeit-Kinetikverlustleistung der Bremsen in Watt ($W$). |
| `max_decel` | **Spitzen-Brems-G** | Numerisch | Höchste Bremsverzögerung während der gesamten Ausfahrt. |
| `brake_events` | **Bremsvorgänge** | Numerisch | Gesamtzahl der registrierten Bremsungen und Feinmodulationen. |
| `brake_energy` | **Bremsenergie** | Numerisch | Gesamte von den Bremsen absorbierte kinetische Energie in Kilojoule ($\text{kJ}$). |
| `pad_wear` | **Belagverschleiß** | Numerisch | Geschätzter Verschleißindex basierend auf Bremsarbeit und Belagtyp. |
| `brake_temperature`| **Bremstemperatur** | Numerisch | Geschätzte Echtzeit-Temperatur von Scheibe/Bremsflanke in °C oder °F. |
| `graphical_brake` | **Brems-Manometer** | Grafisch | Dynamische 5-Zonen-Skala (Weiß zu Gelb zu Orange zu Rot). |
| `graphical_thermal`| **Wärme- & Fade-Monitor** | Grafisch | Duale V/H-Temperaturanzeige, Wärmezonen und Materialgrenzen. |
| `graphical_cockpit`| **Brems-Cockpit-HUD** | Grafisch | Integriertes Cockpit-HUD mit Manometer, Watt, Hitze und Grip-Reserve. |

---

## Visueller Farbcode & Farbspektrum
Optimiert für sekundenschnelle Erfassbarkeit bei Renngeschwindigkeiten von 40 bis 70 km/h:
- **Reines Weiß** ($< 1,0\text{ m/s}^2$): Erster Belagkontakt / sehr sanfte Modulation.
- **Hellgelb** ($1,0 - 2,0\text{ m/s}^2$): Feine Geschwindigkeitsanpassung.
- **Sattes Gelb** ($2,0 - 3,2\text{ m/s}^2$): Standard-Kurvenanbremsung.
- **Reines Orange** ($3,2 - 4,5\text{ m/s}^2$): Kräftiges Verzögern auf steilen Abfahrten.
- **Signalrot** ($> 4,5\text{ m/s}^2$): Notbremsung / maximale Verzögerung.
- **Violett**: Peloton-Modus aktiv (sanftes Schleifen im Feld).
- **Grün**: Temperatur im optimalen und sicheren Bereich ($< 80^\circ\text{C}$).

---

## FIT-Entwicklerdatenfelder für Post-Ride-Analysen
Während der Fahrtaufzeichnung speichert Karoo BBrakers standardisierte FIT-Entwicklerdatenfelder mit 1 Hz:
- `BrakingDeceleration` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watt}$)
- `IsBraking` ($0/1\text{-Flag}$)
- `BrakeTemperature` ($^\circ\text{C}$)
- Zusammenfassungsfelder pro Einheit: Gesamte Bremsvorgänge, Spitzen-G, absorbierte kJ und Verschleißindex.

Vollständig kompatibel mit **Strava**, **Intervals.icu**, **GoldenCheetah** und **WKO5**.

---

## Installation & Automatische Updates (OTA)

### Option 1: Über die Manifest-URL direkt am Karoo (Empfohlen)
1. Kopieren Sie die Manifest-URL:
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. Öffnen Sie auf Ihrem Hammerhead Karoo: **Einstellungen -> Erweiterungen -> Erweiterung hinzufügen**.
3. Fügen Sie die URL ein. Das Karoo OS installiert Karoo BBrakers automatisch und zeigt künftig bei neuen Versionen direkt eine Update-Benachrichtigung an.

### Option 2: Manuelle Installation via ADB
```bash
adb install -r karoobbrakers.apk
```

---

## Entwickler & Credits
- **Autor**: hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Zielhardware**: Hammerhead Karoo 2 und Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Lizenz**: MIT License
