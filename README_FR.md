# Karoo BBrakers 🚴‍♂️💨🛑
### Télémétrie de Freinage Niveau WorldTour, Transfert de Charge Dynamique, Cercle de Frottement de Kamm & Moniteur Thermique pour Hammerhead Karoo 2 et Karoo 3

<p align="center">
  <b>Documentation par Langue / Language Documentation:</b><br>
  <a href="README.md"><b>English 🇬🇧</b></a> | 
  <a href="README_ES.md"><b>Español 🇪🇸</b></a> | 
  <a href="README_DE.md"><b>Deutsch 🇩🇪</b></a> | 
  <a href="README_FR.md"><b>Français 🇫🇷</b></a> | 
  <a href="README_IT.md"><b>Italiano 🇮🇹</b></a> | 
  <a href="README_PT.md"><b>Português 🇵🇹</b></a>
</p>

---

## 📖 Vue d'ensemble
**Karoo BBrakers** est la toute première extension au monde dédiée à la télémétrie complète de freinage, à la dynamique inertielle et à la sécurité active spécialement conçue pour l'écosystème **Hammerhead Karoo** (Karoo 2 et le nouveau Karoo 3).

L'analyse de la performance cycliste moderne s'est historiquement concentrée sur les capteurs de puissance, la fréquence cardiaque et la cadence. Cependant, sur route en compétition, dans les descentes de gravel ou en VTT, **les courses se gagnent et les chutes s'évitent sur les freins** : retarder le point de freinage, maîtriser le *trail braking* jusqu'au point de corde et gérer l'échauffement thermique des disques et des jantes. Karoo BBrakers métamorphose votre compteur cycliste en un cockpit de télémétrie digne de la Formule 1 ou du MotoGP.

---

## 🔬 Science, Physique & Algorithmes WorldTour

### 1. Décélération Inertielle Continue (Échantillonnage Matériel à 50 Hz)
- Exploite les capteurs physiques IMU intégrés au Karoo (`Sensor.TYPE_LINEAR_ACCELERATION` & `Sensor.TYPE_ACCELEROMETER`) à **50 Hz (mise à jour toutes les 20 ms)**.
- **Compensation de la Gravité et de la Pente en Temps Réel** : L'algorithme isole l'inclinaison statique de la pente routière grâce à des filtres complémentaires. La décélération est mesurée en unités physiques directes ($\text{m/s}^2$ ou Force $G$) avec une réactivité instantanée.
- **Découplage de la Résistance Aérodynamique et de Roulement** : La traînée naturelle de l'air ($F_{aero} = \frac{1}{2}\rho C_d A v^2$) et le frottement des pneumatiques ($C_{rr}$) sont soustraits dynamiquement afin d'éviter qu'une décélération en roue libre sur le plat ne soit comptabilisée à tort comme un freinage.

### 2. Transfert Dynamique de Charge & Répartition Avant / Arrière
- Lors d'une décélération, la force d'inertie transfère le poids du système vers la zone de contact du pneu avant :
  $$\Delta W = \frac{m \cdot a \cdot h_{CG}}{L_{empattement}}$$
- En descente raide ou lors d'un gros freinage, jusqu'à **85 % de la charge normale** se retrouve sur la roue avant. Karoo BBrakers calcule en temps réel la répartition de freinage Avant vs Arrière, permettant au coureur d'optimiser le dosage des leviers et d'éviter les dérapages de l'arrière.

### 3. Cercle de Frottement de Kamm & Prévention des Glissades (*Washout*)
- L'adhérence d'un pneumatique sur le bitume est un vecteur limité par une enveloppe finie :
  $$F_{total} = \sqrt{F_{freinage}^2 + F_{virage}^2} \le \mu \cdot N$$
- Freiner brusquement alors que le vélo est incliné en courbe ($\theta_{inclinaison}$) augmente drastiquement le risque de décrochage soudain de la roue avant.
- Karoo BBrakers surveille en permanence la **Marge d'Adhérence %** en temps réel, alertant le cycliste avant le dépassement de la limite d'adhérence.

### 4. Moniteur Thermodynamique Double des Rotors & Alerte Carbone
- **Refroidissement Différentiel des Étriers** : L'étrier avant bénéficie d'un flux d'air laminaire propre et à haute vitesse ($h_{avant} \propto v^{0.8}$), tandis que l'étrier arrière est masqué dans le sillage turbulent des bases ($h_{arriere} \approx 0.65 \cdot h_{avant}$).
- **Seuils Critiques de Sécurité par Matériau** :
  - **Freins à Disque** : Plage sûre jusqu'à 350 °C.
  - **Freins sur Jante (Aluminium)** : Sûr jusqu'à 150 °C.
  - **Freins sur Jante (Piste Carbone)** : Avertissement préventif à 145 °C et alerte critique à **180 °C** pour prévenir la délamination de la résine et l'explosion de la chambre à air/pneu.

