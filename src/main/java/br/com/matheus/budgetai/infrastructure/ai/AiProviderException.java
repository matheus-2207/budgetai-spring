package br.com.matheus.budgetai.infrastructure.ai;
public class AiProviderException extends RuntimeException {
    public AiProviderException(RuntimeException cause) { super("Falha na integração de IA", cause); }
}
