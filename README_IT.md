<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### Telemetria di Frenata WorldTour PRO, Radar di Curva & Velocità all'Apice, Limite di Aderenza Kamm, Energia Metabolica & Fluidità in Gruppo (PEI) per Hammerhead Karoo 2 & Karoo 3

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
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases/latest"><img src="https://img.shields.io/github/v/release/DAVOE75/KarooBBrakers?style=flat-square&logo=android&logoColor=white&label=Versione&color=00C853" alt="Ultima versione" /></a>
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases"><img src="https://flat.badgen.net/github/assets-dl/DAVOE75/KarooBBrakers?label=Download&color=0091EA&icon=github" alt="Download totali" /></a>
  <img src="https://img.shields.io/badge/Dispositivo-Karoo_2_•_Karoo_3-1B1B1B?style=flat-square&logo=googlemaps&logoColor=00E676" alt="Compatibilità Karoo" />
  <a href="https://github.com/DAVOE75/KarooBBrakers/blob/main/LICENSE"><img src="https://img.shields.io/github/license/DAVOE75/KarooBBrakers?style=flat-square&label=Licenza&color=455A64" alt="Licenza" /></a>
  <a href="https://buymeacoffee.com/hesiox"><img src="https://img.shields.io/badge/Dona-Buy_Me_a_Coffee-FF813F?style=flat-square&logo=buy-me-a-coffee&logoColor=white" alt="Buy Me A Coffee" /></a>
</p>

---

## Panoramica
**Karoo BBrakers** è un'estensione avanzata di telemetria di frenata, dinamica inerziale, sicurezza predittiva e intelligenza di gara sviluppata appositamente per l'ecosistema **Hammerhead Karoo** (Karoo 2 e Karoo 3).

L'analisi moderna delle prestazioni ciclistiche si è concentrata quasi unicamente su misuratori di potenza, frequenza cardiaca e cadenza. Tuttavia, nelle gare su strada, nelle discese alpine, nel gravel e nella mountain bike, **le corse si vincono e le cadute si evitano staccando al punto giusto**: modulando la frenata a impulsi, eseguendo il trail-braking fino all'apice della curva e gestendo la temperatura di dischi e pastiglie. Karoo BBrakers trasforma il tuo ciclocomputer in un cockpit di telemetria WorldTour.

---

## Fisica, Algoritmi & Ingegneria WorldTour

### 1. Blocco da Fermo & Immunità ai Falsi Positivi (< 4 km/h)
- Senza microinterruttori fisici sulle leve né sensori idraulici, un test sul tavolo o la bici ferma al semaforo non devono mai registrare una frenata.
- Quando la velocità GPS o del sensore ruota è inferiore a $1.11\text{ m/s}$ ($4.0\text{ km/h}$), tutti i vettori di decelerazione vengono azzerati rigorosamente a $0.00\text{ m/s}^2$. Il sistema rimane saldamente nello stato **ARMATO** e la misurazione si attiva nell'istante esatto in cui la bici inizia a muoversi.

### 2. Disaccoppiamento della Gravità sulle Salite Ripide
- Nelle salite con forte pendenza, la gravità tira indietro l'accelerometro lungo l'asse longitudinale ($g \cdot \sin(\theta)$).
- Karoo BBrakers calcola l'angolo istantaneo di inclinazione $\theta = \arcsin\left(\frac{-g_y}{9.80665}\right)$ e sottrae la componente gravitazionale della salita:
  $$a_{\text{frenata, netta}} = a_{\text{misurata, y}} - \max(0, g \cdot \sin(\theta))$$
- Pendenze del $15\% - 25\%$ non generano mai false decelerazioni.

### 3. Radar di Curva & Mantenimento della Velocità all'Apice (0–100%)
- Registra la velocità di ingresso ($v_{\text{ingresso}}$), la velocità minima all'apice ($v_{\text{apex}}$) e l'accelerazione in uscita ($\Delta v_{\text{uscita}}$).
- Calcola la **Percentuale di Mantenimento della Velocità all'Apice**:
  $$\text{Ritenzione Apice} = \left(\frac{v_{\text{apex}}}{v_{\text{ingresso}}}\right) \cdot 100\%$$
- I migliori discesisti professionisti mantengono valori superiori al $75\%$ nelle curve veloci, conservando slancio senza staccare eccessivamente.

### 4. Avvisi Predittivi di Frenata Prima delle Curve
- In base al raggio di curvatura della svolta imminente e alla velocità attuale, calcola lo spazio di arresto in sicurezza:
  $$d_{\text{arresto}} = \frac{v_{\text{attuale}}^2 - v_{\text{sicura}}^2}{2 \cdot a_{\text{sicura}}}$$
