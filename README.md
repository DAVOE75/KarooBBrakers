<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="320" />
</p>

# Karoo BBrakers (v1.0.0)
### WorldTour Braking Telemetry, Dynamic Safety & Wear Analytics for Hammerhead Karoo 2 & Karoo 3

<p align="center">
  <a href="README.md">Español</a> |
  <a href="README_en.md">English</a> |
  <a href="README_fr.md">Français</a> |
  <a href="README_de.md">Deutsch</a> |
  <a href="README_it.md">Italiano</a> |
  <a href="README_pt.md">Português</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Karoo_OS-KOS_1.634+-FF6D00?logo=android&logoColor=white" alt="Karoo OS">
  <img src="https://img.shields.io/badge/Kotlin-2.0.0-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Release-v1.0.0-00E5FF?logo=github&logoColor=white" alt="Release">
  <img src="https://img.shields.io/badge/Units-Metric%20%26%20Imperial%20(UK%20%7C%20US%20%7C%20EU)-00E5FF" alt="Units">
  <img src="https://img.shields.io/badge/Languages-EN%20%7C%20ES%20%7C%20FR%20%7C%20IT%20%7C%20PT%20%7C%20DE-7F52FF" alt="i18n">
  <a href="https://www.buymeacoffee.com/hesiox"><img src="https://img.shields.io/badge/Buy_Me_a_Coffee-hesiox-FFDD00?logo=buy-me-a-coffee&logoColor=black" alt="Buy Me A Coffee"></a>
  <img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg" alt="License">
</p>

---

## 📖 Descripción General

**Karoo BBrakers** es la extensión de telemetría de frenada y seguridad dinámica de nivel **WorldTour** para ciclocomputadores **Hammerhead Karoo (Karoo 2 y Karoo 3)**.

