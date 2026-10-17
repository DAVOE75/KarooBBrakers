<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### Telemetría de Frenada Nivel WorldTour, Reparto de Carga Dinámico, Círculo de Fricción de Kamm y Monitor Térmico Dual para Hammerhead Karoo 2 y Karoo 3

<p align="center">
  <b>Idioma / Language:</b> 
  <a href="README.md"><b>English</b></a> | 
  <a href="README_ES.md"><b>Español</b></a> | 
  <a href="README_DE.md"><b>Deutsch</b></a> | 
  <a href="README_FR.md"><b>Français</b></a> | 
  <a href="README_IT.md"><b>Italiano</b></a> | 
  <a href="README_PT.md"><b>Português</b></a>
</p>

---

## Visión General
**Karoo BBrakers** es una extensión de telemetría integral de frenada, dinámica inercial y seguridad activa desarrollada específicamente para el ecosistema **Hammerhead Karoo** (Karoo 2 y el nuevo Karoo 3).

El rendimiento ciclista moderno se ha centrado históricamente en el potenciómetro, el pulso y la cadencia. Sin embargo, en competición de carretera, descensos de gravel y mountain bike, **las carreras se ganan y las caídas se evitan en las frenadas**: en la capacidad de retrasar el punto de frenada, en el trail braking dentro de la curva y en el control térmico de los discos y llantas. Karoo BBrakers transforma tu ciclocomputador en un cockpit de telemetría profesional comparable al de la Fórmula 1 o MotoGP.

---

## Ciencia, Física y Algoritmos WorldTour

### 1. Desaceleración Inercial Continua (Muestreo Hardware a 50 Hz)
- Emplea los sensores inerciales físicos del propio Karoo (`Sensor.TYPE_LINEAR_ACCELERATION` y `Sensor.TYPE_ACCELEROMETER`) funcionando a **50 Hz (muestras cada 20 ms)**.
- **Filtrado de Gravedad y Pendiente en Tiempo Real**: El algoritmo aísla la inclinación estática de la carretera mediante filtros complementarios. La desaceleración se mide en unidades físicas directas ($\text{m/s}^2$ o Fuerza $G$) con respuesta instantánea.
- **Desacoplamiento Aerodinámico y de Rodadura**: La resistencia natural del viento ($F_{aero} = \frac{1}{2}\rho C_d A v^2$) y el coeficiente de fricción de los neumáticos ($C_{rr}$) se restan dinámicamente para que el mero hecho de rodar por inercia en llano no compute falsamente como frenada.

### 2. Reparto Dinámico de Carga y Balance Delantero/Trasero
- Durante la frenada, la fuerza inercial transfiere peso hacia la rueda delantera:
  $$\Delta W = \frac{m \cdot a \cdot h_{CG}}{L_{batalla}}$$
- En bajadas pronunciadas o frenadas fuertes, hasta el **85% de la carga normal** se concentra en el neumático delantero. Karoo BBrakers calcula en tiempo real el porcentaje de reparto entre el freno delantero y trasero, permitiendo optimizar el tacto de manetas y evitar derrapes del eje posterior.

### 3. Círculo de Fricción de Kamm y Prevención de Caídas
- El agarre del neumático con el asfalto es un vector finito limitado por:
  $$F_{total} = \sqrt{F_{frenada}^2 + F_{curva}^2} \le \mu \cdot N$$
- Frenar bruscamente mientras la bicicleta está tumbada en plena curva ($\theta_{lean}$) dispara la probabilidad de un barrido o lavado repentino de la rueda delantera (*washout*).
- Karoo BBrakers monitoriza en tiempo real el **Margen de Agarre %**, alertando antes de superar el límite de adherencia.

### 4. Monitor Termodinámico Dual de Rotores y Alerta de Carbono
- **Enfriamiento Diferencial de Pinzas**: La pinza delantera recibe un flujo laminar limpio de aire a alta velocidad ($h_{del} \propto v^{0.8}$), mientras que la trasera queda resguardada en el aire turbulento de las vainas ($h_{tras} \approx 0.65 \cdot h_{del}$).
- **Límites de Seguridad por Material**:
  - **Frenos de Disco**: Seguro hasta 350°C.
  - **Frenos de Zapata (Llanta Aluminio)**: Seguro hasta 150°C.
  - **Frenos de Zapata (Llanta de Carbono)**: Aviso preventivo a 145°C y alarma crítica a **180°C** para prevenir la deslaminación de la resina y el reventón del neumático.