- Emette un segnale acustico e un avviso `InRideAlert` sullo schermo del Karoo indicando i metri di anticipo necessari per decelerare.

### 5. Cerchio di Attrito di Kamm & Allarme Limite di Aderenza
- L'aderenza totale dello pneumatico è limitata fisicamente da:
  $$F_{\text{totale}} = \sqrt{F_{\text{frenata}}^2 + F_{\text{curva}}^2} \le \mu \cdot N$$
- Su asfalto bagnato (`WET_ROAD`, $\mu \approx 0.45$), frenare bruscamente con la bici piegata causa la perdita improvvisa della ruota anteriore (*washout*).
- Karoo BBrakers monitora costantemente il **Margine di Aderenza %** ed emette un allarme di emergenza acustico e visivo se il margine scende sotto i livelli critici.

### 6. Energia Metabolica Sprecata (kJ & kcal)
- Ogni frenata distrugge energia cinetica meccanica ($\Delta E = \frac{1}{2} m (v_1^2 - v_2^2)$).
- Considerando l'efficienza muscolare umana lorda ($\eta \approx 21.5\%$), rilanciare per recuperare quella velocità richiede di bruciare quasi 5 volte più glicogeno metabolico:
  $$E_{\text{metabolica}} = \frac{\Delta E_{\text{cinetica}}}{0.215}$$
- Registra i kilojoule e le kilocalorie totali sprecati nelle frenate durante l'uscita.

### 7. Indice di Fluidità in Gruppo (PEI 0–100%)
- Frenare bruscamente in testa al gruppo innesca onde d'urto a fisarmonica verso la coda, costringendo i ciclisti dietro a scatti continui a 800W.
- Su una finestra temporale di 60 secondi, il PEI valuta la fluidità da 0 a 100%, insegnando a gestire la distanza tramite resistenza aerodinamica anziché pinzate secche.

### 8. Punteggio di Efficienza in Discesa (DES 0–100%)
- La discesa alpina richiede frenate brevi e decise prima della curva, rilasciando completamente le leve sui rettilinei per favorire il raffreddamento per convezione.
- Trascinare continuamente i freni causa surriscaldamento e vetrificazione delle pastiglie.
- L'algoritmo DES valuta la tua tecnica da 0 a 100%.

### 9. Trasferimento di Carico Dinamico (Anteriore/Posteriore)
- Durante la frenata, l'inerzia trasferisce il carico sullo pneumatico anteriore:
  $$\Delta W = \frac{m \cdot a \cdot h_{\text{CG}}}{L_{\text{passo}}}$$
- Nelle discese ripide, fino all'**85% del carico** grava sulla ruota anteriore. Karoo BBrakers calcola la distribuzione in tempo reale.

### 10. Odometro Digitale di Usura delle Pastiglie
- Registra l'energia totale assorbita dalle pastiglie in kilojoule ($\text{kJ}$):
  - **Organiche / Resina**: Capacità nominale di ~2500 kJ.
  - **Semi-Metalliche**: Capacità nominale di ~4500 kJ.
  - **Sinterizzate / Metalliche**: Capacità nominale di ~7000 kJ.
- Mostra la **Vita Residua delle Pastiglie %** con azzeramento istantaneo con un tocco nell'app.

### 11. Termodinamica Doppia & Allarme Vetrificazione
- **Raffreddamento Differenziale**: La pinza anteriore riceve aria pulita e laminare, mentre la posteriore lavora nell'aria turbolenta dei foderi.
- **Limiti di Sicurezza**:
  - **Freni a disco**: Sicuro fino a 350°C.
  - **Freni a pattino (Cerchio in alluminio)**: Sicuro fino a 150°C.
  - **Freni a pattino (Pista in carbonio)**: Preallarme a 145°C, allarme critico di delaminazione a **180°C**.

### 12. Rilevamento Sfregamento Parassita (Watt Sprecati / Wasted Watts)
- Rileva costantemente pistoncini bloccati, dischi storti o pastiglie che sfregano mentre pedali.
- Calcola in tempo reale i **Watt Sprecati** ($P = F_{\text{sfregamento}} \cdot v$).

---

## Catalogo Completo dei Campi Dati (17 Tipi Nativi)

Karoo BBrakers registra 17 tipi di dati nativi in Karoo OS (`extension_info.xml`):

