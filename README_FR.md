<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### Télémétrie de Freinage WorldTour PRO, Radar de Trail-Braking, Efficacité en Descente & Odomètre de Plaquettes pour Hammerhead Karoo 2 & Karoo 3

<p align="center">
  <b>Langue / Language:</b> 
  <a href="README.md"><b>English</b></a> | 
  <a href="README_ES.md"><b>Español</b></a> | 
  <a href="README_DE.md"><b>Deutsch</b></a> | 
  <a href="README_FR.md"><b>Français</b></a> | 
  <a href="README_IT.md"><b>Italiano</b></a> | 
  <a href="README_PT.md"><b>Português</b></a>
</p>

<p align="center">
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases/latest"><img src="https://img.shields.io/github/v/release/DAVOE75/KarooBBrakers?style=flat-square&logo=android&logoColor=white&label=Version&color=00C853" alt="Dernière Version" /></a>
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases"><img src="https://img.shields.io/github/downloads/DAVOE75/KarooBBrakers/total?style=flat-square&logo=github&logoColor=white&label=Téléchargements&color=0091EA&_v=1" alt="Téléchargements totaux" /></a>
  <img src="https://img.shields.io/badge/Appareil-Karoo_2_•_Karoo_3-1B1B1B?style=flat-square&logo=googlemaps&logoColor=00E676" alt="Compatibilité Karoo" />
  <a href="https://github.com/DAVOE75/KarooBBrakers/blob/main/LICENSE"><img src="https://img.shields.io/github/license/DAVOE75/KarooBBrakers?style=flat-square&label=Licence&color=455A64" alt="Licence" /></a>
  <a href="https://buymeacoffee.com/hesiox"><img src="https://img.shields.io/badge/Soutenir-Buy_Me_a_Coffee-FF813F?style=flat-square&logo=buy-me-a-coffee&logoColor=white" alt="Buy Me A Coffee" /></a>
</p>

---

## Présentation
**Karoo BBrakers** est une extension complète de télémétrie de freinage, de dynamique inertielle et de sécurité active conçue spécialement pour l'écosystème **Hammerhead Karoo** (Karoo 2 et Karoo 3).

L'analyse moderne des performances cyclistes s'est presque exclusivement concentrée sur la puissance, la fréquence cardiaque et la cadence. Pourtant, lors des descentes de grands cols, en gravel et en VTT, **les courses se gagnent et les chutes s'évitent au freinage** : par la maîtrise des impulsions franches, le trail-braking jusqu'au point de corde et la gestion thermique des disques et jantes. Karoo BBrakers transforme votre compteur Hammerhead en un cockpit de télémétrie WorldTour.

---

## Physique, Science et Algorithmes WorldTour PRO

### 1. Décélération Inertielle Continue (Échantillonnage Hardware à 50 Hz)
- Exploite les capteurs inertiels IMU du Karoo (`Sensor.TYPE_LINEAR_ACCELERATION` et `Sensor.TYPE_ACCELEROMETER`) cadencés à **50 Hz (mises à jour toutes les 20 ms)**.
- **Découplage Pente et Gravité en Temps Réel** : Les filtres complémentaires éliminent l'influence de l'inclinaison de la route. La décélération est mesurée directement en unités physiques ($\text{m/s}^2$ ou Force $G$) avec une réactivité instantanée.
- **Filtrage de la Traînée Aérodynamique et de Roulement** : La résistance de l'air ($F_{aero} = \frac{1}{2}\rho C_d A v^2$) et le frottement des pneus ($C_{rr}$) sont soustraits pour ne pas comptabiliser le roulage en roue libre comme du freinage.

### 2. Score d'Efficacité en Descente (DES 0–100%)
- La descente en montagne exige des freinages brefs et appuyés avant le virage, puis un relâchement complet en ligne droite pour permettre le refroidissement par convection.
- Traîner les freins en continu empêche l'évacuation calorique et provoque le glaçage (vitrification de la matrice de résine).
- L'algorithme DES note votre pilotage de 0 à 100%, récompensant les impulsions nettes et pénalisant les appuis prolongés de plus de 5 secondes.

