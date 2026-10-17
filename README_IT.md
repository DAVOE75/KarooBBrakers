<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### Telemetria di Frenata Livello WorldTour, Ripartizione Dinamica del Carico, Cerchio di Attrito di Kamm & Monitor Termico per Hammerhead Karoo 2 e Karoo 3

<p align="center">
  <b>Lingua / Language:</b> 
  <a href="README.md"><b>English</b></a> | 
  <a href="README_ES.md"><b>Español</b></a> | 
  <a href="README_DE.md"><b>Deutsch</b></a> | 
  <a href="README_FR.md"><b>Français</b></a> | 
  <a href="README_IT.md"><b>Italiano</b></a> | 
  <a href="README_PT.md"><b>Português</b></a>
</p>

---

## Panoramica
**Karoo BBrakers** è un'estensione per la telemetria completa di frenata, la dinamica inerziale e la sicurezza attiva sviluppata per l'ecosistema **Hammerhead Karoo** (Karoo 2 e Karoo 3).

L'analisi moderna delle prestazioni ciclistiche si è concentrata quasi esclusivamente su misuratori di potenza, frequenza cardiaca e cadenza. Tuttavia, nelle competizioni su strada, nelle discese gravel e nella mountain bike, **le corse si vincono e le cadute si evitano staccando al limite**: ritardando il punto di frenata, gestendo il trail braking verso l'apice della curva e controllando le temperature di dischi e cerchi. Karoo BBrakers trasforma il tuo ciclocomputer in un cockpit di telemetria professionale paragonabile a quello della Formula 1 o della MotoGP.

---

## Scienza, Fisica & Algoritmi WorldTour

### 1. Decelerazione Inerziale Continua (Campionamento Hardware a 50 Hz)
- Sfrutta i sensori fisici IMU del Karoo (`Sensor.TYPE_LINEAR_ACCELERATION` e `Sensor.TYPE_ACCELEROMETER`) attivi a **50 Hz (un campione ogni 20 ms)**.
- **Compensazione Istantanea di Gravità e Pendenza**: L'algoritmo isola l'inclinazione statica della strada attraverso filtri complementari continui. La decelerazione viene calcolata in unità fisiche dirette ($\text{m/s}^2$ o Forza $G$) con reattività immediata.
- **Disaccoppiamento Aerodinamico e di Rotolamento**: La resistenza naturale dell'aria ($F_{aero} = \frac{1}{2}\rho C_d A v^2$) e l'attrito degli pneumatici ($C_{rr}$) vengono filtrati dinamicamente, impedendo che il semplice scorrimento per inerzia in pianura venga conteggiato come frenata.

### 2. Trasferimento Dinamico del Carico & Bilanciamento Anteriore / Posteriore
- Durante la staccata, la forza d'inerzia trasferisce il peso del sistema sull'impronta a terra della ruota anteriore:
  $$\Delta W = \frac{m \cdot a \cdot h_{baricentro}}{L_{passo}}$$
- Nelle discese ripide o nelle frenate intense, fino all'**85% del carico normale** grava sullo pneumatico anteriore. Karoo BBrakers calcola in tempo reale la ripartizione tra freno anteriore e posteriore, aiutando a ottimizzare la modulazione delle leve ed evitando il bloccaggio del retrotreno.

### 3. Cerchio di Attrito di Kamm & Prevenzione Perdita di Aderenza (*Washout*)
- L'aderenza dello pneumatico con l'asfalto è limitata da un vettore combinato:
  $$F_{totale} = \sqrt{F_{frenata}^2 + F_{curva}^2} \le \mu \cdot N$$
- Frenare bruscamente con la bici inclinata in piega ($\theta_{piega}$) fa impennare il rischio di scivolata improvvisa dell'anteriore.
- Karoo BBrakers monitora costantemente il **Margine di Aderenza %** in tempo reale, avvisando prima di raggiungere il limite di aderenza.

### 4. Monitor Termodinamico Doppio dei Rotori & Allerta Cerchi in Carbonio
- **Raffreddamento Differenziale delle Pinze**: La pinza anteriore è lambita da un flusso d'aria laminare pulito ad alta velocità ($h_{ant} \propto v^{0.8}$), mentre quella posteriore è coperta nella scia turbolenta dei foderi ($h_{post} \approx 0.65 \cdot h_{ant}$).
- **Soglie di Sicurezza per Materiale**:
  - **Freni a Disco**: Sicuro fino a 350 °C.
  - **Freni Tradizionali (Cerchio in Alluminio)**: Sicuro fino a 150 °C.
  - **Freni Tradizionali (Pista in Carbonio)**: Preavviso a 145 °C e allarme critico a **180 °C** per prevenire la delaminazione della resina e lo scoppio della camera/tubeless.

