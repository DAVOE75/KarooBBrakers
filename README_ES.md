<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### Telemetría de Frenada WorldTour PRO, Radar de Curvas y Velocidad en Ápice, Límite de Agarre Kamm, Energía Metabólica y Fluidez en Pelotón (PEI) para Hammerhead Karoo 2 y Karoo 3

<p align="center">
  <b>Idioma / Language:</b> 
  <a href="README.md"><b>English</b></a> | 
  <a href="README_ES.md"><b>Español</b></a> | 
  <a href="README_DE.md"><b>Deutsch</b></a> | 
  <a href="README_FR.md"><b>Français</b></a> | 
  <a href="README_IT.md"><b>Italiano</b></a> | 
  <a href="README_PT.md"><b>Português</b></a>
</p>

<p align="center">
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases/latest"><img src="https://img.shields.io/github/v/release/DAVOE75/KarooBBrakers?style=flat-square&logo=android&logoColor=white&label=Versi%C3%B3n&color=00C853" alt="Última versión" /></a>
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases"><img src="https://flat.badgen.net/github/assets-dl/DAVOE75/KarooBBrakers?label=Descargas&color=0091EA&icon=github" alt="Descargas totales" /></a>
  <img src="https://img.shields.io/badge/Dispositivo-Karoo_2_•_Karoo_3-1B1B1B?style=flat-square&logo=googlemaps&logoColor=00E676" alt="Compatibilidad Karoo" />
  <a href="https://github.com/DAVOE75/KarooBBrakers/blob/main/LICENSE"><img src="https://img.shields.io/github/license/DAVOE75/KarooBBrakers?style=flat-square&label=Licencia&color=455A64" alt="Licencia" /></a>
  <a href="https://buymeacoffee.com/hesiox"><img src="https://img.shields.io/badge/Donar-Buy_Me_a_Coffee-FF813F?style=flat-square&logo=buy-me-a-coffee&logoColor=white" alt="Buy Me A Coffee" /></a>
</p>

---

## Visión General
**Karoo BBrakers** es una extensión de telemetría integral de frenada, dinámica inercial, seguridad predictiva e inteligencia de carrera desarrollada específicamente para el ecosistema **Hammerhead Karoo** (Karoo 2 y el nuevo Karoo 3).

El rendimiento ciclista moderno se ha centrado históricamente en el potenciómetro, el pulso y la cadencia. Sin embargo, en competición de carretera, descensos de puertos alpinos, gravel y mountain bike, **las carreras se ganan y las caídas se evitan en las frenadas**: en la capacidad de modular por pulsos, en el trail-braking dentro de la curva, en la velocidad de paso por el ápice y en el control térmico de los discos y pastillas. Karoo BBrakers transforma tu ciclocomputador en un cockpit de telemetría WorldTour.

---

## Ciencia, Física y Algoritmos WorldTour PRO

### 1. Bloqueo en Parado e Inmunidad a Falsos Positivos (< 4 km/h)
- Sin microinterruptores físicos en las manetas ni sensores hidráulicos, un test sobre la mesa o la bicicleta detenida en un semáforo nunca deben registrar frenada.
- Cuando la velocidad por GPS o sensor de rueda es inferior a $1.11\text{ m/s}$ ($4.0\text{ km/h}$), todos los vectores de desaceleración se anulan estrictamente a $0.00\text{ m/s}^2$. El sistema permanece sólidamente en estado **ARMADO** y la detección instantánea se activa en cuanto la bicicleta echa a rodar.

### 2. Desacoplamiento de Gravedad en Subidas Pronunciadas
- En rampas de fuerte pendiente, la gravedad tira del acelerómetro interno hacia atrás a lo largo del eje longitudinal ($g \cdot \sin(\theta)$).
- Karoo BBrakers calcula el ángulo instantáneo de inclinación $\theta = \arcsin\left(\frac{-g_y}{9.80665}\right)$ y resta el componente gravitacional de la pendiente:
  $$a_{\text{frenada, neta}} = a_{\text{medida, y}} - \max(0, g \cdot \sin(\theta))$$
- Rampas del $15\% - 25\%$ nunca computarán como falsas frenadas.

### 3. Radar de Curvas y Retención de Velocidad en Ápice (0–100%)
- Registra la velocidad de entrada ($v_{\text{entrada}}$), velocidad mínima en el ápice o vértice ($v_{\text{ápice}}$) y la aceleración a la salida ($\Delta v_{\text{salida}}$).
- Calcula el **Porcentaje de Retención de Velocidad en el Ápice**:
  $$\text{Retención Ápice} = \left(\frac{v_{\text{ápice}}}{v_{\text{entrada}}}\right) \cdot 100\%$$
- Los mejores bajadores profesionales buscan retenciones superiores al $75\%$ en curvas rápidas, conservando inercia sin sobre-frenar.

### 4. Avisos Predictivos de Frenada ante Curvas
- Utilizando la curvatura de la curva que se aproxima y la velocidad actual, calcula la distancia de frenada segura:
  $$d_{\text{parada}} = \frac{v_{\text{actual}}^2 - v_{\text{segura}}^2}{2 \cdot a_{\text{segura}}}$$