### 5. Détection de Frottement Résiduel (Watts Perdus / Wasted Watts)
- Détecte en continu les pistons grippés, les disques voilés ou le frottement continu des plaquettes pendant que vous pédalez.
- Calcule instantanément la **perte de puissance en Watts** ($P = F_{frottement} \cdot v$) pour diagnostiquer toute perte mécanique parasite.

### 6. Mode Peloton (Modulation Subtile en Groupe)
- Dans un peloton ou un groupe compact, les cyclistes effleurent légèrement le frein arrière pour ajuster leur distance sans à-coup.
- L'extension isole ces micro-effleurements (0,2–1,2 $\text{m/s}^2$) avec un badge violet distinctif sans déclencher de fausse alerte de freinage violent.

---

## 📊 Catalogue Complet des Champs de Données (10 Types Karoo)

Karoo BBrakers enregistre 10 types de données natifs dans l'OS Hammerhead (`extension_info.xml`) :

| ID du Type | Nom dans Karoo OS | Format | Description |
|---|---|---|---|
| `current_decel` | **Force de Freinage** | Numérique | Force de décélération instantanée en $\text{m/s}^2$, Force $G$ ou $\text{ft/s}^2$. |
| `braking_power` | **Puissance de Freinage** | Numérique | Puissance cinétique dissipée par les freins en Watts ($W$). |
| `max_decel` | **Pic de Freinage G** | Numérique | Décélération maximale enregistrée durant la sortie. |
| `brake_events` | **Événements de Freinage** | Numérique | Nombre total d'actions de freinage et de touchers détectés. |
| `brake_energy` | **Énergie Absorbée** | Numérique | Énergie cinétique totale absorbée en kilojoules ($\text{kJ}$). |
| `pad_wear` | **Usure des Plaquettes** | Numérique | Indice d'usure cumulé selon l'énergie de frottement et la garniture. |
| `brake_temperature`| **Température des Freins** | Numérique | Température estimée en temps réel du disque/jante en °C ou °F. |
| `graphical_brake` | **Jauge de Freinage** | Graphique | Indicateur dynamique à 5 segments (Blanc $\to$ Jaune $\to$ Orange $\to$ Rouge). |
| `graphical_thermal`| **Moniteur Thermique & Fade** | Graphique | Températures différentielles Av/Ar, zones thermiques et limites. |
| `graphical_cockpit`| **Cockpit HUD de Freinage** | Graphique | HUD tout-en-un avec jauge, puissance, chaleur et marge d'adhérence. |

---

## 🎨 Code Couleur Visuel en Course
Conçu pour une lisibilité instantanée à haute vitesse (40 à 70 km/h) :
- ⚪ **Blanc Pur** ($< 1,0\text{ m/s}^2$) : Contact initial des plaquettes / effleurement très doux.
- 🟡 **Jaune Clair** ($1,0 - 2,0\text{ m/s}^2$) : Régulation fine de l'allure.
- 🟡 **Jaune Vif** ($2,0 - 3,2\text{ m/s}^2$) : Freinage standard d'approche de virage.
- 🟠 **Orange Vif** ($3,2 - 4,5\text{ m/s}^2$) : Décélération ferme en descente raide.
- 🔴 **Rouge Éclatant** ($> 4,5\text{ m/s}^2$) : Freinage d'urgence / arrêt brutal.
- 🟣 **Violet** : Mode peloton actif (ajustement subtil de la trajectoire).
- 🟢 **Vert** : Température dans la plage optimale et sûre ($< 80^\circ\text{C}$).

---

## 📱 Champs FIT Developer pour l'Analyse Post-Course
Pendant l'enregistrement de vos sorties, Karoo BBrakers intègre des champs développeur FIT à 1 Hz :
- `BrakingDeceleration` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` (indicateur $0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- Résumé de session : Total d'événements, Pic de $G$, Énergie absorbée en kJ et Score d'Usure.

Parfaitement compatible avec **Strava**, **Intervals.icu**, **GoldenCheetah** et **WKO5**.

---

## ⚙️ Installation & Mises à Jour Directes (OTA)

### Option 1 : Via l'URL du Manifeste sur le Karoo (Recommandé)
1. Copiez l'URL du manifeste :
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. Sur votre Hammerhead Karoo, accédez à **Paramètres $\to$ Extensions $\to$ Ajouter une extension**.
3. Collez l'URL. Karoo OS installera l'extension automatiquement et vous préviendra dès qu'une mise à jour sera disponible.

### Option 2 : Installation manuelle via ADB
```bash
adb install -r karoobbrakers.apk
```

---

## 👨‍💻 Auteur & Crédits
- **Développeur** : hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Matériel supporté** : Hammerhead Karoo 2 et Karoo 3 (Android 8.1 - 12+)
- **SDK** : Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Licence** : Licence MIT
