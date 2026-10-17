<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="320" />
</p>

# Karoo BBrakers (v1.0.0)

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
  <img src="https://img.shields.io/badge/Units-Metric%20%26%20Imperial%20(UK%20%7C%20US%20%7C%20EU)-00E5FF" alt="Units">
  <img src="https://img.shields.io/badge/Languages-EN%20%7C%20ES%20%7C%20FR%20%7C%20IT%20%7C%20PT%20%7C%20DE-7F52FF" alt="i18n">
  <a href="https://www.buymeacoffee.com/hesiox"><img src="https://img.shields.io/badge/Buy_Me_a_Coffee-hesiox-FFDD00?logo=buy-me-a-coffee&logoColor=black" alt="Buy Me A Coffee"></a>
  <img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg" alt="License">
</p>

---

## 📖 Descripción General

**Karoo BBrakers** es una extensión avanzada de telemetría y seguridad activa diseñada específicamente para ciclocomputadores **Hammerhead Karoo (Karoo 2 y Karoo 3)**.

Siguiendo el estilo de las extensiones de [DAVOE75](https://github.com/DAVOE75) ([Karoo Sentinel](https://github.com/DAVOE75/KarooSentinel), [ALTGRAPH](https://github.com/DAVOE75/ALTGRAPH), [KarooTopoMaps](https://github.com/DAVOE75/KarooTopoMaps)), **Karoo BBrakers** fusiona los acelerómetros internos del Karoo con la velocidad GPS/rueda, cadencia y potencia para calcular con precisión milimétrica la **desaceleración instantánea**, la **potencia cinética disipada (W)**, la **temperatura estimada de frenos (°C / °F)** y el **desgaste acumulado de las pastillas**.

---

## ⚡ Innovaciones de Procesamiento Físico ("Finísimo")

### 1. 💨 Distinción entre Inercia / Rodar Libre y Frenada Real
* **Reducir la velocidad no implica frenar:** Cuando el ciclista deja de pedalear (*freewheeling* o *coasting*), la bicicleta pierde velocidad por **resistencia aerodinámica** ($F_{aero} = \frac{1}{2} \rho C_d A v^2$) y **rozamiento de rodadura** ($F_{rr} = C_{rr} m g$).
* El algoritmo calcula dinámicamente esta deceleración pasiva de arrastre según la masa y la velocidad del ciclista, restándola de la lectura del acelerómetro:
  $$a_{net} = \max(0, a_{medida} - a_{arrastre}(v))$$
  Dejar de pedalear o planear en llano jamás se confunde con una frenada.

### 2. 🎛️ Detección de Mordida ("Toque de Freno") y Liberación ("Soltar el Freno")
* **Toque de Freno (*Brake Bite*):** Al rozar las pastillas con la llanta o disco, se genera un impulso mecánico inmediato (*jerk* positivo $\frac{da}{dt} > 0.65\text{ m/s}^3$) y un momento de cabeceo hacia adelante en el manillar. El sensor capta el inicio exacto del contacto mecánico.
* **Soltar el Freno (*Brake Release*):** Al abrir las manetas de freno, la deceleración cae en picado (*jerk* negativo $\frac{da}{dt} < -1.2\text{ m/s}^3$) y la horquilla se descomprime, finalizando limpiamente el evento y volviendo al estado de rodadura libre (*COASTING*).

### 3. 🎯 Rechazo Estricto de Rampas y Pendientes
* **Bloqueo por Pedaleo:** Si hay cadencia (> 5 RPM) o potencia (> 25 W), la frenada se anula al 100%. Las pérdidas de velocidad al subir un repecho pedaleando no generan datos espurios.
* **Compensación de Inclinación:** Un filtro pasobajo continuo sobre el vector de gravedad aísla la inclinación estática de la carretera ($\theta$), evitando que subir una rampa sin pedalear sea registrado como deceleración frontal.

### 4. 🔥 Modelo Termodinámico de Temperatura (Llantas de Carbono y Discos)
* **Entrada Térmica y Refrigeración por Aire:** Calcula el calor generado por fricción ($Q_{in} \approx 82\% \times P_{frenada}$) y la disipación convectiva con el aire exterior ($Q_{out} \propto v^{0.75} \cdot \Delta T$).
* **Protección Antidelaminación en Carbono:**
  * Alarma acústica y banner visual al alcanzar **145°C / 293°F** en zapatas sobre carbono para evitar la degradación de la resina epoxi de la pista.
  * Umbrales específicos también para frenos de disco (**260°C / 500°F**) y llantas de aluminio (**190°C / 374°F**).

---

## 🌍 Soporte Completo de Unidades Regionales

Diseñado para ciclistas de todo el mundo, con conmutación con un solo toque y detección automática según el idioma/región:

| Región | Sistema | Temperatura | Peso | Desaceleración | Velocidad |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **España, Alemania, Francia, Italia, UE** | **Métrico** | **°C** (Celsius) | **kg** (40–160 kg) | **m/s²** | **km/h** |
| **Reino Unido (UK) y Estados Unidos (USA)** | **Imperial** | **°F** (Fahrenheit) | **lbs** (90–350 lbs) | **G-Force (G) / ft/s²** | **mph** |

*El ciclista puede seleccionar el preajuste regional completo o personalizar cada unidad por separado en la pestaña de Configuración.*

---

## 🎨 Logotipo e Identidad Visual DAVOE75

El nuevo escudo de armas sigue estrictamente la estética de **Karoo Sentinel**, **ALTGRAPH** y **Karoo Topo Maps**:
* **Fondo:** Negro carbón mate con textura de tejido de fibra de carbono.
* **Bicicleta:** Silueta lineal de geometría de carretera en color blanco puro.
* **Discos térmicos:** Rotores encendidos en tonos rojo y naranja térmico.
* **Escala de Intensidad de Frenada:** Barra de espectro segmentada que evoluciona desde el **Blanco** (mordida inicial suave), pasando por el **Amarillo Claro**, **Amarillo Puro**, **Naranja** y culminando en **Rojo Fuego** (máxima deceleración y carga térmica).

---

## 📊 Campos de Datos para Karoo

| ID de Campo | Nombre | Unidad | Descripción |
| :--- | :--- | :---: | :--- |
| `current_decel` | **Fuerza de Frenada** | m/s², G o ft/s² | Desaceleración instantánea limpia de arrastre aerodinámico. |
| `braking_power` | **Potencia de Frenada** | W (Vatios) | Potencia cinética disipada en tiempo real ($P = m \cdot a_{net} \cdot v$). |
| `max_decel` | **Pico de Frenada G** | G, m/s² o ft/s² | Máxima fuerza de frenada registrada en la ruta. |
| `brake_events` | **Eventos de Frenada** | Toques/frenadas | Contador de toques de maneta y detenciones. |
| `brake_energy` | **Energía Disipada** | kJ | Trabajo total absorbido en kilojulios. |
| `pad_wear` | **Desgaste de Pastillas** | Puntos | Índice de desgaste acumulado según material y energía. |
| `brake_temperature` | **Temperatura de Frenos** | °C o °F | Temperatura estimada en tiempo real con límite térmico. |
| `graphical_brake` | **Medidor Gráfico** | Espectro Blanco a Rojo | Widget Glance dinámico con aguja de pico y estado (`BRAKE`, `PEDAL`, `COAST`, `ARMED`). |

---

## 🛠️ Instalación y Compilación

Requisitos: **Android Studio**, **JDK 17** y **Android SDK (API 34)**.

```bash
# Compilar el APK en modo depuración
./gradlew assembleDebug

# Instalar directamente en el Karoo conectado por USB
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

El binario compilado se genera en:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🌐 Internacionalización (6 Idiomas)
* 🇪🇸 **Español** (`res/values-es/`)
* 🇬🇧 **English** (`res/values/`)
* 🇩🇪 **Deutsch** (`res/values-de/`)
* 🇫🇷 **Français** (`res/values-fr/`)
* 🇮🇹 **Italiano** (`res/values-it/`)
* 🇵🇹 **Português** (`res/values-pt/`)

---

## ☕ Invítame a un café

Si disfrutas de **Karoo BBrakers** y de las demás extensiones del ecosistema, puedes apoyar el desarrollo continuo y nuevas funcionalidades invitándome a un café:

<p align="center">
  <a href="https://www.buymeacoffee.com/hesiox" target="_blank">
    <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee (hesiox)" height="52" />
  </a>
</p>

---

## 📄 Licencia

Distribuido bajo la Licencia **Apache 2.0**. Compatible con Hammerhead Karoo 2 y Karoo 3.
Repositorio oficial: [https://github.com/DAVOE75/KarooBBrakers](https://github.com/DAVOE75/KarooBBrakers)
