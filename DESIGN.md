---
name: "BudgetAI"
description: "Caderno digital para ler e registrar despesas pessoais."
colors:
  accent: "#6e51d9"
  accent-dark: "#5738bc"
  accent-soft: "#efebfb"
  accent-ink: "#6244bd"
  bg: "#f6f7fb"
  surface: "#ffffff"
  sidebar: "#fcfcfe"
  ink: "#202331"
  muted: "#696d7f"
  line: "#e9eaf1"
  danger: "#ad3045"
  success: "#327463"
  groceries-color: "#8a72df"
  groceries-bg: "#f0ebfb"
  groceries-ink: "#654eab"
  pharma-color: "#76ad9e"
  pharma-bg: "#eaf4ef"
  pharma-ink: "#3c7766"
  auto-color: "#719bc9"
  auto-bg: "#edf3fa"
  auto-ink: "#476f99"
  food-color: "#d6a55e"
  food-bg: "#fbf1e4"
  food-ink: "#916326"
  leisure-color: "#ca87a3"
  leisure-bg: "#f8edf2"
  leisure-ink: "#a05a77"
  other-color: "#9697ab"
  other-bg: "#f0f0f5"
  other-ink: "#64667c"
typography:
  display:
    fontFamily: "-apple-system, BlinkMacSystemFont, \"Segoe UI\", sans-serif"
    fontSize: "50px"
    fontWeight: 550
    lineHeight: 1.3
    letterSpacing: "-0.035em"
  headline:
    fontFamily: "-apple-system, BlinkMacSystemFont, \"Segoe UI\", sans-serif"
    fontSize: "30px"
    fontWeight: 650
    lineHeight: 1.25
    letterSpacing: "-0.035em"
  title:
    fontFamily: "-apple-system, BlinkMacSystemFont, \"Segoe UI\", sans-serif"
    fontSize: "17px"
    fontWeight: 650
    letterSpacing: "-0.02em"
  body:
    fontFamily: "-apple-system, BlinkMacSystemFont, \"Segoe UI\", sans-serif"
    fontSize: "14px"
    lineHeight: 1.5
  label:
    fontFamily: "-apple-system, BlinkMacSystemFont, \"Segoe UI\", sans-serif"
    fontSize: "13px"
    fontWeight: 550
rounded:
  surface: "16px"
  popover: "12px"
  icon-tile: "10px"
  navigation: "9px"
  button: "8px"
  field: "7px"
  chip: "5px"
  small: "4px"
spacing:
  xs: "4px"
  sm: "8px"
  control-gap: "9px"
  md: "12px"
  lg: "16px"
  xl: "20px"
  panel: "24px"
  section: "28px"
  wide: "32px"
components:
  button-primary:
    backgroundColor: "{colors.accent}"
    textColor: "{colors.surface}"
    rounded: "{rounded.button}"
    padding: "10px 17px"
  button-primary-hover:
    backgroundColor: "{colors.accent-dark}"
  button-secondary:
    backgroundColor: "{colors.surface}"
    textColor: "#505468"
    rounded: "{rounded.button}"
    padding: "10px 17px"
  button-icon:
    textColor: "#74778b"
    rounded: "{rounded.field}"
    size: "34px"
  button-text:
    textColor: "{colors.accent-ink}"
    padding: "7px 0"
  input:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.ink}"
    rounded: "{rounded.field}"
    height: "41px"
    padding: "0 11px"
  nav-active:
    backgroundColor: "{colors.accent-soft}"
    textColor: "{colors.accent-ink}"
    rounded: "{rounded.navigation}"
    padding: "12px 14px"
  category-chip:
    rounded: "{rounded.chip}"
    padding: "4px 8px"
  card:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.surface}"
---

# Design System: BudgetAI

## Overview

**Creative North Star: "Caderno digital"**

O Caderno digital usa uma base cinza azulada, folhas brancas e violeta para tornar a leitura de despesas calma e precisa. A densidade vem de linhas de registros, divisórias finas e controles compactos; o valor total recebe a maior escala tipográfica.

A composição é plana e operacional. Uma única família sans de sistema sustenta títulos, conteúdo e controles; números tabulares mantêm valores alinhados. Ícones SVG lineares, sem imagens raster, dão orientação sem competir com os dados.

**Key Characteristics:**
- Superfícies claras com divisórias discretas.
- Violeta para ações, seleção e foco.
- Uma família sans e valores tabulares.
- Registros em linhas, com categoria legível também no celular.

## Colors

