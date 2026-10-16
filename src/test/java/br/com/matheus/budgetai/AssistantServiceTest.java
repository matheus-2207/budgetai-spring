package br.com.matheus.budgetai;

import br.com.matheus.budgetai.infrastructure.ai.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import java.nio.charset.StandardCharsets;
import java.time.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AssistantServiceTest {
    private final ChatClient chat = mock(ChatClient.class, RETURNS_DEEP_STUBS);
    private final TranscriptionModel transcription = mock(TranscriptionModel.class);
    private final TextToSpeechModel speech = mock(TextToSpeechModel.class);
    private AssistantService assistant;
    @BeforeEach void setup() throws Exception {
        var builder = mock(ChatClient.Builder.class);
        when(builder.defaultTools(any(Object[].class))).thenReturn(builder);
        when(builder.build()).thenReturn(chat);
        assistant = new AssistantService(builder, transcription, speech, mock(BudgetTools.class),
            Clock.fixed(Instant.parse("2026-01-15T12:00:00Z"), ZoneOffset.UTC),
            new ByteArrayResource("Assistente financeiro".getBytes(StandardCharsets.UTF_8)));
    }
    @Test void audioIsTranscribedThenAnsweredThenSpoken() {
        var file = new MockMultipartFile("file", "comando.wav", "audio/wav", new byte[]{1, 2, 3});
        when(transcription.transcribe(any(Resource.class))).thenReturn("Quanto gastei este mês?");
        when(chat.prompt().system(anyString()).user("Quanto gastei este mês?").call().content()).thenReturn("Você gastou 50 reais.");
        when(speech.call("Você gastou 50 reais.")).thenReturn(new byte[]{4, 5});
        assertThat(assistant.voice(file)).containsExactly((byte) 4, (byte) 5);
        verify(transcription).transcribe(any(Resource.class));
        verify(speech).call("Você gastou 50 reais.");
    }
    @Test void invalidAudioNeverReachesProvider() {
        var file = new MockMultipartFile("file", "arquivo.txt", "text/plain", new byte[]{1});
        assertThatThrownBy(() -> assistant.audio(file)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(transcription, speech);
    }
    @Test void missingMessageIsRejected() {
        assertThatThrownBy(() -> assistant.text(" ")).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void providerFailureIsWrappedWithoutExposingItsMessage() {
        var file = new MockMultipartFile("file", "comando.wav", "audio/wav", new byte[]{1});
        when(transcription.transcribe(any(Resource.class))).thenThrow(new IllegalStateException("detalhe interno"));
        assertThatThrownBy(() -> assistant.audio(file)).isInstanceOf(AiProviderException.class)
                .hasMessage("Falha na integração de IA");
    }
}