### 3. Radar de Trail-Braking et Entrée en Virage
- Le trail-braking prolonge la décélération à l'inscription en courbe en relâchant progressivement la pression à mesure que l'angle de gîte ($\theta_{lean}$) augmente vers le point de corde.
- Karoo BBrakers analyse l'angle de roulis et l'inertie longitudinale ($da/dt$) en temps réel pour classifier le pilotage en 4 catégories WorldTour :
  - **PERFECT** : Forte décélération initiale relâchée avec fluidité au fur et à mesure de l'inclinaison.
  - **GOOD** : Modulation propre et ajustement sûr de la vitesse.
  - **EARLY RELEASE** : Relâchement trop tôt en ligne droite avant la courbe, perte de vitesse.
  - **PANIC LEAN** : Freinage brutal et dangereux alors que le vélo est déjà fortement incliné.

### 4. Transfert de Charge Dynamique et Équilibre Avant/Arrière
- Au freinage, l'inertie transfère jusqu'à **85% de la charge normale** sur le pneu avant :
  $$\Delta W = \frac{m \cdot a \cdot h_{CG}}{L_{empattement}}$$
- Karoo BBrakers calcule en temps réel la répartition de freinage avant/arrière pour optimiser le dosage et éviter le blocage du train arrière.

### 5. Cercle de Friction de Kamm et Prévention des Chutes
- L'adhérence d'un pneu est une limite vectorielle finie :
  $$F_{total} = \sqrt{F_{freinage}^2 + F_{virage}^2} \le \mu \cdot N$$
- Freiner fort en virage augmente considérablement le risque de décrochage de la roue avant (*washout*).
- Karoo BBrakers calcule en temps réel votre **Marge d'Adhérence %** et avertit avant la limite de décrochage.

### 6. Odomètre de Service des Plaquettes
- Comptabilise l'énergie totale cumulée absorbée par vos plaquettes en kilojoules ($\text{kJ}$).
- Capacités nominales étalonnées par garniture :
  - **Organique / Résine** : ~2500 kJ de capacité nominale. Mordant immédiat, silencieuse, limite thermique modérée.
  - **Semi-Métallique** : ~4500 kJ de capacité nominale. Équilibre idéal toutes conditions.
  - **Métallique / Frittée** : ~7000 kJ de capacité nominale. Très haute résistance thermique et longévité maximale.
- Affiche la **Durée de Vie Restante en %** et permet la réinitialisation de l'odomètre d'un simple toucher après remplacement.

### 7. Moniteur Thermique Dual et Alerte de Glaçage
- **Refroidissement Différentiel des Étriers** : L'étrier avant bénéficie d'un flux d'air laminaire direct ($h_{av} \propto v^{0.8}$), tandis que l'arrière est dans le sillage turbulent des bases ($h_{arr} \approx 0.65 \cdot h_{av}$).
- **Seuils Critiques** :
  - **Freins à Disque** : Sûrs jusqu'à 350°C.
  - **Freins sur Jante Aluminium** : Sûrs jusqu'à 150°C.
  - **Freins sur Jante Carbone** : Avertissement à 145°C, alarme critique à **180°C**.
- **Alerte Sonore de Glaçage** : Avertit immédiatement si un freinage continu dépasse 22 secondes à des températures risquant de vitrifier les garnitures.

### 8. Détection de Frottement Parasite (Watts Gaspillés / Wasted Watts)
- Détecte en continu les pistons grippés, disques voilés ou plaquettes qui frottent pendant le pédalage.
- Calcule en direct les **Watts Gaspillés** ($P = F_{rub} \cdot v$) pour préserver votre énergie.

### 9. Throttling des Vues Distantes (8 Hz)
- Le rendu des données vers Karoo OS est cadencé à 120 ms (~8 Hz), éliminant la saturation du bus Binder et prolongeant considérablement l'autonomie de la batterie.

---

## Catalogue Complet des Champs de Données (14 Types Nactifs)

Karoo BBrakers enregistre 14 types de données natifs dans Karoo OS (`extension_info.xml`) :

