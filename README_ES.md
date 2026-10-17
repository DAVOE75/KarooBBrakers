<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### Telemetría de Frenada WorldTour PRO, Radar de Trail-Braking, Eficiencia en Bajada y Odómetro de Pastillas para Hammerhead Karoo 2 y Karoo 3

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
**Karoo BBrakers** es una extensión de telemetría integral de frenada, dinámica inercial y seguridad activa desarrollada específicamente para el ecosistema **Hammerhead Karoo** (Karoo 2 y el nuevo Karoo 3).

El rendimiento ciclista moderno se ha centrado históricamente en el potenciómetro, el pulso y la cadencia. Sin embargo, en competición de carretera, descensos de puertos alpinos, gravel y mountain bike, **las carreras se ganan y las caídas se evitan en las frenadas**: en la capacidad de modular por pulsos, en el trail-braking dentro de la curva y en el control térmico de los discos y pastillas. Karoo BBrakers transforma tu ciclocomputador en un cockpit de telemetría WorldTour.

---

## Ciencia, Física y Algoritmos WorldTour PRO

### 1. Desaceleración Inercial Continua (Muestreo Hardware a 50 Hz)
- Emplea los sensores inerciales físicos del propio Karoo (`Sensor.TYPE_LINEAR_ACCELERATION` y `Sensor.TYPE_ACCELEROMETER`) funcionando a **50 Hz (muestras cada 20 ms)**.
- **Filtrado de Gravedad y Pendiente en Tiempo Real**: El algoritmo aísla la inclinación estática de la carretera mediante filtros complementarios. La desaceleración se mide en unidades físicas directas ($\text{m/s}^2$ o Fuerza $G$) con respuesta instantánea.
- **Desacoplamiento Aerodinámico y de Rodadura**: La resistencia natural del viento ($F_{aero} = \frac{1}{2}\rho C_d A v^2$) y el coeficiente de fricción de los neumáticos ($C_{rr}$) se restan dinámicamente para que el mero hecho de rodar por inercia en llano no compute falsamente como frenada.

### 2. Puntuación de Eficiencia en Bajada (DES 0–100%)
- En los grandes puertos de montaña, la técnica profesional exige frenadas cortas y firmes por pulsos antes de entrar a la curva, soltando manetas en recta para permitir la refrigeración por convección del disco.
- Arrastrar continuamente el freno genera calor acumulativo y provoca la cristalización o vitrificación térmica de las pastillas.
- El algoritmo DES evalúa tu técnica de 0 a 100%, premiando la modulación limpia y penalizando arrastrar el freno más de 5 segundos seguidos.

### 3. Radar de Trail-Braking y Entrada en Curva
- El trail-braking consiste en mantener la frenada al iniciar el giro, aliviando la presión de forma progresiva a medida que la inclinación ($\theta_{lean}$) aumenta hacia el vértice o ápice de la curva.
- Karoo BBrakers monitoriza el ángulo de inclinación y el tirón inercial ($da/dt$) en tiempo real clasificando la técnica en 4 categorías WorldTour:
  - **PERFECT**: Fuerte deceleración a la entrada que se suelta suavemente conforme se inclina hacia el ápice.
  - **GOOD**: Buena modulación progresiva con control seguro de velocidad.
  - **EARLY RELEASE**: Soltada prematura de frenos antes de tiempo en recta, perdiendo inercia.
  - **PANIC LEAN**: Peligrosa frenada brusca con la bicicleta ya muy inclinada dentro de la curva.

### 4. Reparto Dinámico de Carga y Balance Delantero/Trasero
- Durante la frenada, la fuerza inercial transfiere peso hacia la rueda delantera:
  $$\Delta W = \frac{m \cdot a \cdot h_{CG}}{L_{batalla}}$$
- En bajadas pronunciadas o frenadas fuertes, hasta el **85% de la carga normal** se concentra en el neumático delantero. Karoo BBrakers calcula en tiempo real el porcentaje de reparto entre el freno delantero y trasero, permitiendo optimizar el tacto de manetas y evitar derrapes del eje posterior.

