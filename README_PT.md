<p align="center">
  <img src="art/logo.png" alt="Karoo BBrakers Logo" width="280">
</p>

# Karoo BBrakers
### Telemetria de Travagem WorldTour PRO, Radar de Curvas & Velocidade no Ápice, Limite de Aderência Kamm, Energia Metabólica & Fluidez em Pelotão (PEI) para Hammerhead Karoo 2 & Karoo 3

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
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases/latest"><img src="https://img.shields.io/github/v/release/DAVOE75/KarooBBrakers?style=flat-square&logo=android&logoColor=white&label=Vers%C3%A3o&color=00C853" alt="Última versão" /></a>
  <a href="https://github.com/DAVOE75/KarooBBrakers/releases"><img src="https://flat.badgen.net/github/assets-dl/DAVOE75/KarooBBrakers?label=Transfer%C3%AAncias&color=0091EA&icon=github" alt="Transferências totais" /></a>
  <img src="https://img.shields.io/badge/Dispositivo-Karoo_2_•_Karoo_3-1B1B1B?style=flat-square&logo=googlemaps&logoColor=00E676" alt="Compatibilidade Karoo" />
  <a href="https://github.com/DAVOE75/KarooBBrakers/blob/main/LICENSE"><img src="https://img.shields.io/github/license/DAVOE75/KarooBBrakers?style=flat-square&label=Licen%C3%A7a&color=455A64" alt="Licença" /></a>
  <a href="https://buymeacoffee.com/hesiox"><img src="https://img.shields.io/badge/Apoiar-Buy_Me_a_Coffee-FF813F?style=flat-square&logo=buy-me-a-coffee&logoColor=white" alt="Buy Me A Coffee" /></a>
</p>

---

## Visão Geral
**Karoo BBrakers** é uma extensão avançada de telemetria de travagem, dinâmica inercial, segurança preditiva e inteligência de corrida desenvolvida especificamente para a plataforma **Hammerhead Karoo** (Karoo 2 e Karoo 3).

A análise moderna do desempenho no ciclismo concentrou-se quase exclusivamente em medidores de potência, ritmo cardíaco e cadência. No entanto, em competição de estrada, descidas alpinas, gravel e BTT, **as corridas ganham-se e as quedas evitam-se na travagem**: na capacidade de modular por impulsos, no trail-braking até ao ponto de viragem (ápice) e na gestão térmica de discos e pastilhas. O Karoo BBrakers transforma o teu ciclocomputador num cockpit de telemetria WorldTour.

---

## Pré-visualizações do Cockpit e Telemetria em Corrida

<p align="center">
  <img src="art/02_overview_hard_braking.png" width="31%" alt="HUD Cockpit de Travagem Forte" />
  <img src="art/01_grid8_hard_braking.png" width="31%" alt="Grelha de Telemetria de 8 Campos" />
  <img src="art/04_overview_trail_braking.png" width="31%" alt="Trail-Braking & Pontuação DES WorldTour" />
</p>

<p align="center">
  <em>Esquerda: Cockpit HUD em direto e Monitor Térmico duplo durante travagem forte em curva apertada | Centro: Grelha de competição de 8 campos com margens perimétricas reativas por cor | Direita: Deteção pro de Trail-Braking e pontuação de Eficiência em Descida WorldTour (DES 97%).</em>
</p>

---

## Física, Algoritmos & Engenharia WorldTour

### 1. Bloqueio em Parado & Imunidade a Falsos Positivos (< 4 km/h)
- Sem microinterruptores físicos nas manetes nem sensores hidráulicos, um teste na secretária ou a bicicleta parada num semáforo nunca devem registar travagem.
- Quando a velocidade por GPS ou sensor de roda é inferior a $1.11\text{ m/s}$ ($4.0\text{ km/h}$), todos os vetores de desaceleração são estritamente forçados a $0.00\text{ m/s}^2$. O sistema permanece solidamente no estado **ARMADO** e a deteção instantânea ativa-se no milissegundo em que a bicicleta começa a rolar.

### 2. Desacoplamento da Gravidade em Subidas Íngremes
- Em subidas acentuadas, a gravidade puxa o acelerómetro interno para trás ao longo do eixo longitudinal ($g \cdot \sin(\theta)$).
- O Karoo BBrakers calcula o ângulo instantâneo de inclinação $\theta = \arcsin\left(\frac{-g_y}{9.80665}\right)$ e subtrai a componente gravitacional da subida:
  $$a_{\text{travagem, líquida}} = a_{\text{medida, y}} - \max(0, g \cdot \sin(\theta))$$
- Rampas de $15\% - 25\%$ nunca geram falsas desacelerações.

### 3. Radar de Curvas & Retenção de Velocidade no Ápice (0–100%)
- Regista a velocidade de entrada ($v_{\text{entrada}}$), a velocidade mínima no ápice ($v_{\text{ápice}}$) e a aceleração à saída ($\Delta v_{\text{saída}}$).
- Calcula a **Percentagem de Retenção de Velocidade no Ápice**:
  $$\text{Retenção Ápice} = \left(\frac{v_{\text{ápice}}}{v_{\text{entrada}}}\right) \cdot 100\%$$
