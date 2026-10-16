# Demonstração pública no Render

O `render.yaml` configura um serviço Docker no plano **Free**. O mesmo servidor Java serve a interface e a API. Não há chave de OpenAI nem serviços pagos configurados.

[Abrir configuração de publicação no Render](https://render.com/deploy?repo=https://github.com/matheus-2207/budgetai-spring)

## Publicar

1. Entre em https://dashboard.render.com/.
2. Selecione **New → Blueprint** e informe o repositório `https://github.com/matheus-2207/budgetai-spring`.
3. Confira que o serviço `budgetai-spring` está no plano **Free** e publique.
4. Aguarde a construção e abra o endereço HTTPS gerado pelo Render.
5. Confira `/actuator/health` e experimente o painel com dados fictícios.

## Dados e limites

O perfil `demo` usa H2 em memória e não lê a pasta `data` local. Os registros públicos são compartilhados entre visitantes e desaparecem quando o processo reinicia. Use somente informações fictícias: esta versão não tem autenticação e não deve armazenar finanças pessoais em público.

O plano gratuito suspende a aplicação após 15 minutos sem visitas. O próximo acesso pode levar cerca de um minuto para iniciar. Não configure cartão, upgrades ou bancos pagos para esta demonstração. A publicação só estará concluída quando houver uma URL pública verificada; estes arquivos, isoladamente, não significam que o site já está online.

Referência: https://render.com/docs/free
