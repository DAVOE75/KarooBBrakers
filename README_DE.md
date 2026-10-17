<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### WorldTour PRO Bremsen-Telemetrie, Kurven-Radar & Scheitelpunkt-Geschwindigkeit, Kamm-Haftungslimit, Stoffwechselenergie & Peloton-Flüssigkeit (PEI) für Hammerhead Karoo 2 & Karoo 3

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
  <img src="https://img.shields.io/badge/Gerät-Karoo_2_•_Karoo_3-1B1B1B?style=flat-square&logo=googlemaps&logoColor=00E676" alt="Karoo Kompatibilität" />
  <a href="https://github.com/DAVOE75/KarooBBrakers/blob/main/LICENSE"><img src="https://img.shields.io/github/license/DAVOE75/KarooBBrakers?style=flat-square&label=Lizenz&color=455A64" alt="Lizenz" /></a>
  <a href="https://buymeacoffee.com/hesiox"><img src="https://img.shields.io/badge/Spenden-Buy_Me_a_Coffee-FF813F?style=flat-square&logo=buy-me-a-coffee&logoColor=white" alt="Buy Me A Coffee" /></a>
</p>

---

## Übersicht
**Karoo BBrakers** ist eine hochpräzise Bremsen-Telemetrie-, Trägheitsdynamik- und aktive Sicherheitserweiterung, die speziell für das **Hammerhead Karoo** Ökosystem (Karoo 2 und Karoo 3) entwickelt wurde.

Traditionelle Leistungsanalysen im Radsport beschränken sich meist auf Wattmessung, Herzfrequenz und Trittfrequenz. In anspruchsvollen Straßenrennen, Alpenabfahrten, Gravel- und Mountainbike-Wettkämpfen **werden Rennen jedoch beim Bremsen gewonnen und Stürze durch feine Dosierung vermieden**: durch Trail-Braking am Kurvenscheitelpunkt, Kurvenausgangsgeschwindigkeit und thermisches Rotormanagement. Karoo BBrakers verwandelt deinen Radcomputer in ein WorldTour-Telemetrie-Cockpit.

---

## Physik, Algorithmen & WorldTour-Technik

### 1. Stillstandssperre & Fehlalarm-Immunität (< 4 km/h)
- Ohne physische Mikroschalter an den Bremshebeln darf ein Schreibtischtest oder ein Halt an der Ampel niemals ein Bremsen registrieren.
- Bei GPS- oder Raddrehzahlen unter $1.11\text{ m/s}$ ($4.0\text{ km/h}$) werden alle Verzögerungsvektoren strikt auf $0.00\text{ m/s}^2$ gesetzt. Das System bleibt zuverlässig im Status **SCHARF** und aktiviert die Bremsmessung in der Millisekunde, in der das Rad anrollt.

### 2. Gravitations-Neigungskompensation bei Steigungen
- Bei steilen Anstiegen zieht die Schwerkraft entlang der Längsachse am internen Beschleunigungsmesser nach hinten ($g \cdot \sin(\theta)$).
- Karoo BBrakers berechnet den Neigungswinkel $\theta = \arcsin\left(\frac{-g_y}{9.80665}\right)$ und subtrahiert die Steigungsgravitation:
  $$a_{\text{brems, netto}} = a_{\text{gemessen, y}} - \max(0, g \cdot \sin(\theta))$$
- Steile Rampen von $15\% - 25\%$ lösen dadurch niemals falsche Bremsereignisse aus.

### 3. Kurven-Radar & Scheitelpunkt-Geschwindigkeit (0–100%)
- Erfasst Kurveneingangsgeschwindigkeit ($v_{\text{eingang}}$), minimale Scheitelpunktgeschwindigkeit ($v_{\text{apex}}$) und Kurvenausgangsbeschleunigung ($\Delta v_{\text{ausgang}}$).
- Berechnet den **Scheitelpunkt-Geschwindigkeitserhalt in %**:
  $$\text{Apex-Erhalt} = \left(\frac{v_{\text{apex}}}{v_{\text{eingang}}}\right) \cdot 100\%$$
- WorldTour-Profis erzielen in schnellen Kurven Werte von $> 75\%$ und nutzen den Schwung optimal aus.

### 4. Vorausschauende Kurven-Bremswarnungen
- Berechnet anhand des Kurvenradius und der aktuellen Geschwindigkeit den erforderlichen Bremsweg:
  $$d_{\text{stopp}} = \frac{v_{\text{aktuell}}^2 - v_{\text{sicher}}^2}{2 \cdot a_{\text{sicher}}}$$
- Gibt ein akustisches Signal und einen `InRideAlert` auf dem Karoo-Bildschirm mit rechtzeitigem Bremsabstand vor engen Kurven aus.

