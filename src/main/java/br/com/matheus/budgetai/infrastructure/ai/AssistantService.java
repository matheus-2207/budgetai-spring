package br.com.matheus.budgetai.infrastructure.ai;

import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

@Service
@Profile("ai")
public class AssistantService {
    private final ChatClient chat;
    private final TranscriptionModel transcription;
    private final TextToSpeechModel speech;
    private final Clock clock;
    private final String systemPrompt;
    public AssistantService(ChatClient.Builder builder, TranscriptionModel transcription,
            TextToSpeechModel speech, BudgetTools tools, Clock clock,
            @Value("classpath:prompts/system.txt") Resource prompt) throws IOException {
        this.chat = builder.defaultTools(tools).build();
        this.transcription = transcription;
        this.speech = speech;
        this.clock = clock;
        this.systemPrompt = prompt.getContentAsString(StandardCharsets.UTF_8);
    }
    public record Reply(String transcript, String answer) { }
    public Reply text(String message) {
        if (message == null || message.isBlank() || message.length() > 4000)
            throw new IllegalArgumentException("Mensagem deve ter de 1 a 4000 caracteres.");
        try {
            String answer = chat.prompt().system(systemPrompt + "\nData atual: " + LocalDate.now(clock))
                    .user(message).call().content();
            if (answer == null || answer.isBlank()) throw new IllegalStateException("Resposta vazia.");
            return new Reply(message, answer);
        } catch (RuntimeException ex) { throw new AiProviderException(ex); }
    }
    public Reply audio(MultipartFile file) {
        validateAudio(file);
        final String text;
        try { text = transcription.transcribe(file.getResource()); }
        catch (RuntimeException ex) { throw new AiProviderException(ex); }
        return text(text);
    }
    public byte[] voice(MultipartFile file) {
        var reply = audio(file);
        try { return speech.call(reply.answer()); }
        catch (RuntimeException ex) { throw new AiProviderException(ex); }
    }
    private void validateAudio(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > 10 * 1024 * 1024)
            throw new IllegalArgumentException("Envie áudio não vazio com no máximo 10 MB.");
        String name = file.getOriginalFilename();
        int dot = name == null ? -1 : name.lastIndexOf('.');
        String extension = dot < 0 ? "" : name.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (!Set.of("mp3", "mp4", "mpeg", "mpga", "m4a", "wav", "webm").contains(extension))
            throw new IllegalArgumentException("Formato aceito: mp3, mp4, mpeg, mpga, m4a, wav ou webm.");
    }
}