### 5. Detección de Rozamiento Parásito (Vatios Perdidos / Wasted Watts)
- Detecta si los pistones se han quedado atascados, si el disco está doblado o si las pastillas rozan continuamente mientras estás pedaleando.
- Calcula los **Vatios Perdidos** ($P = F_{roce} \cdot v$) para diagnosticar pérdidas mecánicas en tiempo real.

### 6. Modo Grupeta (Peloton Feathering)
- En un pelotón o grupeta compacta, los ciclistas realizan toques sutiles del freno trasero para mantener la distancia sin descolgarse ni provocar frenazos.
- La extensión aísla estos microtoques (0.2–1.2 $\text{m/s}^2$) con una insignia violeta distintiva, sin disparar avisos de emergencia innecesarios.

---

## Catálogo Completo de Pantallas y Campos (10 Tipos Karoo)

Karoo BBrakers registra 10 tipos de datos en el sistema Karoo (`extension_info.xml`):

| ID del Campo | Nombre en Karoo OS | Formato | Descripción |
|---|---|---|---|
| `current_decel` | **Fuerza de Frenada** | Numérico | Desaceleración instantánea en $\text{m/s}^2$, Fuerza $G$ o $\text{ft/s}^2$. |
| `braking_power` | **Potencia de Frenada** | Numérico | Potencia cinética disipada por los frenos en Vatios ($W$). |
| `max_decel` | **Pico de Frenada G** | Numérico | Máxima desaceleración pico alcanzada en la salida. |
| `brake_events` | **Eventos de Frenada** | Numérico | Conteo total de toques y frenadas detectadas. |
| `brake_energy` | **Energía Disipada** | Numérico | Energía cinética total absorbida en kilojulios ($\text{kJ}$). |
| `pad_wear` | **Desgaste Estimado** | Numérico | Puntuación de desgaste según trabajo térmico y compuesto. |
| `brake_temperature`| **Temperatura de Frenos** | Numérico | Temperatura estimada en tiempo real de pista/disco en °C o °F. |
| `graphical_brake` | **Manómetro de Frenada** | Gráfico | Escala visual de 5 zonas (Blanco a Amarillo a Naranja a Rojo). |
| `graphical_thermal`| **Monitor Térmico & Fade** | Gráfico | Temperaturas duales D/T, zonas térmicas y límites de material. |
| `graphical_cockpit`| **Cockpit HUD de Frenada** | Gráfico | HUD maestro todo-en-uno con manómetro, vatios, calor y margen de agarre. |

---

## Código Cromático de Seguridad en Marcha
Diseñado para una comprensión visual instantánea pedaleando entre 40 y 70 km/h:
- **Blanco Puro** ($< 1.0\text{ m/s}^2$): Toque inicial de maneta / modulación muy suave.
- **Amarillo Claro** ($1.0 - 2.0\text{ m/s}^2$): Ajuste de distancia en marcha.
- **Amarillo Puro** ($2.0 - 3.2\text{ m/s}^2$): Frenada estándar de aproximación a curva.
- **Naranja Puro** ($3.2 - 4.5\text{ m/s}^2$): Frenada firme en descensos pronunciados.
- **Rojo Fuego** ($> 4.5\text{ m/s}^2$): Frenada de emergencia / parada a fondo.
- **Violeta**: Modo grupeta activo (toque fino sin falsa parada).
- **Verde**: Temperatura en zona óptima y segura ($< 80^\circ\text{C}$).

---

## Campos FIT Developer para Análisis Post-Ruta
Durante la grabación de la actividad, Karoo BBrakers inyecta campos FIT nativos a 1 Hz:
- `BrakingDeceleration` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` ($0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- Resumen de sesión: Total de eventos, Pico G alcanzado, kJ absorbidos e Índice de Desgaste.

Compatible con **Strava**, **Intervals.icu**, **GoldenCheetah** y **WKO5**.

---

## Instalación y Actualización Directa (OTA)

### Opción 1: Mediante la URL del Manifiesto en tu Karoo (Recomendada)
1. Copia la URL del manifiesto:
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. En tu Hammerhead Karoo, ve a **Ajustes -> Extensiones -> Añadir Extensión**.
3. Pega la URL. Karoo OS descargará e instalará la aplicación automáticamente, y te avisará con el botón **ACTUALIZAR** en cuanto subamos nuevas versiones.

### Opción 2: Instalación manual por ADB
```bash
adb install -r karoobbrakers.apk
```

---

## Autor y Créditos
- **Desarrollador**: hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Dispositivos soportados**: Hammerhead Karoo 2 y Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Licencia**: Licencia MIT
