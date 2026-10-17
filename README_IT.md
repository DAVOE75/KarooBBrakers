<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### Telemetria di Frenata WorldTour PRO, Radar Trail-Braking, Efficienza in Discesa & Contachilometri Pastiglie per Hammerhead Karoo 2 & Karoo 3

<p align="center">
  <b>Lingua / Language:</b> 
  <a href="README.md"><b>English</b></a> | 
  <a href="README_ES.md"><b>Español</b></a> | 
  <a href="README_DE.md"><b>Deutsch</b></a> | 
  <a href="README_FR.md"><b>Français</b></a> | 
  <a href="README_IT.md"><b>Italiano</b></a> | 
  <a href="README_PT.md"><b>Português</b></a>
</p>

<p align="center">
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases/latest"><img src="https://img.shields.io/github/v/release/DAVOE75/KarooBBrakers?style=flat-square&logo=android&logoColor=white&label=Versione&color=00C853" alt="Ultima Versione" /></a>
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases"><img src="https://flat.badgen.net/github/assets-dl/DAVOE75/KarooBBrakers?label=Download&color=0091EA&icon=github" alt="Download Totali" /></a>
  <img src="https://img.shields.io/badge/Dispositivo-Karoo_2_•_Karoo_3-1B1B1B?style=flat-square&logo=googlemaps&logoColor=00E676" alt="Compatibilità Karoo" />
  <a href="https://github.com/DAVOE75/KarooBBrakers/blob/main/LICENSE"><img src="https://img.shields.io/github/license/DAVOE75/KarooBBrakers?style=flat-square&label=Licenza&color=455A64" alt="Licenza" /></a>
  <a href="https://buymeacoffee.com/hesiox"><img src="https://img.shields.io/badge/Donazioni-Buy_Me_a_Coffee-FF813F?style=flat-square&logo=buy-me-a-coffee&logoColor=white" alt="Buy Me A Coffee" /></a>
</p>

---

## Panoramica
**Karoo BBrakers** è un'estensione completa di telemetria di frenata, dinamica inerziale e sicurezza attiva sviluppata appositamente per la piattaforma **Hammerhead Karoo** (Karoo 2 e Karoo 3).

L'analisi moderna delle prestazioni ciclistiche si è concentrata quasi esclusivamente su potenza, frequenza cardiaca e cadenza. Tuttavia, nelle discese alpine, nelle gare su strada, nel gravel e nella mountain bike, **le corse si vincono e le cadute si evitano staccando al punto giusto**: con la modulazione della frenata a impulsi, il trail-braking fino al punto di corda e la gestione termica di dischi e pastiglie. Karoo BBrakers trasforma il tuo ciclocomputer Hammerhead in un vero cockpit di telemetria WorldTour.

---

## Fisica, Scienza e Algoritmi WorldTour PRO

### 1. Decelerazione Inerziale Continua (Campionamento Hardware a 50 Hz)
- Sfrutta i sensori inerziali IMU fisici del Karoo (`Sensor.TYPE_LINEAR_ACCELERATION` e `Sensor.TYPE_ACCELEROMETER`) attivi a **50 Hz (aggiornamenti ogni 20 ms)**.
- **Disaccoppiamento Pendenza e Gravità in Tempo Reale**: I filtri complementari isolano l'inclinazione della strada. La decelerazione viene misurata direttamente in grandezze fisiche ($\text{m/s}^2$ o Forza $G$) con risposta istantanea.
- **Filtro Aerodinamico e di Rotolamento**: La resistenza dell'aria ($F_{aero} = \frac{1}{2}\rho C_d A v^2$) e l'attrito dei copertoni ($C_{rr}$) vengono sottratti dinamicamente per non conteggiare l'inerzia in pianura come frenata.

### 2. Punteggio di Efficienza in Discesa (DES 0–100%)
- La guida professionale sui passi alpini richiede frenate brevi e decise a impulsi prima della curva, rilasciando completamente le leve sui rettilinei per favorire il raffreddamento per convezione dei dischi.
- Trascinare continuamente i freni impedisce la dissipazione termica e porta alla vetrificazione della resina delle pastiglie.
- L'algoritmo DES valuta la tua tecnica da 0 a 100%, premiando la modulazione corretta e penalizzando trascinamenti superiori a 5 secondi.

