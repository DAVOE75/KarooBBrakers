<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### Telemetria de Travagem WorldTour PRO, Radar de Trail-Braking, Eficiência em Descida & Odómetro de Pastilhas para Hammerhead Karoo 2 & Karoo 3

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
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases/latest"><img src="https://img.shields.io/github/v/release/DAVOE75/KarooBBrakers?style=flat-square&logo=android&logoColor=white&label=Vers%C3%A3o&color=00C853" alt="Última Versão" /></a>
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases"><img src="https://flat.badgen.net/github/assets-dl/DAVOE75/KarooBBrakers?label=Transfer%C3%AAncias&color=0091EA&icon=github" alt="Total de Transferências" /></a>
  <img src="https://img.shields.io/badge/Dispositivo-Karoo_2_•_Karoo_3-1B1B1B?style=flat-square&logo=googlemaps&logoColor=00E676" alt="Compatibilidade Karoo" />
  <a href="https://github.com/DAVOE75/KarooBBrakers/blob/main/LICENSE"><img src="https://img.shields.io/github/license/DAVOE75/KarooBBrakers?style=flat-square&label=Licen%C3%A7a&color=455A64" alt="Licença" /></a>
  <a href="https://buymeacoffee.com/hesiox"><img src="https://img.shields.io/badge/Apoiar-Buy_Me_a_Coffee-FF813F?style=flat-square&logo=buy-me-a-coffee&logoColor=white" alt="Buy Me A Coffee" /></a>
</p>

---

## Visão Geral
**Karoo BBrakers** é uma extensão abrangente de telemetria de travagem, dinâmica inercial e segurança ativa desenvolvida propositadamente para o ecossistema **Hammerhead Karoo** (Karoo 2 e Karoo 3).

A análise moderna do desempenho ciclista focou-se quase exclusivamente no medidor de potência, na frequência cardíaca e na cadência. Contudo, nas descidas de alta montanha, em gravel e em BTT, **as corridas ganham-se e as quedas evitam-se na travagem**: na capacidade de travar por impulsos firmes, no trail-braking até ao vértice da curva e na gestão térmica de discos e pastilhas. O Karoo BBrakers transforma o teu ciclocomputador Hammerhead num verdadeiro cockpit de telemetria WorldTour.

---

## Física, Ciência e Algoritmos WorldTour PRO

### 1. Desaceleração Inercial Contínua (Amostragem Hardware a 50 Hz)
- Utiliza os sensores inerciais físicos IMU do Karoo (`Sensor.TYPE_LINEAR_ACCELERATION` e `Sensor.TYPE_ACCELEROMETER`) em execução a **50 Hz (atualizações a cada 20 ms)**.
- **Desacoplamento de Inclinação e Gravidade em Tempo Real**: Os filtros complementares isolam a pendente da estrada. A desaceleração é medida diretamente em unidades físicas ($\text{m/s}^2$ ou Força $G$) com resposta instantânea.
- **Filtro Aerodinâmico e de Resistência ao Rolamento**: A resistência do ar ($F_{aero} = \frac{1}{2}\rho C_d A v^2$) e o atrito dos pneus ($C_{rr}$) são subtraídos dinamicamente para não registar o rolar por inércia no plano como travagem.

### 2. Pontuação de Eficiência em Descida (DES 0–100%)
- Em descidas de montanha, a técnica profissional exige travagens curtas e firmes por impulsos antes da curva, soltando as manetes nas retas para permitir o arrefecimento por convecção.
- Arrastar continuamente os travões impede a dissipação de calor e leva à cristalização ou vitrificação da resina das pastilhas.
- O algoritmo DES avalia a tua técnica de 0 a 100%, premiando travagens limpas e penalizando apoios contínuos de mais de 5 segundos.