- Os melhores descenedores profissionais procuram valores superiores a $75\%$ em curvas rápidas, mantendo o balanço sem travar em excesso.

### 4. Avisos Preditivos de Travagem Antes de Curvas
- Com base no raio de curvatura da curva seguinte e na velocidade atual, calcula a distância de paragem segura:
  $$d_{\text{paragem}} = \frac{v_{\text{atual}}^2 - v_{\text{segura}}^2}{2 \cdot a_{\text{segura}}}$$
- Emite um sinal sonoro e um aviso `InRideAlert` no ecrã do Karoo com a distância necessária para iniciar a desaceleração.

### 5. Círculo de Fricção de Kamm & Alarme de Limite de Aderência
- A aderência total do pneu é fisicamente limitada por:
  $$F_{\text{total}} = \sqrt{F_{\text{travagem}}^2 + F_{\text{curva}}^2} \le \mu \cdot N$$
- Em piso molhado (`WET_ROAD`, $\mu \approx 0.45$), travar com força enquanto a bicicleta está inclinada causa a perda fulminante da roda dianteira (*washout*).
- O Karoo BBrakers monitoriza em tempo real a **Margem de Aderência %** e emite um alarme de emergência acústico e visual se a margem cair para níveis críticos.

### 6. Energia Metabólica Desperdiçada (kJ & kcal)
- Cada travagem destrói energia cinética mecânica ($\Delta E = \frac{1}{2} m (v_1^2 - v_2^2)$).
- Tendo em conta a eficiência muscular bruta humana ($\eta \approx 21.5\%$), recuperar essa velocidade exige queimar quase 5 vezes mais glicogénio metabólico:
  $$E_{\text{metabólica}} = \frac{\Delta E_{\text{cinética}}}{0.215}$$
- Regista os kilojoules e as kilocalorias totais desperdiçados em travagens ao longo de todo o treino.

### 7. Índice de Fluidez em Pelotão (PEI 0–100%)
- Travagens bruscas na frente do pelotão criam ondas de choque em acordeão para a cauda, forçando os ciclistas de trás a sucessivos sprints de 800W.
- Numa janela de 60 segundos, o PEI avalia a fluidez de 0 a 100%, ensinando a manter a distância através da resistência aerodinâmica em vez de toques secos de travão.

### 8. Pontuação de Eficiência em Descida (DES 0–100%)
- A descida em montanha exige travagens curtas e firmes por impulsos antes da curva, soltando as manetes nas retas para refrigerar os discos por convecção.
- Arrastar continuamente o travão provoca a cristalização ou vitrificação térmica das pastilhas.
- O algoritmo DES avalia a tua técnica de 0 a 100%.

### 9. Transferência Dinâmica de Carga (Frente/Trás)
- Ao desacelerar, a inércia transfere o peso para a roda dianteira:
  $$\Delta W = \frac{m \cdot a \cdot h_{\text{CG}}}{L_{\text{distância\_eixos}}}$$
- Em descidas íngremes, até **85% da carga** recai sobre o pneu dianteiro. O Karoo BBrakers calcula a distribuição em tempo real.

### 10. Odómetro Digital de Manutenção de Pastilhas
- Regista a energia total acumulada absorvida pelas pastilhas em kilojoules ($\text{kJ}$):
  - **Orgânicas / Resina**: Capacidade nominal de ~2500 kJ.
  - **Semi-Metálicas**: Capacidade nominal de ~4500 kJ.
  - **Sinterizadas / Metálicas**: Capacidade nominal de ~7000 kJ.
- Apresenta a **Vida Restante das Pastilhas %** com reposição num único toque.

### 11. Termodinâmica Dupla & Alarme de Cristalização
- **Arrefecimento Diferencial**: A pinça dianteira recebe fluxo de ar limpo e laminar, enquanto a traseira trabalha no ar turbulento das escoras.
- **Limites de Segurança**:
  - **Travões de Disco**: Seguro até 350°C.
  - **Travões de Calço (Aro de alumínio)**: Seguro até 150°C.
  - **Travões de Calço (Pista de carbono)**: Alerta prévio aos 145°C e alarme crítico de delaminação aos **180°C**.

### 12. Deteção de Atrito Parasita (Watts Perdidos / Wasted Watts)
- Deteta continuamente êmbolos presos, discos empenados ou pastilhas a roçar enquanto pedalas.
- Calcula em tempo real os **Watts Perdidos** ($P = F_{\text{atrito}} \cdot v$).

---

## Catálogo Completo de Campos de Dados (17 Tipos Nativos)

O Karoo BBrakers regista 17 tipos de dados nativos no Karoo OS (`extension_info.xml`):

