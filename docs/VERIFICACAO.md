# Verificação local — 16/10/2026

## Resultado

- Compilação e geração do JAR: `BUILD SUCCESSFUL`.
- 19 testes executados, 0 falhas, 0 erros e 0 ignorados.
- API iniciada pelos testes em porta aleatória e banco H2 em memória.
- Nenhuma chave real de API utilizada e nenhuma chamada paga realizada.

| Classe | Testes | Falhas |
| --- | --- | --- |
| `AssistantServiceTest` | 4 | 0 |
| `BudgetToolsTest` | 3 | 0 |
| `ExpenseApiTest` | 6 | 0 |
| `ExpenseTest` | 6 | 0 |

## Comandos

```bash
./gradlew test bootJar
```

Nesta execução, o cache do Gradle foi direcionado para uma pasta temporária do ambiente. Isso não muda o comando de uso do projeto.

## O que foi verificado

- Registro REST e persistência JPA.
- Filtros por categoria e datas, incluindo os limites inicial e final.
- Resumo com soma decimal exata, totais por categoria e resultado vazio.
- Rejeição de valor zero, negativo, com casas decimais extras, categoria ausente, datas futuras e períodos inválidos.
- Rejeição antes da gravação também quando a entrada vem de uma ferramenta de IA.
- Geração dos schemas das três ferramentas pelo Spring AI.
- Orquestração de transcrição → chat → voz, tratamento de falha e rejeição de áudio inválido com mocks.
- Inicialização e health check sem chave de API.

## O que não foi verificado

A transcrição, a escolha de ferramenta pelo modelo e a síntese com o provedor real não foram executadas. Exigem chave e acesso aos modelos. Os mocks verificam o encadeamento interno, sem garantir a qualidade ou disponibilidade do serviço externo.
