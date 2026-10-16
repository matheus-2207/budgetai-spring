package br.com.matheus.budgetai.infrastructure.http;

import br.com.matheus.budgetai.infrastructure.ai.AssistantService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Profile("ai")
@RequestMapping("/api/assistant")
public class AssistantController {
    private final AssistantService assistant;
    public AssistantController(AssistantService assistant) { this.assistant = assistant; }
    public record Message(String message) { }
    @PostMapping("/text")
    public AssistantService.Reply text(@RequestBody Message input) { return assistant.text(input.message()); }
    @PostMapping(value = "/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AssistantService.Reply audio(@RequestParam("file") MultipartFile file) { return assistant.audio(file); }
    @PostMapping(value = "/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mpeg")
    public ResponseEntity<byte[]> voice(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=budgetai.mp3")
                .header("X-Audio-Source", "AI-generated")
                .contentType(MediaType.parseMediaType("audio/mpeg")).body(assistant.voice(file));
    }
}
