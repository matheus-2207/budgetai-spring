# Verificação da interface — 16/10/2026

## Uso

Abra `http://127.0.0.1:8080/` com a aplicação em modo local. Os endpoints `/api/expenses` permanecem disponíveis e continuam retornando JSON. A interface nova usa HTML, CSS e JavaScript sem dependências externas.

## Verificado no navegador

- Resumo retornando as duas despesas de demonstração de outubro, total R$ 50,00.
- Busca por Mercado e filtro de categoria Saúde e farmácia.
- Rejeição de valor zero antes da gravação.
- Cadastro real de uma despesa fictícia de R$ 1,00 em 16/09/2026, descrição `DEMONSTRAÇÃO — Teste de cadastro`; o painel passou para setembro e mostrou o registro salvo.
- Volta a outubro preservando as duas despesas originais.
- Navegação para novembro mostrando estado vazio e total zero.
- Rejeição de datas com início posterior ao fim.
- Exportação CSV com descrições, categorias, datas e valores correspondentes à lista.
- Mensagem de recuperação quando a aplicação estava sendo reiniciada.
- Visual em 1440px, 692px e 390px; sem rolagem horizontal em 390px.
- Categoria textual mantida nos registros do celular após revisão independente.
- Console sem erros durante os fluxos normais verificados.

A revisão visual solicitou um ajuste de categoria no celular; o passe de revisão confirmou o ajuste como resolvido. PRODUCT.md, DESIGN.md e .impeccable/design.json documentam o escopo e o sistema visual.

## Limites

A busca considera a lista carregada (até 200 itens). O resumo considera todas as despesas do período, mesmo com filtros de lista. Os rótulos de demonstração distinguem as entradas fictícias; não são dados financeiros reais. Não foram feitas chamadas de IA paga durante esta verificação.