### 3. Radar de Trail-Braking e Entrada em Curva
- O trail-braking consiste em manter a força de travagem ao entrar na curva, aliviando-a gradualmente à medida que a inclinação ($\theta_{lean}$) aumenta em direção ao ponto de tangência ou vértice.
- O Karoo BBrakers monitoriza o ângulo de inclinação e a inércia longitudinal ($da/dt$) em tempo real para classificar a técnica em 4 classes WorldTour:
  - **PERFECT**: Forte desaceleração à entrada que se solta progressivamente conforme a bicicleta deita.
  - **GOOD**: Boa modulação com controlo seguro da velocidade.
  - **EARLY RELEASE**: Libertação precipitada antes da curva em reta, com perda de velocidade.
  - **PANIC LEAN**: Travagem brusca e perigosa com a bicicleta já muito inclinada na curva.

### 4. Transferência Dinâmica de Carga e Repartição Dianteira/Traseira
- Numa travagem forte, a inércia transfere até **85% da carga normal** para o pneu dianteiro:
  $$\Delta W = \frac{m \cdot a \cdot h_{CG}}{L_{distancia\_entre\_eixos}}$$
- O Karoo BBrakers calcula em tempo real a repartição percentual entre travão dianteiro e traseiro, ajudando a dosear a pressão e a evitar o bloqueio da roda traseira.

### 5. Círculo de Fricção de Kamm e Prevenção de Quedas
- A aderência do pneu com o piso é um limite vetorial finito:
  $$F_{total} = \sqrt{F_{travagem}^2 + F_{curva}^2} \le \mu \cdot N$$
- Travar com intensidade em plena inclinação de curva dispara o risco de perda repentina da roda dianteira (*washout*).
- O Karoo BBrakers calcula em tempo real a tua **Margem de Aderência %**, avisando antes do limite de escorregamento.

### 6. Odómetro Digital de Serviço de Pastilhas
- Regista a energia total acumulada absorvida pelas tuas pastilhas de travão em kilojoules ($\text{kJ}$).
- Capacidades nominais calibradas por composto:
  - **Orgânicas / Resina**: ~2500 kJ de capacidade nominal. Mordida rápida, silenciosas, menor tolerância ao calor.
  - **Semi-Metálicas**: ~4500 kJ de capacidade nominal. Equilíbrio ideal para todas as condições meteorológicas.
  - **Metálicas / Sinterizadas**: ~7000 kJ de capacidade nominal. Máxima resistência a altas temperaturas e máxima durabilidade.
- Apresenta a **Vida Útil Restante em %** e permite repor o odómetro com um simples toque na aplicação após a substituição.

### 7. Monitor Térmico Duplo e Alerta de Cristalização
- **Arrefecimento Diferencial das Pinças**: A pinça dianteira recebe ar limpo a alta velocidade ($h_{diant} \propto v^{0.8}$), enquanto a traseira fica na esteira turbulenta das escoras ($h_{tras} \approx 0.65 \cdot h_{diant}$).
- **Limites de Segurança por Material**:
  - **Travões de Disco**: Seguros até 350°C.
  - **Travões de Aro em Alumínio**: Seguros até 150°C.
  - **Travões de Aro em Carbono**: Aviso preventivo a 145°C e alarme crítico a **180°C**.
- **Alarme Sonoro de Cristalização**: Avisa imediatamente se uma travagem contínua ultrapassar 22 segundos a temperaturas que ameacem a vitrificação da resina.

### 8. Deteção de Atrito Parasita (Watts Desperdiçados / Wasted Watts)
- Deteta em tempo real pistões presos, discos empenados ou pastilhas a roçar enquanto pedalas.
- Calcula instantaneamente os **Watts Desperdiçados** ($P = F_{rub} \cdot v$) para poupar a tua energia.

### 9. Throttling de RemoteViews (8 Hz)
- A transmissão de dados para o Karoo OS está otimizada a 120 ms (~8 Hz), eliminando a sobrecarga do Binder e prolongando consideravelmente a autonomia da bateria em tiradas longas.

---

## Catálogo Completo dos Campos de Dados (14 Tipos Nativos)

O Karoo BBrakers regista 14 tipos de dados nativos no Karoo OS (`extension_info.xml`):

