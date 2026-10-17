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
  <img src="https://img.shields.io/badge/Languages-EN%20%7C%20ES%20%7C%20FR%20%7C%20IT%20%7C%20PT%20%7C%20DE-00E5FF" alt="i18n">
  <img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg" alt="License">
</p>

---

## 📖 Descripción General

**Karoo BBrakers** es una extensión avanzada de telemetría y seguridad activa diseñada específicamente para ciclocomputadores **Hammerhead Karoo (Karoo 2 y Karoo 3)**.

Diseñada en consonancia con el ecosistema de extensiones de [DAVOE75](https://github.com/DAVOE75) ([Karoo Sentinel](https://github.com/DAVOE75/KarooSentinel), [ALTGRAPH](https://github.com/DAVOE75/ALTGRAPH), [KarooTopoMaps](https://github.com/DAVOE75/KarooTopoMaps)), **Karoo BBrakers** fusiona los acelerómetros internos de alta frecuencia con los flujos de telemetría del Karoo (velocidad, cadencia y potencia) para calcular la **desaceleración instantánea ($m/s^2$ o $G$)**, la **potencia de frenada disipada (W)**, la **estimación térmica continua de la pista de frenado/rotor (°C)** y el **desgaste acumulado de las pastillas**.

---

## ⚡ Características Principales

### 1. 🎯 Filtro Inteligente de Pedaleo y Pendientes (Antirrampas)
* **Cero falsos positivos en subidas:** Si el ciclista está pedaleando (Cadencia > 5 RPM o Potencia > 25 W), la detección de frenada queda estrictamente **bloqueada a 0**. La deceleración natural al aproximarse a una rampa o repecho sin pedalear jamás se confunde con una frenada gracias al análisis del perfil de sacudida (*jerk* de mordida $\Delta a / \Delta t$).
* **Supresión Dinámica de Vibraciones:** Filtro pasobajo adaptativo con perfiles para **Carretera (`ROAD`)**, **Gravel (`GRAVEL`)** y **MTB (`MTB`)**, eliminando oscilaciones provocadas por asfalto rugoso, adoquines o baches.

### 2. 🔥 Estimación Termodinámica de Temperatura de Frenada (Llantas de Carbono y Discos)
* **Modelo Físico Continuo:** Modela la tasa de entrada térmica ($Q_{in} = \eta \cdot P_{frenada}$) y la disipación convectiva por flujo de aire en función de la velocidad ($Q_{out} = (k_0 + k_1 \cdot v^{0.75}) \cdot \Delta T$).
* **Protección Específica para Llantas de Carbono:**
  * En llantas de carbono con zapata, la baja conductividad transversal de la fibra concentra el calor en la resina de la pista de frenado.
  * Al superar los **105°C (Aviso)** o alcanzar **145°C (Crítico)**, el Karoo emite una alerta acústica y visual en pantalla recomendando soltar frenos intermitentemente para ventilar y evitar la delaminación o deformación de la llanta.
* **Frenos de Disco y Llanta de Aluminio:** Umbrales adaptados a la capacidad térmica del acero inoxidable y la aleación de aluminio.

### 3. ⚖️ Física de Potencia Cinética Real ($P = m \cdot a \cdot v$)
* El ciclista configura el **peso total (Ciclista + Bicicleta + Equipamiento)** de 40 kg a 160 kg.
* Cálculo en tiempo real de:
  * **Fuerza de Frenada ($F$):** $F = M \cdot a$ (Newtons)
  * **Potencia Instantánea ($P$):** $P = F \cdot v = M \cdot a \cdot v$ (Vatios)
  * **Energía Cinética Absorbida ($E$):** $E = \int P \, dt$ (Kilojulios)

### 4. 🚨 Alarmas Activas de Seguridad en Marcha
* **Alarma de Frenada Brusca:** En detenciones repentinas ($> 4.2 \text{ m/s}^2$ / $0.43\text{ G}$), se activa un patrón de zumbador físico (`PlayBeepPattern`) y un banner en pantalla (`InRideAlert`).
* **Alerta de Sobrecalentamiento:** Avisa en descensos alpinos de gran desnivel continuo si los frenos están acumulando temperatura excesiva.

### 5. 📊 Campos de Datos para Pantallas Karoo
* **Fuerza de Frenada:** Deceleración en tiempo real en $\text{m/s}^2$ o $\text{G}$.
* **Potencia de Frenada:** Vatios disipados por los frenos (W).
* **Temperatura de Frenada:** Temperatura estimada en tiempo real (°C).
* **Pico de Frenada (G):** Máximo valor de desaceleración de la ruta.
* **Eventos de Frenada:** Contador de accionamientos de freno.
* **Energía Disipada:** Kilojulios totales absorbidos (kJ).
* **Desgaste de Pastillas:** Puntuación de desgaste ponderada por material (disco / aluminio / carbono).
* **Tacómetro Gráfico (Glance):** Indicador visual dinámico con escala de 5 segmentos en color (Verde $\to$ Ámbar $\to$ Rojo) y marcador de pico.

### 6. 📁 Campos de Desarrollador FIT (Strava e Intervals.icu)
Todos los datos se inyectan a 1 Hz en el archivo `.fit` nativo de Karoo (`WriteToRecordMesg`) y se resumen al finalizar en el mensaje de sesión (`WriteToSessionMesg`), permitiendo visualizar la telemetría completa de frenada en plataformas de análisis.

---

## 🌍 Idiomas Disponibles (6 Idiomas)

* 🇪🇸 **Español**
* 🇬🇧 **English**
* 🇫🇷 **Français**
* 🇮🇹 **Italiano**
* 🇵🇹 **Português**
* 🇩🇪 **Deutsch**

---

## 🛠️ Instalación y Configuración

1. Descarga el archivo `app-debug.apk` de [Releases](https://github.com/DAVOE75/KarooBBrakers/releases).
2. Instálalo en tu Hammerhead Karoo 2 o Karoo 3 mediante sideloading (ADB o Karoo sideload tools).
3. Abre **Karoo BBrakers** desde el menú de aplicaciones.
4. Selecciona tu **Tipo de Frenos**, introduce tu **Peso Total** y ajusta tu **Perfil de Terreno**.
5. En los ajustes de tu Perfil de Ruta del Karoo, añade cualquiera de los campos de datos de **Karoo BBrakers** a tus páginas de visualización.

---

## 🏗️ Compilación desde el Código Fuente

```bash
git clone https://github.com/DAVOE75/KarooBBrakers.git
cd KarooBBrakers
./gradlew assembleDebug
```

El APK compilado se encuentra en:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🤝 Autor

Desarrollado por **[DAVOE75](https://github.com/DAVOE75)**  
Creador de [Karoo Sentinel](https://github.com/DAVOE75/KarooSentinel), [ALTGRAPH](https://github.com/DAVOE75/ALTGRAPH) y [KarooTopoMaps](https://github.com/DAVOE75/KarooTopoMaps).

---

## 📄 Licencia

Distribuido bajo la [Licencia Apache 2.0](LICENSE).