- Emite un aviso acústico y un `InRideAlert` en la pantalla del Karoo indicando los metros de antelación necesarios para iniciar la deceleración.

### 5. Círculo de Fricción de Kamm y Alarma de Pérdida de Agarre
- El agarre del neumático con el asfalto es un vector finito limitado por:
  $$F_{\text{total}} = \sqrt{F_{\text{frenada}}^2 + F_{\text{curva}}^2} \le \mu \cdot N$$
- En asfalto mojado (`WET_ROAD`, $\mu \approx 0.45$), combinar una frenada fuerte con la bicicleta tumbada provoca el lavado fulminante de la rueda delantera (*washout*).
- Karoo BBrakers monitoriza en tiempo real el **Margen de Agarre %** y activa una alarma de emergencia acústica y visual si el margen cae a niveles críticos.

### 6. Energía Metabólica Malgastada (kJ y kcal)
- Cada frenada destruye energía cinética mecánica ($\Delta E = \frac{1}{2} m (v_1^2 - v_2^2)$).
- Considerando la eficiencia muscular humana bruta ($\eta \approx 21.5\%$), recuperar esa inercia requiere quemar casi 5 veces más energía metabólica de glucógeno:
  $$E_{\text{metabólica}} = \frac{\Delta E_{\text{cinética}}}{0.215}$$
- Registra los kilojulios y kilocalorías totales desperdiciados en frenadas durante toda la ruta.

### 7. Índice de Fluidez en Pelotón (PEI 0–100%)
- Frenar bruscamente en una grupeta genera ondas de choque en acordeón hacia la parte trasera, forzando a los ciclistas retrasados a cerrar huecos con sprints de 800W.
- En una ventana móvil de 60 segundos, el PEI puntúa la fluidez de 0 a 100%, enseñando a mantener la distancia mediante resistencia aerodinámica en lugar de tirones de freno.

### 8. Puntuación de Eficiencia en Bajada (DES 0–100%)
- En los grandes puertos de montaña, la técnica profesional exige frenadas cortas y firmes por pulsos antes de entrar a la curva, soltando manetas en recta para permitir la refrigeración por convección del disco.
- Arrastrar continuamente el freno genera calor acumulativo y provoca la cristalización o vitrificación térmica de las pastillas.
- El algoritmo DES evalúa tu técnica de 0 a 100%, premiando la modulación limpia y penalizando arrastrar el freno más de 5 segundos seguidos.

### 9. Reparto Dinámico de Carga y Balance Delantero/Trasero
- Durante la frenada, la fuerza inercial transfiere peso hacia la rueda delantera:
  $$\Delta W = \frac{m \cdot a \cdot h_{\text{CG}}}{L_{\text{batalla}}}$$
- En bajadas pronunciadas o frenadas fuertes, hasta el **85% de la carga normal** se concentra en el neumático delantero. Karoo BBrakers calcula en tiempo real el porcentaje de reparto entre el freno delantero y trasero.

### 10. Odómetro Digital de Servicio de Pastillas
- Registra la energía total acumulada absorbida por tus pastillas de freno en kilojulios ($\text{kJ}$):
  - **Orgánicas / Resina**: Capacidad nominal de ~2500 kJ.
  - **Semi-Metálicas**: Capacidad nominal de ~4500 kJ.
  - **Sinterizadas / Metálicas**: Capacidad nominal de ~7000 kJ.
- Muestra el **Porcentaje de Vida Restante %** y permite restablecer el odómetro con un solo toque en la app al sustituir las pastillas.

### 11. Monitor Termodinámico Dual y Aviso de Cristalización
- **Enfriamiento Diferencial de Pinzas**: La pinza delantera recibe un flujo laminar limpio de aire ($h_{\text{del}} \propto v^{0.8}$), mientras que la trasera queda resguardada en el aire turbulento de las vainas ($h_{\text{tras}} \approx 0.65 \cdot h_{\text{del}}$).
- **Límites de Seguridad por Material**:
  - **Frenos de Disco**: Seguro hasta 350°C.
  - **Frenos de Zapata (Llanta Aluminio)**: Seguro hasta 150°C.
  - **Frenos de Zapata (Llanta de Carbono)**: Aviso preventivo a 145°C y alarma crítica a **180°C**.

### 12. Detección de Rozamiento Parásito (Vatios Perdidos / Wasted Watts)
- Identifica de forma continua pistones agarrotados, discos doblados o roces de pastillas mientras pedaleas.
- Calcula en tiempo real los **Vatios Perdidos** ($P = F_{\text{rub}} \cdot v$) para corregir fricciones parásitas que merman tu rendimiento.

---

## Catálogo Completo de Campos de Datos (17 Tipos Nativos)

Karoo BBrakers registra 17 tipos de datos nativos en Karoo OS (`extension_info.xml`):