### 3. Radar di Trail-Braking ed Entrata in Curva
- Il trail-braking consiste nel prolungare la frenata all'inserimento in curva, rilasciando progressivamente la pressione man mano che aumenta la piega ($\theta_{lean}$) verso il punto di corda.
- Karoo BBrakers traccia l'angolo di rollio e l'inerzia longitudinale ($da/dt$) in tempo reale classificando la guida in 4 classi WorldTour:
  - **PERFECT**: Forte decelerazione iniziale rilasciata fluidamente all'aumentare dell'inclinazione.
  - **GOOD**: Ottima modulazione con controllo sicuro della velocità.
  - **EARLY RELEASE**: Rilascio prematuro in rettilineo prima della curva, perdita di inerzia.
  - **PANIC LEAN**: Pericolosa frenata brusca a bici già piegata all'interno della curva.

### 4. Trasferimento Dinamico di Carico e Ripartizione Anteriore/Posteriore
- In frenata brusca, l'inerzia trasferisce fino all'**85% del carico normale** sulla ruota anteriore:
  $$\Delta W = \frac{m \cdot a \cdot h_{CG}}{L_{passo}}$$
- Karoo BBrakers calcola in tempo reale la ripartizione percentuale tra freno anteriore e posteriore, aiutando a ottimizzare la modulazione ed evitare bloccaggi della ruota posteriore.

### 5. Cerchio di Aderenza di Kamm e Prevenzione Cadute
- L'aderenza del pneumatico è un limite vettoriale finito:
  $$F_{totale} = \sqrt{F_{frenata}^2 + F_{curva}^2} \le \mu \cdot N$$
- Frenare bruscamente a forte angolo di piega incrementa enormemente il rischio di perdita improvvisa dell'anteriore (*washout*).
- Karoo BBrakers monitora in tempo reale il **Margine di Aderenza %**, avvisando prima del limite di scivolamento.

### 6. Contachilometri di Servizio Pastiglie
- Registra l'energia totale cumulativa assorbita dalle pastiglie in kilojoule ($\text{kJ}$).
- Capacità nominali calibrate per mescola:
  - **Organica / Resina**: ~2500 kJ di capacità nominale. Attacco immediato, silenziose, minore resistenza termica.
  - **Semi-Metallica**: ~4500 kJ di capacità nominale. Equilibrio ideale per tutte le stagioni.
  - **Metallica / Sinterizzata**: ~7000 kJ di capacità nominale. Massima resistenza alle alte temperature e massima durata.
- Visualizza la **Vita Residua in %** e consente l'azzeramento del contachilometri con un tocco nell'app dopo la sostituzione.

### 7. Monitor Termico Duale e Allarme Vetrificazione
- **Raffreddamento Differenziale delle Pinze**: La pinza anteriore riceve flusso laminare diretto ($h_{ant} \propto v^{0.8}$), mentre la posteriore è nella scia turbolenta dei foderi ($h_{post} \approx 0.65 \cdot h_{ant}$).
- **Soglie di Sicurezza**:
  - **Freni a Disco**: Sicuri fino a 350°C.
  - **Freni su Cerchio in Alluminio**: Sicuri fino a 150°C.
  - **Freni su Cerchio in Carbonio**: Preallarme a 145°C, allarme critico a **180°C**.
- **Allarme Acustico di Vetrificazione**: Avvisa tempestivamente se una frenata continua supera i 22 secondi a temperature critiche per la resina.

### 8. Rilevamento Attrito Parassita (Watt Sprecati / Wasted Watts)
- Rileva costantemente pistoncini bloccati, dischi storti o pastiglie che sfregano durante la pedalata.
- Calcola in tempo reale i **Watt Sprecati** ($P = F_{rub} \cdot v$) per preservare la tua potenza.

### 9. Throttling delle RemoteViews (8 Hz)
- L'invio dei dati al Karoo OS è ottimizzato a 120 ms (~8 Hz), riducendo drasticamente il carico sul bus Binder e allungando la durata della batteria.

---

## Catalogo Completo dei Campi Dati (14 Tipi Nativi)