O violeta de ação acompanha neutros frios e um conjunto pastel estável para categorias; os valores normativos estão no frontmatter.

### Primary
- **Violeta de ação** (`accent`): ação principal, marca e detalhes interativos; `accent-dark` é o hover da ação principal.
- **Lavanda de seleção** (`accent-soft`): fundo da navegação ativa e hover de botões de ícone. `accent-ink` é a tinta violeta legível de links e seleção.

### Secondary
- **Cores de categoria**: Mercado violeta, Saúde e farmácia verde, Transporte azul, Alimentação ocre, Lazer rosa e Outros cinza. Cada categoria possui `color` para barra e marcador, `bg` para ícone/chip e `ink` para o conteúdo desse fundo. A categoria permanece nomeada em texto.
- **Vinho de erro** (`danger`): erro textual e estado de conexão indisponível. `success` existe como propriedade de cor no CSS, mas a confirmação implementada usa ícone verde claro sobre fundo escuro.

### Neutral
- **Papel frio** (`bg`), **folha branca** (`surface`) e **margem clara** (`sidebar`): página, painéis e navegação.
- **Tinta azul escura** (`ink`), **grafite suave** (`muted`) e **linha de caderno** (`line`): hierarquia textual e separadores.

**The Action Violet Rule.** Reserve o violeta de interface para ações, navegação selecionada e foco; as cores de categoria mantêm seu significado de classificação.

## Typography

**Display Font:** a mesma família sans de sistema usada no corpo; não há família de display independente.
**Body Font:** `-apple-system`, `BlinkMacSystemFont`, `Segoe UI`, `sans-serif`.

**Character:** textos diretos e compactos, com títulos levemente condensados pelo espaçamento entre letras. Os pesos intermediários dependem das opções disponíveis na fonte do sistema.

### Hierarchy
- **Display:** total (50px, 550, 1.3); símbolo monetário separado (28px, 450). O total responde a 44px em até 1200px, 40px em até 940px e 46px em até 700px. Totais a partir de um milhão usam 32px, ou 30px no celular.
- **Headline:** título da página (30px, 650, 1.25); celular (25px).
- **Title:** títulos gerais (17px, 650), lista (16px), categorias (15px) e editor (18px). O contexto determina a escala, sem caixa alta decorativa.
- **Body:** conteúdo e registros (14px, 1.5); descrições da lista em 13px no celular. Estados vazios limitam o texto a 42ch.
- **Label:** controles e legendas (12–13px); botões (14px, 600, 1.4).

**The Aligned Numbers Rule.** Use números tabulares em valores, contagens e datas; alinhe os valores da lista à direita.

## Layout

O desktop usa navegação fixa (208px), área principal com margem equivalente e limite de largura (1600px). O recuo principal é 44px no topo, 48px nas laterais e 24px embaixo; acima de 1600px, as laterais passam a 64px. Resumo em duas colunas iguais, com linha divisória central; lista abaixo com intervalo de 36px. Os painéis têm aproximadamente 24–34px de respiro interno.

A lista desktop usa quatro colunas: descrição flexível, categoria flexível, data e valor. Linhas têm altura mínima de 78px, divisórias e valores à direita. O formulário usa quatro colunas na proporção 1.5fr / .8fr / 1fr / .9fr.

- **Até 1200px:** navegação de 188px, laterais de 30px, painéis de 27px, formulário em duas colunas e legenda extensa do período oculta.
- **Até 940px:** navegação de 72px com ícones e nomes acessíveis, laterais de 25px, detalhes do resumo empilhados e legenda de categorias em uma coluna.
- **Até 700px:** navegação vira faixa superior; conteúdo com laterais de 20px; resumo em uma coluna com divisória horizontal. Busca e filtro podem quebrar de linha. Cabeçalho da tabela e chip desktop ficam ocultos; registros usam descrição à esquerda, valor acima da data à direita, e categoria textual sob a descrição. Datas usam forma compacta sem “de”. Formulário em duas colunas, descrição ocupando a linha inteira. Botões e campos têm altura mínima de 44px; categoria interativa também tem área mínima de 44px.

## Elevation & Depth

Painéis são planos, com borda fina e mudança de tom entre fundo e superfície. O formulário aberto tem borda lavanda para reforçar sua função. As únicas sombras são funcionais e suaves.

### Shadow Vocabulary
- **Período aberto:** `0 12px 32px #29234012`.
- **Confirmação flutuante:** `0 8px 30px #28203620`.