| Type ID | Nom Affiché | Format | Description |
|---|---|---|---|
| `current_decel` | **Force de Freinage** | Numérique | Décélération instantanée en $\text{m/s}^2$, Force $G$ ou $\text{ft/s}^2$. |
| `braking_power` | **Puissance de Freinage** | Numérique | Puissance cinétique dissipée par les freins en Watts ($W$). |
| `max_decel` | **Pic de Décélération G** | Numérique | Décélération maximale enregistrée pendant la sortie. |
| `brake_events` | **Événements de Freinage** | Numérique | Nombre total d'actions de freinage et d'effleurements. |
| `brake_energy` | **Énergie Dissipée** | Numérique | Énergie cinétique totale absorbée en kilojoules ($\text{kJ}$). |
| `pad_wear` | **Indice d'Usure** | Numérique | Estimation d'usure basée sur l'énergie de friction et la garniture. |
| `pad_life` | **Vie des Plaquettes** | Numérique | Pourcentage de vie restante des plaquettes selon les kJ cumulés. |
| `descent_efficiency`| **Efficacité Descente (DES)**| Numérique | Score WorldTour (0–100%) récompensant les impulsions nettes. |
| `wasted_watts` | **Watts Gaspillés** | Numérique | Puissance parasite dissipée par frottement de plaquette en Watts. |
| `grip_margin` | **Marge d'Adhérence** | Numérique | Marge de sécurité du cercle de Kamm avant perte d'adhérence. |
| `brake_temperature`| **Température des Freins**| Numérique | Température estimée en temps réel du disque/jante en °C ou °F. |
| `graphical_brake` | **Jauge de Freinage** | Graphique | Indicateur couleur 5 segments (Blanc, Jaune, Orange, Rouge). |
| `graphical_thermal`| **Moniteur Thermique & Fade**| Graphique | Double affichage de température, zones de chaleur et limites. |
| `graphical_cockpit`| **Cockpit HUD de Freinage**| Graphique | HUD tout-en-un avec jauge, Watts, chaleur, gîte et badge trail. |

---

## Code Couleur Visuel en Sortie
- **Blanc** ($< 1.0\text{ m/s}^2$) : Contact initial / effleurement léger.
- **Jaune Pâle** ($1.0 - 2.0\text{ m/s}^2$) : Modulation douce / calage de distance.
- **Jaune Pur** ($2.0 - 3.2\text{ m/s}^2$) : Freinage standard avant virage.
- **Orange** ($3.2 - 4.5\text{ m/s}^2$) : Freinage ferme en pente raide.
- **Rouge** ($> 4.5\text{ m/s}^2$) : Freinage d'urgence / arrêt d'urgence.
- **Violet** : Mode peloton actif (effleurement de roue arrière).
- **Vert** : Température dans la zone optimale ($< 80^\circ\text{C}$).

---

## Champs Developer FIT & Analyse Post-Sortie
Pendant vos sorties enregistrées, Karoo BBrakers injecte des champs FIT à 1 Hz :
- `BrakingDeceleration` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` (indicateur $0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- `DescentEfficiency` ($\%$)
- `GripMargin` ($\%$)
- Données de résumé de session : Total événements, Pic de G, kJ absorbés et score d'usure.

Compatible avec **Strava**, **Intervals.icu**, **GoldenCheetah** et **WKO5**.

---

## Installation & Mises à Jour OTA

### Option 1 : Installation Directe par Manifest sur le Karoo
1. Copiez cette URL de Manifest :
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. Sur votre Hammerhead Karoo, allez dans **Paramètres -> Extensions -> Ajouter une Extension**.
3. Collez l'URL. Karoo OS installera Karoo BBrakers automatiquement et vous avertira des futures mises à jour.

### Option 2 : Installation manuelle via ADB
```bash
adb install -r karoobbrakers.apk
```

---

## Développeur & Crédits
- **Auteur** : hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Matériel cible** : Hammerhead Karoo 2 / Karoo 3 (Android 8.1 - 12+)
- **SDK** : Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Licence** : Licence MIT