| Type ID | Nome sul Display (Italiano) | Formato | Descrizione |
|---|---|---|---|
| `current_decel` | **Decelerazione di Frenata** | Numerico | Decelerazione istantanea in $\text{m/s}^2$, Forza $G$ o $\text{ft/s}^2$. |
| `braking_power` | **Potenza di Frenata** | Numerico | Potenza cinetica dissipata dai freni in Watt ($W$). |
| `max_decel` | **Picco di Decelerazione G**| Numerico | Decelerazione massima registrata durante l'uscita. |
| `brake_events` | **Eventi di Frenata** | Numerico | Conteggio totale delle pinzate e regolazioni di velocità. |
| `brake_energy` | **Energia Dissipata** | Numerico | Energia cinetica totale assorbita dai freni in kilojoule ($\text{kJ}$). |
| `pad_wear` | **Indice di Usura Pastiglie**| Numerico | Punteggio stimato di usura in base a energia e mescola. |
| `pad_life` | **Vita Residua Pastiglie** | Numerico | Percentuale di vita residua stimata delle pastiglie. |
| `descent_efficiency` | **Efficienza in Discesa (DES)**| Numerico | Punteggio WorldTour (0–100%) che premia frenate a impulsi. |
| `wasted_watts` | **Watt Sprecati (Attrito)** | Numerico | Potenza parassita persa per sfregamento dei freni in Watt. |
| `grip_margin` | **Margine di Aderenza Curva** | Numerico | Margine di sicurezza del cerchio di Kamm prima della perdita di aderenza. |
| `brake_temperature`| **Temperatura Freni** | Numerico | Temperatura stimata in tempo reale di disco/pista in °C o °F. |
| `corner_radar` | **Radar di Curva & Apice** | Numerico | Velocità di ingresso, mantenimento all'apice e rilancio in uscita. |
| `metabolic_burn` | **Energia Metabolica Sprecata**| Numerico | Kilojoule muscolari persi secondo efficienza corporea (21.5%). |
| `peloton_smoothness`| **Fluidità in Gruppo (PEI)** | Numerico | Efficienza in scia (0-100%) e resistenza a onde a fisarmonica. |
| `graphical_brake` | **Manometro di Frenata** | Grafico | Indicatore dinamico a 5 segmenti (Bianco, Giallo, Arancione e Rosso). |
| `graphical_thermal`| **Monitor Termico & Fade** | Grafico | Temperature anteriori/posteriori, fasce termiche e limiti di sicurezza. |
| `graphical_cockpit`| **Cockpit HUD di Frenata** | Grafico | HUD completo con manometro, watt, calore, piega e badge trail. |

---

## Campi Developer FIT & Analisi (WKO5 & GoldenCheetah)

Durante le uscite registrate, Karoo BBrakers inietta 12 campi FIT Developer a 1 Hz:
- `BrakeDecel` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` (flag $0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- `DescentEfficiency` ($\%$)
- `GripMargin` ($\%$)
- `MetabolicBurn` ($\text{kJ}$)
- `PelotonSmoothness` ($\%$)
- `ApexRetention` ($\%$)
- Riepilogo di sessione: `TotalBrakeEvents`, `MaxBrakeDecel`, `TotalBrakeEnergy` e `PadWearScore`.

Per formule di grafici per WKO5, script Python per GoldenCheetah e modelli per allenatori WorldTour, consulta la [Guida all'Integrazione WKO5](wko5/README.md).

---

## Codice Colori Visivo Immediato
- **Bianco** ($< 1.0\text{ m/s}^2$): Contatto iniziale / tocco leggero.
- **Giallo Chiaro** ($1.0 - 2.0\text{ m/s}^2$): Regolazione della distanza in gruppo.
- **Giallo Puro** ($2.0 - 3.2\text{ m/s}^2$): Frenata standard prima della curva.
- **Arancione** ($3.2 - 4.5\text{ m/s}^2$): Frenata decisa su discese ripide.
- **Rosso** ($> 4.5\text{ m/s}^2$): Staccata di emergenza / frenata violenta.
- **Viola**: Modalità gruppo attiva (tocchi leggeri al freno posteriore).
- **Verde**: Temperatura ottimale di funzionamento ($< 80^\circ\text{C}$).

---

## Installazione & Aggiornamenti OTA

### Opzione 1: Installazione Diretta tramite Manifest su Karoo
1. Copia questo URL del Manifest:
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. Sul tuo Karoo, vai su **Impostazioni -> Estensioni -> Aggiungi estensione**.
3. Incolla l'URL. Karoo OS installerà Karoo BBrakers e notificherà gli aggiornamenti futuri.

### Opzione 2: Installazione manuale via ADB
```bash
adb install -r karoobbrakers.apk
```

---

## Sviluppatore & Crediti
- **Autore**: hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Dispositivi supportati**: Hammerhead Karoo 2 / Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Licenza**: Licenza MIT