**The Quiet Surface Rule.** Mantenha painéis de dados sem sombra; use bordas e fundo para separar conteúdo. Sombras pertencem apenas ao seletor de período aberto e à confirmação flutuante.

## Shapes

Painéis usam curva ampla (16px), seletor de período (12px), ícones de categoria (10px), navegação (9px), botões (8px), campos (7px) e chips/contagens (5px). Bordas de 1px e curvas pequenas mantêm as linhas de dados claras. Avatar e ponto de conexão são circulares. A barra de categorias é um conjunto horizontal de segmentos separados por 4px.

## Components

### Buttons

Ação compacta com rótulo explícito. O botão principal usa violeta e branco, padding de 10px 17px e altura mínima de 42px; hover escurece e pressionado usa `#4e2cac`. Secundário usa fundo branco e borda `#dedfe8`, com hover claro. Botão de ícone tem 34px e curva de 7px; hover lavanda, pressionado `#ded7f5`. Ação textual usa tinta violeta e sublinhado no hover. Controles desabilitados têm opacidade .5 e cursor de indisponibilidade.

Foco compartilhado: contorno de 3px em `#9176e8`, afastado 3px. Transições de cor, fundo e borda duram .18s com `ease-out`.

### Chips

Categoria possui fundo e tinta semânticos, fonte de 12px, padding 4px 8px e curva de 5px. É identificação, sem estado selecionável. A legenda interativa do resumo usa botões com `aria-pressed`; hover e seleção sublinham o nome em violeta. Contagem usa caixa lavanda neutra e números tabulares.

### Cards / Containers

Resumo e lista usam branco, borda `line`, curva de 16px e nenhum sombreado. O resumo mantém o total e a distribuição como regiões abertas da mesma superfície. Editor é um painel em fluxo, com fechamento explícito e foco inicial na descrição; ao fechar devolve o foco ao botão Nova despesa.

### Inputs / Fields

Campos brancos com borda fina e curva de 7px, altura de 41px, padding lateral de 11px. Hover altera a borda para `#b6abc8`; foco usa o contorno compartilhado. Busca tem superfície quase branca, ícone de 16px e campo sem borda interna. Valor tem prefixo R$ separado. Erros são texto vinho junto do formulário; o erro de consulta usa faixa rosa clara com ação de tentar novamente. Ao salvar, o botão mostra “Salvando…” e fechamento/cancelamento ficam desabilitados.

### Navigation

Links com ícones de 20px, espaço de 12px, padding 12px 14px e texto de 550. Inativo usa texto suave; hover recebe fundo claro e tinta escura; ativo recebe lavanda e tinta violeta, com `aria-current`. No tablet os nomes ficam visualmente ocultos; no celular a marca e os ícones dividem uma faixa horizontal. Rótulos acessíveis permanecem.

### Ícones e marca

SVG autoral de traço, viewBox 24×24, sem preenchimento, stroke de 1.7 e pontas/junções arredondadas. Tamanhos gerais de 20px e pequenos de 16px, ajustados nos contextos compactos. A marca usa três barras com stroke 3 em bloco violeta arredondado. Os SVG da aplicação reutilizam símbolos internos; snippets do sidecar incorporam os caminhos para funcionar isoladamente. Nenhum raster é enviado.

### Carregamento, ausência, erro e confirmação

Skeletons usam lavanda cinza e pulso de 1.5s; consultas subsequentes reduzem a opacidade do resumo para .65. Estados vazios explicam a próxima ação e distinguem ausência de registros de filtros sem resultados. Falha inicial substitui os skeletons por mensagem; falha após uma consulta mantém os dados anteriores com aviso textual. Toast escuro, ícone verde claro e anúncio `polite` confirmam o cadastro por 6s. O registro salvo recebe realce lavanda de 2.6s. Barra de categoria interpola `flex-grow` em .24s. `prefers-reduced-motion` remove animações, transições e rolagem suave.

## Do's and Don'ts

### Do:
- **Do** usar os tokens de categoria em conjunto: segmento, fundo e tinta da mesma categoria.
- **Do** manter rótulos, valores e confirmação em português brasileiro.
- **Do** manter a categoria sob a descrição quando o chip desktop estiver oculto.
- **Do** usar SVG linear com pontas e junções arredondadas e foco visível em controles.

### Don't:
- **Don't** introduzir cartões aninhados para cada lançamento.
- **Don't** usar cor como única identificação de categoria ou erro.
- **Don't** transformar a família de sistema em uma fonte de exibição decorativa.
- **Don't** adicionar imagens raster ou ícones de glifos a este sistema sem uma necessidade de conteúdo.