Diseñada bajo los más altos estándares visuales y de ingeniería de [DAVOE75](https://github.com/DAVOE75) ([Karoo Sentinel](https://github.com/DAVOE75/KarooSentinel), [ALTGRAPH](https://github.com/DAVOE75/ALTGRAPH), [KarooTopoMaps](https://github.com/DAVOE75/KarooTopoMaps)), **Karoo BBrakers** fusiona los acelerómetros y giróscopos triaxiales del Karoo con los sensores de velocidad de rueda, cadencia y potenciómetro para modelar con rigor físico:
1. **Desaceleración Instantánea Neta y Pico G** (eliminando fricción aerodinámica e inercia).
2. **Transferencia Dinámica de Carga y Reparto Delantero/Trasero** (55% a 85% delante según desaceleración y pendiente).
3. **Círculo de Fricción de Kamm y Margen de Agarre** (combinando inclinación en curva $\theta_{lean}$ con frenada para evitar caídas por pérdida de rueda delantera).
4. **Detección de Arrastre Involuntario de Frenos (Vatios Perdidos)** por roces de pastillas o pistones pegados.
5. **Modo Grupeta / Pelotón** (aislando toques suaves de modulación trasera a rueda sin disparar falsas paradas).
6. **Monitor Térmico Dual Delantero y Trasero** con protección contra delaminación de resina en llantas de carbono y fatiga (*fade*).

---

## 🔬 Ciencia y Física de Nivel WorldTour

```
                         ▲ Vector Gravedad / Inclinación
                         │
     [Freno Delantero]   │      [Freno Trasero]
           (Cyan)        │          (Ámbar)
            ▼            │             ▼
    ╭──────────────────────────────────────────────╮
    │    CARGA DINÁMICA: 75% F  │  25% R           │
    │    PENDIENTE: -7.5%   │   INCLINACIÓN: 28°   │
    │    CÍRCULO DE KAMM:  82% MARGEN DE AGARRE    │
    ╰──────────────────────────────────────────────╯
                         │
                         ▼ Vector Aceleración Neta
```

### 1. ⚖️ Transferencia Dinámica de Carga y Reparto F/R
En frenada plana, la distribución estática de masa es aproximadamente 40% delante y 60% detrás. Sin embargo, bajo desaceleración longitudinal ($a_x$), la inercia genera un momento de cabeceo que transfiere peso hacia el parche de contacto delantero:
$$N_{delantero} = W \cdot \left(\frac{l_r}{L} + \frac{h_{cdg}}{L} \cdot \frac{a_x}{g}\right) + W \cdot \sin(\theta_{pendiente})$$
En bajadas pronunciadas (-7% a -15%) o frenadas fuertes (> 3.5 m/s²), la carga delantera alcanza entre el **75% y el 85%**. Karoo BBrakers calcula y muestra en tiempo real esta distribución porcentual.

### 2. ⭕ Círculo de Fricción de Kamm y Margen de Agarre (Anti-Lavado)
La adherencia del neumático con el asfalto o tierra es un límite finito definido por el coeficiente de fricción ($\mu \cdot N$). Al frenar dentro de una curva, la adherencia disponible se divide entre la aceleración lateral ($a_y = g \cdot \tan\theta_{lean}$) y la deceleración longitudinal ($a_x$):
$$a_{combinada} = \sqrt{a_x^2 + a_y^2} \le \mu \cdot g$$
El indicador de **Margen de Agarre (%)** avisa al ciclista antes de alcanzar el límite crítico de deslizamiento, protegiéndolo de caídas por lavado de rueda delantera en curvas mojadas o grava.

### 3. 🚴‍♂️ Modo Grupeta / Pelotón & Detección de Arrastre (Vatios Perdidos)
* **Toque a Rueda (*Peloton Feathering*):** En abanicos o grupeta a alta velocidad, el ciclista roza suavemente el freno trasero para ajustar 10-20 cm sin frenar bruscamente. Karoo BBrakers lo clasifica como estado **GRUPETA** (distintivo violeta) sin registrar una parada falsa.
* **Arrastre Involuntario (*Wasted Watts*):** Cuando el ciclista pedalea (>25 W) pero los sensores detectan una deceleración residual constante, el algoritmo calcula los **Vatios Perdidos** ($P_{arrastre} = m \cdot a_{friccion} \cdot v$) por rozamiento de zapatas o discos desalineados.

### 4. 🌡️ Modelo Termodinámico Dual (Llantas de Carbono y Discos)
* **Refrigeración Diferenciada:** El disco/pista delantera recibe flujo laminar directo y limpio de aire (alto coeficiente convectivo $h$), mientras que el freno trasero está resguardado en la turbulencia de vainas y bielas.
* **Alarma Crítica en Carbono (180°C / 356°F):** Alerta sonora por zumbador hardware y parpadeo visual antes de que la resina epoxi de la pista sufra delaminación térmica.

---

## 🎨 Código Visual de Colores al Instante (40–70 km/h)

Diseñado para comprensión visual periférica e inmediata a alta velocidad:

### 1. Espectro de Intensidad de Frenada (5 Segmentos)
| Segmento | Color | Hex | Intensidad | Contexto de Frenada |
| :---: | :---: | :---: | :---: | :--- |
| **1** | **Blanco Puro** | `#FFFFFF` | Mordida Inicial | Contacto leve de pastilla / ajuste milimétrico |
| **2** | **Amarillo Claro** | `#FFF176` | Suave (< 1.5 m/s²) | Desaceleración suave en falso llano |
| **3** | **Amarillo Puro** | `#FFD600` | Moderada (1.5–3.0 m/s²) | Frenada habitual de entrada a curva |
| **4** | **Naranja Puro** | `#FF6D00` | Firme (3.0–4.5 m/s²) | Descenso pronunciado de puerto de montaña |
| **5** | **Rojo Fuego** | `#D50000` | Emergencia (> 4.5 m/s²) | Detención brusca / Alarma visual y acústica |

### 2. Canales y Estados de Ruta
* 🔵 **Azul Cian (`#00E5FF`):** Freno delantero & Potencia disipada (W).
* 🟡 **Ámbar (`#FFB74D`):** Freno trasero & Reparto posterior.
* 🟣 **Violeta (`#AB47BC`):** Modo Grupeta (*Peloton Feathering* activo).
* 🟢 **Verde Neón (`#00E676`):** Sistema Armado (*ARMED*) & Zona Térmica Segura (< 80°C).
* 🔴 **Rojo Neón (`#FF1744`):** Alarma de Emergencia & Zona Térmica Crítica (> 230°C).

---

## 📱 Pantallas y Widgets Gráficos en Ruta

Karoo BBrakers ofrece tres widgets gráficos Glance para los perfiles de ruta del Karoo:

1. **Brake Gauge (Graphical)** (`graphical_brake`): Manómetro de 5 segmentos graduados, distintivo de estado dinámico (`ARMED`, `BRAKE`, `GRUPETA`, `PEDAL`, `COAST`), marcador de Pico G y barra de equilibrio Delantero/Trasero.
2. **Thermal & Fade Monitor (Graphical)** (`graphical_thermal`): Monitor térmico independiente de disco delantero y trasero con 4 zonas de calor y advertencia de límite de material.
3. **Master Cockpit HUD (Graphical)** (`graphical_cockpit`): HUD integral con deceleración, vatios de disipación, temperaturas duales, ángulo de inclinación y margen de agarre del Círculo de Kamm.

---

## 📊 Catálogo Completo de Campos de Datos

| ID de Campo | Nombre | Tipo | Unidad | Descripción |
| :--- | :--- | :---: | :---: | :--- |
| `current_decel` | **Fuerza de Frenada** | Numérico | m/s², G, ft/s² | Desaceleración instantánea libre de arrastre aerodinámico. |
| `braking_power` | **Potencia de Frenada** | Numérico | W (Vatios) | Potencia cinética disipada en tiempo real ($P = m \cdot a \cdot v$). |
| `max_decel` | **Pico de Frenada G** | Numérico | G, m/s², ft/s² | Máxima fuerza de deceleración registrada en la ruta. |
| `brake_events` | **Eventos de Frenada** | Numérico | Toques | Contador de frenadas y toques de maneta. |
| `brake_energy` | **Energía Disipada** | Numérico | kJ | Trabajo total absorbido en kilojulios. |
| `pad_wear` | **Desgaste de Pastillas** | Numérico | Puntos | Índice de desgaste acumulado según material y energía. |
| `brake_temperature` | **Temperatura de Frenos** | Numérico | °C o °F | Temperatura estimada con límite térmico de seguridad. |
| `graphical_brake` | **Manómetro de Frenada** | Gráfico | Espectro 5 zonas | Manómetro dinámico Blanco $\to$ Rojo con Pico G y balance F/R. |
| `graphical_thermal` | **Monitor Térmico & Fade** | Gráfico | Zonas Térmicas | Temperaturas duales Delantera/Trasera y alerta de delaminación. |
| `graphical_cockpit` | **Cockpit HUD de Frenada** | Gráfico | HUD Integral | Telemetría completa con Círculo de Kamm, vatios e inclinación. |

---

## 🌍 Sistema de Unidades Regionales

| Región | Sistema | Temperatura | Peso | Desaceleración | Velocidad |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **España, Alemania, Francia, Italia, Portugal, UE** | **Métrico** | **°C** (Celsius) | **kg** (40–160 kg) | **m/s²** | **km/h** |
| **Reino Unido (UK) y Estados Unidos (USA)** | **Imperial** | **°F** (Fahrenheit) | **lbs** (90–350 lbs) | **G-Force (G) / ft/s²** | **mph** |

---

## 🛠️ Instalación y Actualizaciones Automáticas

### Validación Hammerhead y Actualizaciones OTA
La extensión cumple rigurosamente con las especificaciones de Hammerhead (Validaciones 1 y 2):
* **Manifest Remoto:** Declarado en `AndroidManifest.xml` mediante `io.hammerhead.karooext.MANIFEST_URL` apuntando a `manifest.json`.
* **APK Firmado:** Los binarios de release se distribuyen firmados y listos para instalación.

### Descarga e Instalación Rápida
1. Descarga el archivo [karoobbrakers.apk](https://github.com/DAVOE75/KarooBBrakers/releases/latest/download/karoobbrakers.apk) desde las [Releases de GitHub](https://github.com/DAVOE75/KarooBBrakers/releases).
2. Instálalo en tu Karoo mediante **SideQuest**, la herramienta de sideloading web de Karoo o por ADB:
```bash
adb install -r karoobbrakers.apk
```

---

## 🌐 Internacionalización (6 Idiomas Completos)
* 🇪🇸 **Español** (`res/values-es/`)
* 🇬🇧 **English** (`res/values/`)
* 🇩🇪 **Deutsch** (`res/values-de/`)
* 🇫🇷 **Français** (`res/values-fr/`)
* 🇮🇹 **Italiano** (`res/values-it/`)
* 🇵🇹 **Português** (`res/values-pt/`)

---

## ☕ Invítame a un café

Si disfrutas de **Karoo BBrakers** y de las demás extensiones del ecosistema de [DAVOE75](https://github.com/DAVOE75), puedes apoyar el desarrollo continuo y nuevas innovaciones invitándome a un café:

<p align="center">
  <a href="https://www.buymeacoffee.com/hesiox" target="_blank">
    <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee (hesiox)" height="52" />
  </a>
</p>

---

## 📄 Licencia

Distribuido bajo la Licencia **Apache 2.0**. Compatible con Hammerhead Karoo 2 y Karoo 3.
Repositorio oficial: [https://github.com/DAVOE75/KarooBBrakers](https://github.com/DAVOE75/KarooBBrakers)
Desarrollado con pasión por **hesiox** ([DAVOE75](https://github.com/DAVOE75)).
