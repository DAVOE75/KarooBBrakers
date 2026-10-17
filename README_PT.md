# Karoo BBrakers 🚴‍♂️💨🛑
### Telemetria de Travagem Nível WorldTour, Distribuição Dinâmica de Carga, Círculo de Fricção de Kamm & Monitor Térmico para Hammerhead Karoo 2 e Karoo 3

<p align="center">
  <b>Documentação por Idioma / Language Documentation:</b><br>
  <a href="README.md"><b>English 🇬🇧</b></a> | 
  <a href="README_ES.md"><b>Español 🇪🇸</b></a> | 
  <a href="README_DE.md"><b>Deutsch 🇩🇪</b></a> | 
  <a href="README_FR.md"><b>Français 🇫🇷</b></a> | 
  <a href="README_IT.md"><b>Italiano 🇮🇹</b></a> | 
  <a href="README_PT.md"><b>Português 🇵🇹</b></a>
</p>

---

## 📖 Visão Geral
**Karoo BBrakers** é a primeira extensão do mundo dedicada à telemetria integral de travagem, dinâmica inercial e segurança ativa desenvolvida especificamente para o ecossistema **Hammerhead Karoo** (Karoo 2 e o novo Karoo 3).

A análise moderna de rendimento no ciclismo focou-se historicamente no potenciómetro, na frequência cardíaca e na cadência. No entanto, em provas de estrada, descidas de gravel e BTT, **as corridas ganham-se e as quedas evitam-se nas travagens**: na capacidade de retardar o ponto de travagem, no controlo de *trail braking* na curva e na gestão térmica de discos e pistas de carbono. Karoo BBrakers transforma o teu ciclocomputador num cockpit de telemetria profissional ao nível da Fórmula 1 ou MotoGP.

---

## 🔬 Ciência, Física & Algoritmos WorldTour

### 1. Desaceleração Inercial Contínua (Amostragem de Hardware a 50 Hz)
- Utiliza os sensores inerciais físicos do Karoo (`Sensor.TYPE_LINEAR_ACCELERATION` & `Sensor.TYPE_ACCELEROMETER`) a trabalhar a **50 Hz (amostras a cada 20 ms)**.
- **Compensação de Gravidade e Inclinação em Tempo Real**: O algoritmo isola a inclinação estática da estrada através de filtros complementares contínuos. A desaceleração é calculada em unidades físicas diretas ($\text{m/s}^2$ ou Força $G$) com resposta instantânea.
- **Desacoplamento Aerodinâmico e de Rolamento**: O atrito aerodinâmico natural ($F_{aero} = \frac{1}{2}\rho C_d A v^2$) e o coeficiente de atrito dos pneus ($C_{rr}$) são subtraídos dinamicamente para que rolar por inércia em terreno plano nunca seja computado erradamente como travagem.

### 2. Transferência Dinâmica de Carga & Equilíbrio Dianteiro / Traseiro
- Durante a travagem, a inércia transfere o peso do sistema para a área de contacto do pneu dianteiro:
  $$\Delta W = \frac{m \cdot a \cdot h_{CG}}{L_{distancia\_entre\_eixos}}$$
- Em descidas íngremes ou travagens fortes, até **85% da carga normal** concentra-se na roda dianteira. O Karoo BBrakers calcula em tempo real a distribuição entre o travão dianteiro e traseiro, permitindo otimizar a modulação das manetes e evitar derrapagens do eixo traseiro.

### 3. Círculo de Fricção de Kamm & Prevenção de Quedas (*Washout*)
- A aderência do pneu com o asfalto é um vetor limitado por uma envolvente finita:
  $$F_{total} = \sqrt{F_{travagem}^2 + F_{curva}^2} \le \mu \cdot N$$
- Travar bruscamente enquanto a bicicleta está inclinada na curva ($\theta_{inclinacao}$) aumenta drasticamente o risco de perda súbita de aderência dianteira (*front-wheel washout*).
- O Karoo BBrakers monitoriza em tempo real a **Margem de Aderência %**, emitindo alertas visuais e sonoros antes de atingir o limite de atrito.

### 4. Monitor Termodinâmico Duplo dos Rotores & Alerta de Carbono
- **Arrefecimento Diferencial das Pinças**: A pinça dianteira recebe um fluxo de ar laminar limpo a alta velocidade ($h_{diant} \propto v^{0.8}$), enquanto a traseira fica resguardada no cone de ar turbulento das escoras ($h_{tras} \approx 0.65 \cdot h_{diant}$).
- **Limites de Segurança por Material**:
  - **Travões de Disco**: Seguro até 350 °C.
  - **Travões de Calço (Aro em Alumínio)**: Seguro até 150 °C.
  - **Travões de Calço (Aro em Carbono)**: Aviso prévio a 145 °C e alarme crítico aos **180 °C** para evitar a delaminação da resina e o rebentamento do pneu/câmara.