Karoo BBrakers registra 14 tipi di dati nativi in Karoo OS (`extension_info.xml`):

| Type ID | Nome Schermo | Formato | Descrizione |
|---|---|---|---|
| `current_decel` | **Forza di Frenata** | Numerico | Decelerazione istantanea in $\text{m/s}^2$, Forza $G$ o $\text{ft/s}^2$. |
| `braking_power` | **Potenza di Frenata** | Numerico | Potenza cinetica dissipata dai freni in Watt ($W$). |
| `max_decel` | **Picco Decelerazione G** | Numerico | Massima decelerazione registrata durante l'uscita. |
| `brake_events` | **Eventi di Frenata** | Numerico | Conteggio totale di frenate e tocchi leggeri. |
| `brake_energy` | **Energia Dissipata** | Numerico | Energia cinetica totale assorbita in kilojoule ($\text{kJ}$). |
| `pad_wear` | **Indice di Usura** | Numerico | Stima dell'usura in base a energia di attrito e mescola. |
| `pad_life` | **Durata Pastiglie** | Numerico | Percentuale di vita utile residua in base ai kJ accumulati. |
| `descent_efficiency`| **Efficienza Discesa (DES)** | Numerico | Punteggio WorldTour (0–100%) che premia gli impulsi decisi. |
| `wasted_watts` | **Watt Sprecati** | Numerico | Potenza parassita dissipata per attrito o pistoncino bloccato in Watt. |
| `grip_margin` | **Margine di Aderenza** | Numerico | Margine di sicurezza nel cerchio di Kamm prima della perdita di aderenza. |
| `brake_temperature`| **Temperatura Freni** | Numerico | Temperatura stimata in tempo reale di disco/pista in °C o °F. |
| `graphical_brake` | **Manometro Freni** | Grafico | Barra a 5 segmenti colorati (da Bianco a Giallo, Arancione e Rosso). |
| `graphical_thermal`| **Monitor Termico & Fade** | Grafico | Doppio monitoraggio di temperatura, zone termiche e limiti. |
| `graphical_cockpit`| **Cockpit HUD Freni** | Grafico | HUD completo con manometro, Watt, calore, piega e badge trail. |

---

## Codice Colore Visivo in Corsa
- **Bianco** ($< 1.0\text{ m/s}^2$): Contatto iniziale / modulazione leggera.
- **Giallo Chiaro** ($1.0 - 2.0\text{ m/s}^2$): Regolazione fine della distanza in gruppo.
- **Giallo Puro** ($2.0 - 3.2\text{ m/s}^2$): Frenata standard di ingresso curva.
- **Arancione** ($3.2 - 4.5\text{ m/s}^2$): Frenata decisa in forte pendenza.
- **Rosso** ($> 4.5\text{ m/s}^2$): Frenata di emergenza / arresto brusco.
- **Viola**: Modalità gruppo attiva (tocchi leggeri al freno posteriore).
- **Verde**: Temperatura ottimale di funzionamento ($< 80^\circ\text{C}$).

---

## Campi Developer FIT & Analisi Post-Corsa
Durante le uscite registrate, Karoo BBrakers inietta campi FIT a 1 Hz:
- `BrakingDeceleration` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` (flag $0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- `DescentEfficiency` ($\%$)
- `GripMargin` ($\%$)
- Dati riassuntivi della sessione: Eventi totali, Picco G, kJ assorbiti e punteggio di usura.

Compatibile con **Strava**, **Intervals.icu**, **GoldenCheetah** e **WKO5**.

---

## Installazione e Aggiornamenti OTA

### Opzione 1: Installazione Diretta tramite Manifest sul Karoo
1. Copia questo URL del Manifest:
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. Sul tuo Hammerhead Karoo, apri **Impostazioni -> Estensioni -> Aggiungi Estensione**.
3. Incolla l'URL. Karoo OS installerà Karoo BBrakers automaticamente e ti informerà dei nuovi aggiornamenti.

### Opzione 2: Installazione manuale via ADB
```bash
adb install -r karoobbrakers.apk
```

---

## Sviluppatore e Crediti
- **Autore**: hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Hardware supportato**: Hammerhead Karoo 2 / Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Licenza**: Licenza MIT