### 5. Kamm’scher Reibungskreis & Reifenhaftungslimit-Alarm
- Die Gesamthaftung der Reifen ist physikalisch begrenzt:
  $$F_{\text{gesamt}} = \sqrt{F_{\text{brems}}^2 + F_{\text{kurve}}^2} \le \mu \cdot N$$
- Bei Nässe (`WET_ROAD`, $\mu \approx 0.45$) führt abruptes Bremsen in Schräglage zum plötzlichen Wegrutschen des Vorderrads (*Washout*).
- Karoo BBrakers überwacht kontinuierlich die **Grip-Reserve %** und warnt akustisch und visuell vor dem Haftungsabriss.

### 6. Verschwendete Stoffwechselenergie (kJ & kcal)
- Jeder Bremsvorgang vernichtet kinetische Energie ($\Delta E = \frac{1}{2} m (v_1^2 - v_2^2)$).
- Unter Berücksichtigung des menschlichen Muskelwirkungsgrads ($\eta \approx 21.5\%$) erfordert das Wiederbeschleunigen das Fünffache an metabolischer Glykogenenergie:
  $$E_{\text{metabolisch}} = \frac{\Delta E_{\text{kinetisch}}}{0.215}$$
- Erfasst die gesamten verschwendeten Kilojoule und Kilokalorien während der Fahrt.

### 7. Peloton-Flüssigkeitsindex (PEI 0–100%)
- Plötzliches Anbremsen an der Spitze löst ziehharmonikaartige Schockwellen nach hinten aus, die nachfolgende Fahrer zu 800W-Sprints zwingen.
- Über ein 60-Sekunden-Zeitfenster bewertet der PEI die Windschatten-Effizienz von 0 bis 100% und trainiert energiesparendes Fahren.

### 8. Abfahrtseffizienz-Wert (DES 0–100%)
- Schnelle Alpenabfahrten erfordern kurze, entschlossene Bremsimpulse vor der Kurve und freie Bremsbeläge auf der Geraden zur Kühlung.
- Kontinuierliches Schleifenlassen führt zu Hitzestau und Belagverglasung.
- Der DES-Algorithmus bewertet die Technik von 0 bis 100%.

### 9. Dynamische Radlastverteilung (Vorne/Hinten)
- Beim Bremsen verlagert die Trägheit die Last auf das Vorderrad:
  $$\Delta W = \frac{m \cdot a \cdot h_{\text{CG}}}{L_{\text{radstand}}}$$
- Bei steilen Abfahrten lasten bis zu **85%** auf dem Vorderreifen. Karoo BBrakers berechnet die Bremskraftverteilung in Echtzeit.

### 10. Digitaler Bremsbelag-Service-Kilometerzähler
- Erfasst die gesamte absorbierte Energie der Beläge in Kilojoule ($\text{kJ}$):
  - **Organisch / Harz**: ~2500 kJ Nennkapazität.
  - **Semi-Metallisch**: ~4500 kJ Nennkapazität.
  - **Gesintert / Metallisch**: ~7000 kJ Nennkapazität.
- Zeigt die **Verbleibende Belagslebensdauer %** mit einfacher Rückstellung nach dem Wechsel.

### 11. Duale Thermodynamik & Verglasungswarnung
- **Differentielle Kühlung**: Vordere Bremszangen erhalten laminaren Fahrtwind, hintere liegen im turbulenten Windschatten.
- **Sicherheitsgrenzen**:
  - **Scheibenbremsen**: Sicher bis 350°C.
  - **Felgenbremsen (Aluminium)**: Sicher bis 150°C.
  - **Felgenbremsen (Carbonflanke)**: Vorwarnung bei 145°C, kritischer Alarm bei **180°C**.

### 12. Erkennung von parasitärem Bremswiderstand (Wasted Watts)
- Erkennt festsitzende Kolben, verzogene Scheiben oder schleifende Beläge während des Tretens.
- Berechnet kontinuierlich die **Verlorenen Watt** ($P = F_{\text{schleif}} \cdot v$).

---

## Vollständiger Datenfeld-Katalog (17 native Karoo-Typen)

Karoo BBrakers registriert 17 native Datentypen in Karoo OS (`extension_info.xml`):