### 5. Deteção de Atrito Parasita (Watts Desperdiçados / Wasted Watts)
- Deteta êmbolos encravados, discos empenados ou pastilhas a roçar continuamente enquanto pedalas.
- Calcula instantaneamente os **Watts desperdiçados** ($P = F_{atrito} \cdot v$) para diagnosticar perdas mecânicas em tempo real.

### 6. Modo Pelotão (Peloton Feathering)
- Num pelotão ou grupo compacto, os ciclistas tocam subtilmente no travão traseiro para manter a distância sem provocar travagens bruscas.
- A extensão isola estes micro-toques (0,2–1,2 $\text{m/s}^2$) com uma insígnia violeta distintiva, sem acionar alarmes desnecessários.

---

## 📊 Catálogo Completo de Campos de Dados (10 Tipos Karoo)

O Karoo BBrakers regista 10 tipos de dados nativos no sistema Karoo (`extension_info.xml`):

| ID do Tipo | Nome no Karoo OS | Formato | Descrição |
|---|---|---|---|
| `current_decel` | **Força de Travagem** | Numérico | Força de desaceleração instantânea em $\text{m/s}^2$, Força $G$ ou $\text{ft/s}^2$. |
| `braking_power` | **Potência de Travagem** | Numérico | Potência cinética dissipada pelos travões em Watts ($W$). |
| `max_decel` | **Pico de Travagem G** | Numérico | Desaceleração máxima de pico registada durante a volta. |
| `brake_events` | **Eventos de Travagem** | Numérico | Número total de toques e travagens detetadas. |
| `brake_energy` | **Energia Dissipada** | Numérico | Trabalho cinético total absorvido em quilojoules ($\text{kJ}$). |
| `pad_wear` | **Desgaste dos Calços** | Numérico | Índice cumulativo de desgaste baseado em energia e composto. |
| `brake_temperature`| **Temperatura dos Travões**| Numérico | Temperatura estimada em tempo real do disco/aro em °C ou °F. |
| `graphical_brake` | **Manómetro de Travagem** | Gráfico | Indicador dinâmico de 5 zonas (Branco $\to$ Amarelo $\to$ Laranja $\to$ Vermelho). |
| `graphical_thermal`| **Monitor Térmico & Fade** | Gráfico | Temperaturas duplas D/T, zonas térmicas e limites de segurança. |
| `graphical_cockpit`| **Cockpit HUD de Travagem** | Gráfico | HUD tudo-em-um com manómetro, potência, calor e margem de aderência. |

---

## 🎨 Código Cromático de Segurança em Marcha
Concebido para uma leitura visual instantânea entre os 40 e os 70 km/h:
- ⚪ **Branco Puro** ($< 1,0\text{ m/s}^2$): Toque inicial na manete / modulação muito suave.
- 🟡 **Amarelo Claro** ($1,0 - 2,0\text{ m/s}^2$): Regulação fina da velocidade.
- 🟡 **Amarelo Vivo** ($2,0 - 3,2\text{ m/s}^2$): Travagem padrão de aproximação a curva.
- 🟠 **Laranja Vivo** ($3,2 - 4,5\text{ m/s}^2$): Travagem firme em descidas acentuadas.
- 🔴 **Vermelho Fogo** ($> 4,5\text{ m/s}^2$): Travagem de emergência / paragem a fundo.
- 🟣 **Violeta**: Modo pelotão ativo (toque fino sem sobressaltos).
- 🟢 **Verde**: Temperatura na zona ideal e segura ($< 80^\circ\text{C}$).

---

## 📱 Campos FIT Developer para Análise Pós-Volta
Durante a gravação da atividade, o Karoo BBrakers injeta campos FIT nativos a 1 Hz:
- `BrakingDeceleration` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` ($0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- Resumo de sessão: Total de eventos, Pico $G$ alcançado, kJ absorvidos e Índice de Desgaste.

Compatível com **Strava**, **Intervals.icu**, **GoldenCheetah** e **WKO5**.

---

## ⚙️ Instalação & Atualizações Diretas (OTA)

### Opção 1: Através do URL do Manifesto no Karoo (Recomendada)
1. Copia o URL do manifesto:
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. No teu Hammerhead Karoo, acede a **Definições $\to$ Extensões $\to$ Adicionar Extensão**.
3. Cola o URL. O Karoo OS instalará a aplicação automaticamente e disponibilizará o botão de atualização assim que lançarmos novas versões.

### Opção 2: Instalação manual por ADB
```bash
adb install -r karoobbrakers.apk
```

---

## 👨‍💻 Autor & Créditos
- **Programador**: hesiox ([DAVOE75](https://github.com/DAVOE75))
- **Dispositivos suportados**: Hammerhead Karoo 2 e Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Licença**: Licença MIT