### 5. Rilevamento Sfregamento Parassita (Watt Sprecati / Wasted Watts)
- Rileva pistoni bloccati, dischi storti o pastiglie che sfregano continuamente mentre si pedala.
- Calcola in tempo reale i **Watt sprecati** ($P = F_{sfregamento} \cdot v$) per diagnosticare perdite meccaniche istantanee.

### 6. Modalità Gruppo (Peloton Feathering)
- In un gruppo compatto o in scia, i ciclisti sfiorano leggermente il freno posteriore per regolare la distanza senza creare rallentamenti a catena.
- L'estensione isola questi micro-tocchi (0,2–1,2 $\text{m/s}^2$) mostrando un badge viola distintivo, senza generare falsi allarmi di frenata brusca.

---

## Catalogo Completo dei Campi Dati (10 Tipi Karoo)

Karoo BBrakers registra 10 tipi di dati nativi nel sistema Karoo (`extension_info.xml`):

| ID del Tipo | Nome in Karoo OS | Formato | Descrizione |
|---|---|---|---|
| `current_decel` | **Forza di Frenata** | Numerico | Forza di decelerazione istantanea in $\text{m/s}^2$, Forza $G$ o $\text{ft/s}^2$. |
| `braking_power` | **Potenza di Frenata** | Numerico | Potenza cinetica dissipata dai freni in Watt ($W$). |
| `max_decel` | **Picco di Frenata G** | Numerico | Massima decelerazione di picco registrata durante l'uscita. |
| `brake_events` | **Eventi di Frenata** | Numerico | Numero totale di frenate e tocchi rilevati. |
| `brake_energy` | **Energia Dissipata** | Numerico | Lavoro cinetico totale assorbito in kilojoule ($\text{kJ}$). |
| `pad_wear` | **Usura Pastiglie** | Numerico | Indice di usura cumulativo basato su energia e mescola. |
| `brake_temperature`| **Temperatura Freni** | Numerico | Temperatura stimata in tempo reale di pista/disco in °C o °F. |
| `graphical_brake` | **Manometro Freni** | Grafico | Misuratore dinamico a 5 zone (Bianco verso Giallo verso Arancione verso Rosso). |
| `graphical_thermal`| **Monitor Termico & Fade** | Grafico | Monitor grafico di temperature D/P, zone termiche e limiti. |
| `graphical_cockpit`| **Cockpit HUD Freni** | Grafico | HUD completo con manometro, potenza, calore e margine di aderenza. |

---

## Codice Cromatico di Sicurezza in Corsa
Progettato per una comprensione visiva istantanea tra 40 e 70 km/h:
- **Bianco Puro** ($< 1,0\text{ m/s}^2$): Primo contatto pastiglia / tocco leggero.
- **Giallo Chiaro** ($1,0 - 2,0\text{ m/s}^2$): Modulazione fine della velocità.
- **Giallo Intenso** ($2,0 - 3,2\text{ m/s}^2$): Frenata standard di ingresso curva.
- **Arancione Intenso** ($3,2 - 4,5\text{ m/s}^2$): Staccata decisa in discese ripide.
- **Rosso Fuoco** ($> 4,5\text{ m/s}^2$): Frenata di emergenza / arresto al limite.
- **Viola**: Modalità gruppo attiva (sfioramento leggero senza strappi).
- **Verde**: Temperatura nella zona ottimale e sicura ($< 80^\circ\text{C}$).

---

## Campi FIT Developer per l'Analisi Post-Uscita
Durante la registrazione dell'attività, Karoo BBrakers inietta campi FIT nativi a 1 Hz:
- `BrakingDeceleration` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` (flag $0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- Campi di riepilogo sessione: Eventi totali, Picco $G$, kJ assorbiti e Indice di Usura.

Perfettamente compatibile con **Strava**, **Intervals.icu**, **GoldenCheetah** e **WKO5**.

---

## Installazione & Aggiornamenti Diretti (OTA)

### Opzione 1: Tramite l'URL del Manifesto sul Karoo (Consigliata)
1. Copia l'URL del manifesto:
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. Sul tuo Hammerhead Karoo, apri **Impostazioni -> Estensioni -> Aggiungi Estensione**.
3. Incolla l'URL. Karoo OS installerà l'applicazione automaticamente e ti proporrà gli aggiornamenti con un solo tocco non appena disponibili.

### Opzione 2: Installazione manuale tramite ADB
```bash
adb install -r karoobbrakers.apk
```

---

## Autore & Riconoscimenti
- **Sviluppatore**: hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Dispositivi supportati**: Hammerhead Karoo 2 e Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Licenza**: Licenza MIT