| Type ID | Display-Name (Deutsch) | Format | Beschreibung |
|---|---|---|---|
| `current_decel` | **Bremsverzögerung** | Numerisch | Momentane Verzögerung in $\text{m/s}^2$, $G$-Kraft oder $\text{ft/s}^2$. |
| `braking_power` | **Bremsleistung** | Numerisch | Durch Bremsen vernichtete kinetische Leistung in Watt ($W$). |
| `max_decel` | **Max. Verzögerung G** | Numerisch | Maximal gemessene Spitzenverzögerung der Fahrt. |
| `brake_events` | **Bremsereignisse** | Numerisch | Gesamtzahl aller Brems- und Dosierungsvorgänge. |
| `brake_energy` | **Vernichtete Energie** | Numerisch | Absorbierte kinetische Gesamtenergie in Kilojoule ($\text{kJ}$). |
| `pad_wear` | **Belagverschleiß** | Numerisch | Geschätzter Verschleißwert basierend auf Energie und Belagtyp. |
| `pad_life` | **Belagslebensdauer** | Numerisch | Geschätzte verbleibende Lebensdauer der Bremsbeläge in %. |
| `descent_efficiency` | **Abfahrtseffizienz (DES)** | Numerisch | WorldTour-Abfahrtswert (0–100%), der Impulsbremsen belohnt. |
| `wasted_watts` | **Verlorene Watt (Schleifen)** | Numerisch | Parasitärer Leistungsverlust durch schleifende Bremsen in Watt. |
| `grip_margin` | **Kurvenhaftungsreserve** | Numerisch | Sicherheitsreserve des Kamm'schen Kreises vor dem Rutschen in %. |
| `brake_temperature`| **Bremstemperatur** | Numerisch | Berechnete Temperatur der Bremsscheibe/Felge in °C oder °F. |
| `corner_radar` | **Kurven-Radar & Apex** | Numerisch | Eingangsgeschwindigkeit, Apex-Erhalt und Kurvenausgangs-Telemetrie. |
| `metabolic_burn` | **Metabolische Energie** | Numerisch | Verschwendete Muskelenergie beim Bremsen basierend auf 21.5% Wirkungsgrad. |
| `peloton_smoothness`| **Peloton-Flüssigkeit (PEI)**| Numerisch | Windschatten-Effizienz (0-100%) und Ziehharmonika-Widerstand. |
| `graphical_brake` | **Bremsmanometer (Grafik)**| Grafik | 5-Segment Farbskala (Weiß über Gelb bis Orange und Rot). |
| `graphical_thermal`| **Thermischer Monitor** | Grafik | Duale Scheibentemperatur vorne/hinten, Hitzestufen und Grenzwerte. |
| `graphical_cockpit`| **Bremsen Cockpit HUD** | Grafik | Kompaktes HUD mit Manometer, Leistung, Hitze, Schräglage und Abzeichen. |

---

## FIT Developer Datenfelder & Analyse (WKO5 & GoldenCheetah)

Karoo BBrakers speichert während aufgezeichneter Fahrten 12 FIT-Entwicklerfelder mit 1 Hz:
- `BrakeDecel` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` (Flag $0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- `DescentEfficiency` ($\%$)
- `GripMargin` ($\%$)
- `MetabolicBurn` ($\text{kJ}$)
- `PelotonSmoothness` ($\%$)
- `ApexRetention` ($\%$)
- Session-Zusammenfassung: `TotalBrakeEvents`, `MaxBrakeDecel`, `TotalBrakeEnergy` und `PadWearScore`.

Formeln für WKO5-Diagramme, GoldenCheetah-Skripte und Coaching-Richtlinien findest du im [WKO5 Integrationsleitfaden](wko5/README.md).

---

## Farbcodierung im Blickfeld
- **Weiß** ($< 1.0\text{ m/s}^2$): Erster Belagkontakt / leichtes Schleifen.
- **Hellgelb** ($1.0 - 2.0\text{ m/s}^2$): Feine Distanzkorrektur im Peloton.
- **Reines Gelb** ($2.0 - 3.2\text{ m/s}^2$): Normales Anbremsen vor Kurven.
- **Orange** ($3.2 - 4.5\text{ m/s}^2$): Kräftige Verzögerung bei steilen Abfahrten.
- **Rot** ($> 4.5\text{ m/s}^2$): Notbremsung / Gefahrenstopp.
- **Violett**: Peloton-Modus aktiv (sanfte Heckbremsung).
- **Grün**: Optimale Betriebstemperatur ($< 80^\circ\text{C}$).

---

## Installation & OTA-Updates

### Option 1: Direkte Manifest-Installation auf dem Karoo
1. Kopiere diese Manifest-URL:
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. Öffne auf deinem Karoo **Einstellungen -> Erweiterungen -> Erweiterung hinzufügen**.
3. URL einfügen. Karoo OS installiert Karoo BBrakers automatisch und benachrichtigt über Updates.

### Option 2: Sideloading via ADB
```bash
adb install -r karoobbrakers.apk
```

---

## Entwickler & Credits
- **Autor**: hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Unterstützte Hardware**: Hammerhead Karoo 2 / Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Lizenz**: MIT Lizenz