| Type ID | Nombre en Pantalla | Formato | Descripción |
|---|---|---|---|
| `current_decel` | **Fuerza de Frenada** | Numérico | Desaceleración instantánea en $\text{m/s}^2$, Fuerza $G$ o $\text{ft/s}^2$. |
| `braking_power` | **Potencia de Frenada** | Numérico | Potencia cinética disipada por los frenos en vatios ($W$). |
| `max_decel` | **Pico de Frenada G** | Numérico | Máxima desaceleración registrada durante la ruta. |
| `brake_events` | **Eventos de Frenada** | Numérico | Contador total de toques y frenadas detectadas. |
| `brake_energy` | **Energía Disipada** | Numérico | Energía cinética total absorbida por los frenos en kilojulios ($\text{kJ}$). |
| `pad_wear` | **Desgaste Estimado** | Numérico | Índice de desgaste acumulado según energía y compuesto. |
| `pad_life` | **Vida Útil de Pastillas** | Numérico | Porcentaje de vida restante de las pastillas según los kJ acumulados. |
| `descent_efficiency` | **Eficiencia en Bajada (DES)** | Numérico | Puntuación WorldTour (0–100%) que premia pulsos frente a arrastre. |
| `wasted_watts` | **Vatios Perdidos (Arrastre)** | Numérico | Potencia parásita disipada por roce de pastillas o pistón atascado en vatios. |
| `grip_margin` | **Margen de Agarre en Curva** | Numérico | Margen de seguridad del círculo de Kamm antes del deslizamiento del neumático. |
| `brake_temperature`| **Temperatura de Frenos** | Numérico | Temperatura estimada en tiempo real de pista/disco en °C o °F. |
| `corner_radar` | **Radar de Curvas & Ápice** | Numérico | Telemetría de velocidad de entrada, retención en ápice y salida de curva. |
| `metabolic_burn` | **Energía Metabólica Gastada**| Numérico | Kilojulios metabólicos perdidos en frenada según eficiencia muscular (21.5%). |
| `peloton_smoothness`| **Índice Fluidez Pelotón** | Numérico | Eficiencia de rebufo en grupeta (0-100%) y resistencia a ondas en acordeón. |
| `graphical_brake` | **Manómetro de Frenada** | Gráfico | Medidor dinámico en 5 segmentos (Blanco a Amarillo, Naranja y Rojo). |
| `graphical_thermal`| **Monitor Térmico & Fade** | Gráfico | Temperaturas duales delantera/trasera, zonas de calor y límites de seguridad. |
| `graphical_cockpit`| **Cockpit HUD de Frenada** | Gráfico | HUD integral de ruta con manómetro, potencia, calor, inclinación y trail badge. |

---

## Campos Developer en Archivos FIT y Análisis (WKO5 y GoldenCheetah)

Durante las rutas grabadas, Karoo BBrakers inyecta 12 campos Developer FIT estándar a 1 Hz:
- `BrakeDecel` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` (bandera $0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- `DescentEfficiency` ($\%$)
- `GripMargin` ($\%$)
- `MetabolicBurn` ($\text{kJ}$)
- `PelotonSmoothness` ($\%$)
- `ApexRetention` ($\%$)
- Resumen de sesión: `TotalBrakeEvents`, `MaxBrakeDecel`, `TotalBrakeEnergy` e `PadWearScore`.

Para fórmulas de gráficos en WKO5, scripts en Python para GoldenCheetah y métricas para directores deportivos, consulta la [Guía de Integración WKO5](wko5/README.md).

---

## Código de Colores Visual al Instante
Los widgets gráficos emplean colores de contraste optimizados para lectura inmediata a 40–70 km/h:
- **Blanco** ($< 1.0\text{ m/s}^2$): Contacto inicial / modulación suave.
- **Amarillo Claro** ($1.0 - 2.0\text{ m/s}^2$): Ajuste fino de distancia en grupo.
- **Amarillo Puro** ($2.0 - 3.2\text{ m/s}^2$): Frenada estándar de entrada a curva.
- **Naranja** ($3.2 - 4.5\text{ m/s}^2$): Frenada firme en descensos pronunciados.
- **Rojo** ($> 4.5\text{ m/s}^2$): Frenada de emergencia / parada brusca.
- **Violeta**: Modo grupeta activo (toques suaves de rueda trasera).
- **Verde**: Temperatura óptima de funcionamiento ($< 80^\circ\text{C}$).

---

## Instalación y Actualizaciones OTA

### Opción 1: Instalación Directa por Manifest en tu Karoo
1. Copia esta URL de Manifest:
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. En tu Hammerhead Karoo, ve a **Ajustes -> Extensiones -> Añadir Extensión**.
3. Pega la URL. Karoo OS instalará Karoo BBrakers automáticamente y te avisará cuando haya nuevas versiones.

### Opción 2: Instalación manual por ADB
```bash
adb install -r karoobbrakers.apk
```

---

## Desarrollador y Créditos
- **Autor**: hesiOX
- **Dispositivos soportados**: Hammerhead Karoo 2 / Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Licencia**: Licencia MIT