### 5. Círculo de Fricción de Kamm y Prevención de Caídas
- El agarre del neumático con el asfalto es un vector finito limitado por:
  $$F_{total} = \sqrt{F_{frenada}^2 + F_{curva}^2} \le \mu \cdot N$$
- Frenar bruscamente mientras la bicicleta está tumbada en curva dispara la probabilidad de un lavado repentino de la rueda delantera (*washout*).
- Karoo BBrakers monitoriza en tiempo real el **Margen de Agarre %**, alertando antes de superar el límite de adherencia.

### 6. Odómetro Digital de Servicio de Pastillas
- Registra la energía total acumulada absorbida por tus pastillas de freno en kilojulios ($\text{kJ}$).
- Calibrado según el compuesto instalado:
  - **Orgánicas / Resina**: Capacidad nominal de ~2500 kJ. Mordida instantánea, silenciosas, menor tolerancia térmica.
  - **Semi-Metálicas**: Capacidad nominal de ~4500 kJ. Equilibrio ideal para todas las estaciones.
  - **Sinterizadas / Metálicas**: Capacidad nominal de ~7000 kJ. Máxima resistencia a altas temperaturas y máxima durabilidad.
- Muestra el **Porcentaje de Vida Restante %** y permite restablecer el odómetro con un solo toque en la app al sustituir las pastillas.

### 7. Monitor Termodinámico Dual y Aviso de Cristalización
- **Enfriamiento Diferencial de Pinzas**: La pinza delantera recibe un flujo laminar limpio de aire ($h_{del} \propto v^{0.8}$), mientras que la trasera queda resguardada en el aire turbulento de las vainas ($h_{tras} \approx 0.65 \cdot h_{del}$).
- **Límites de Seguridad por Material**:
  - **Frenos de Disco**: Seguro hasta 350°C.
  - **Frenos de Zapata (Llanta Aluminio)**: Seguro hasta 150°C.
  - **Frenos de Zapata (Llanta de Carbono)**: Aviso preventivo a 145°C y alarma crítica a **180°C**.
- **Alarma Acústica de Cristalización**: Avisa inmediatamente si la frenada continua supera los 22 segundos con temperaturas que amenacen la vitrificación de la resina.

### 8. Detección de Rozamiento Parásito (Vatios Perdidos / Wasted Watts)
- Identifica de forma continua pistones agarrotados, discos doblados o roces de pastillas mientras pedaleas.
- Calcula en tiempo real los **Vatios Perdidos** ($P = F_{rub} \cdot v$) para corregir fricciones parásitas que merman tu rendimiento.

### 9. Throttling de IPC en RemoteViews (8 Hz)
- La emisión de vistas remotas a Karoo OS está optimizada a 120 ms (~8 Hz), eliminando sobrecarga en el bus Binder de Android y extendiendo la autonomía de la batería en rutas de montaña de larga duración.

---

## Catálogo Completo de Campos de Datos (14 Tipos Nativos)

Karoo BBrakers registra 14 tipos de datos nativos en Karoo OS (`extension_info.xml`):

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
| `graphical_brake` | **Manómetro de Frenada** | Gráfico | Medidor dinámico en 5 segmentos (Blanco a Amarillo, Naranja y Rojo). |
| `graphical_thermal`| **Monitor Térmico & Fade** | Gráfico | Temperaturas duales delantera/trasera, zonas de calor y límites de seguridad. |
| `graphical_cockpit`| **Cockpit HUD de Frenada** | Gráfico | HUD integral de ruta con manómetro, potencia, calor, inclinación y trail badge. |

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

## Campos Developer en Archivos FIT y Análisis Post-Ruta
Durante las rutas grabadas, Karoo BBrakers inyecta campos Developer FIT a 1 Hz:
- `BrakingDeceleration` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` (bandera $0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- `DescentEfficiency` ($\%$)
- `GripMargin` ($\%$)
- Resumen de sesión: Eventos totales, Pico de G, kJ absorbidos e Índice de desgaste.

Compatible con **Strava**, **Intervals.icu**, **GoldenCheetah** y **WKO5**.

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
- **Autor**: hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Dispositivos soportados**: Hammerhead Karoo 2 / Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Licencia**: Licencia MIT