| Type ID | Nome no Ecrã (Português) | Formato | Descrição |
|---|---|---|---|
| `current_decel` | **Força de Travagem** | Numérico | Desaceleração instantânea em $\text{m/s}^2$, Força $G$ ou $\text{ft/s}^2$. |
| `braking_power` | **Potência de Travagem** | Numérico | Potência cinética dissipada pelos travões em Watts ($W$). |
| `max_decel` | **Pico de Travagem G** | Numérico | Máxima desaceleração registada durante a volta. |
| `brake_events` | **Eventos de Travagem** | Numérico | Contador total de toques e travagens detetadas. |
| `brake_energy` | **Energia Dissipada** | Numérico | Energia cinética total absorvida pelos travões em kilojoules ($\text{kJ}$). |
| `pad_wear` | **Desgaste Estimado** | Numérico | Índice de desgaste estimado com base na energia e composto. |
| `pad_life` | **Vida Útil de Pastilhas** | Numérico | Percentagem de vida útil restante das pastilhas. |
| `descent_efficiency` | **Eficiência em Descida (DES)** | Numérico | Pontuação WorldTour (0–100%) que premeia travagens firmes por impulsos. |
| `wasted_watts` | **Watts Perdidos (Atrito)** | Numérico | Potência parasita dissipada por atrito involuntário em Watts. |
| `grip_margin` | **Margem Aderência em Curva** | Numérico | Margem de segurança do círculo de Kamm antes do deslize do pneu em %. |
| `brake_temperature`| **Temperatura de Travões** | Numérico | Temperatura estimada em tempo real do disco/pista em °C ou °F. |
| `corner_radar` | **Radar de Curvas & Ápice** | Numérico | Velocidade de entrada, retenção no ápice e aceleração à saída. |
| `metabolic_burn` | **Energia Metabólica Gasta** | Numérico | Kilojoules musculares gastos com base em 21.5% de eficiência humana. |
| `peloton_smoothness`| **Fluidez em Pelotão (PEI)** | Numérico | Eficiência de cone de ar (0-100%) e resistência a ondas em acordeão. |
| `graphical_brake` | **Manómetro de Travagem** | Gráfico | Medidor dinâmico em 5 segmentos (Branco a Amarelo, Laranja e Vermelho). |
| `graphical_thermal`| **Monitor Térmico & Fade** | Gráfico | Temperaturas dianteira/traseira, zonas de calor e limites de segurança. |
| `graphical_cockpit`| **Cockpit HUD de Travagem** | Gráfico | HUD integral de rota com manómetro, potência, calor, inclinação e badge. |

---

## Campos Developer em Ficheiros FIT & Análise (WKO5 & GoldenCheetah)

Durante as voltas gravadas, o Karoo BBrakers injeta 12 campos Developer FIT a 1 Hz:
- `BrakeDecel` ($\text{m/s}^2$)
- `BrakingPower` ($\text{Watts}$)
- `IsBraking` (bandeira $0/1$)
- `BrakeTemperature` ($^\circ\text{C}$)
- `DescentEfficiency` ($\%$)
- `GripMargin` ($\%$)
- `MetabolicBurn` ($\text{kJ}$)
- `PelotonSmoothness` ($\%$)
- `ApexRetention` ($\%$)
- Resumo de sessão: `TotalBrakeEvents`, `MaxBrakeDecel`, `TotalBrakeEnergy` e `PadWearScore`.

Para fórmulas de gráficos no WKO5, scripts Python no GoldenCheetah e modelos para treinadores WorldTour, consulta o [Guia de Integração WKO5](wko5/README.md).

---

## Código de Cores Visual ao Instante
- **Branco** ($< 1.0\text{ m/s}^2$): Contacto inicial / toque suave.
- **Amarelo Claro** ($1.0 - 2.0\text{ m/s}^2$): Ajuste fino de distância em grupo.
- **Amarelo Puro** ($2.0 - 3.2\text{ m/s}^2$): Travagem padrão antes da curva.
- **Laranja** ($3.2 - 4.5\text{ m/s}^2$): Travagem firme em descidas íngremes.
- **Vermelho** ($> 4.5\text{ m/s}^2$): Travagem de emergência / paragem brusca.
- **Violeta**: Modo pelotão ativo (toques suaves de roda traseira).
- **Verde**: Temperatura ideal de funcionamento ($< 80^\circ\text{C}$).

---

## Instalação & Atualizações OTA

### Opção 1: Instalação Direta por Manifest no Karoo
1. Copia este URL do Manifest:
   ```
   https://raw.githubusercontent.com/DAVOE75/KarooBBrakers/main/manifest.json
   ```
2. No teu Karoo, vai a **Definições -> Extensões -> Adicionar Extensão**.
3. Cola o URL. O Karoo OS instalará a extensão e avisará quando houver novas versões.

### Opção 2: Instalação manual por ADB
```bash
adb install -r karoobbrakers.apk
```

---

## Desenvolvedor & Créditos
- **Autor**: hesiOX
- **Dispositivos suportados**: Hammerhead Karoo 2 / Karoo 3 (Android 8.1 - 12+)
- **SDK**: Hammerhead Karoo Extension SDK (`io.hammerhead.karooext`)
- **Licença**: Licença MIT