| Type ID | Nome no Ecrã | Formato | Descrição |
|---|---|---|---|
| `current_decel` | **Força de Travagem** | Numérico | Desaceleração instantânea em $\text{m/s}^2$, Força $G$ ou $\text{ft/s}^2$. |
| `braking_power` | **Potência de Travagem** | Numérico | Potência cinética dissipada pelos travões em Watts ($W$). |
| `max_decel` | **Pico Desaceleração G** | Numérico | Desaceleração máxima registada durante a volta. |
| `brake_events` | **Eventos de Travagem** | Numérico | Contagem total de toques e travagens. |
| `brake_energy` | **Energia Dissipada** | Numérico | Energia cinética total absorvida em kilojoules ($\text{kJ}$). |
| `pad_wear` | **Desgaste Estimado** | Numérico | Índice de desgaste acumulado com base na energia e composto. |
| `pad_life` | **Vida das Pastilhas** | Numérico | Percentagem de vida útil restante com base nos kJ acumulados. |
| `descent_efficiency`| **Eficiência Descida (DES)** | Numérico | Pontuação WorldTour (0–100%) que premeia impulsos sobre arrasto. |
| `wasted_watts` | **Watts Desperdiçados** | Numérico | Potência parasita dissipada por pastilha a roçar em Watts. |
| `grip_margin` | **Margem de Aderência** | Numérico | Margem de segurança no círculo de Kamm antes de escorregar. |
| `brake_temperature`| **Temperatura dos Travões**| Numérico | Temperatura estimada em tempo real do disco/aro em °C ou °F. |
| `graphical_brake` | **Manómetro de Travagem** | Gráfico | Indicador dinâmico de 5 segmentos (Branco, Amarelo, Laranja, Vermelho). |
| `graphical_thermal`| **Monitor Térmico & Fade** | Gráfico | Temperaturas duplas, zonas de calor e limites de segurança. |
| `graphical_cockpit`| **Cockpit HUD de Travagem**| Gráfico | HUD tudo-em-um com manómetro, Watts, calor, inclinação e badge trail. |

---

## Código Visual de Cores em Marcha
- **Branco** ($< 1.0\text{ m/s}^2$): Contacto inicial / modulação suave.
- **Amarelo Claro** ($1.0 - 2.0\text{ m/s}^2$): Ajuste fino de distância em grupo.
- **Amarelo Puro** ($2.0 - 3.2\text{ m/s}^2$): Travagem padrão antes de curva.
- **Laranja** ($3.2 - 4.5\text{ m/s}^2$): Travagem firme em pendente forte.
- **Vermelho** ($> 4.5\text{ m/s}^2$): Travagem de emergência / paragem brusca.
- **Violeta**: Modo pelotão ativo (toques subtis na roda traseira).
- **Verde**: Temperatura ideal de funcionamento ($< 80^\circ\text{C}$).

---

## Campos Developer FIT & Análise Pós-Volta
Durante as voltas gravadas, o Karoo BBrakers injeta campos FIT a 1 Hz:
- `BrakingDeceleration` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` (indicador $0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- `DescentEfficiency` ($\%$)
- `GripMargin` ($\%$)
- Resumo da sessão: Total de eventos, Pico G, kJ absorvidos e pontuação de desgaste.

Compatível com **Strava**, **Intervals.icu**, **GoldenCheetah** e **WKO5**.

---

## Instalação e Atualizações OTA

### Opção 1: Instalação Direta por Manifest no Karoo
1. Copia este URL de Manifest:
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. No teu Hammerhead Karoo, acede a **Definições -> Extensões -> Adicionar Extensão**.
3. Cola o URL. O Karoo OS instalará o Karoo BBrakers automaticamente e avisará de novas versões.

### Opção 2: Instalação manual por ADB
```bash
adb install -r karoobbrakers.apk
```

---

## Desenvolvedor e Créditos
- **Autor**: hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Dispositivos suportados**: Hammerhead Karoo 2 / Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Licença**: Licença MIT
