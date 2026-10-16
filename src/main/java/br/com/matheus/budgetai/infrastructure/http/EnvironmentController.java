package br.com.matheus.budgetai.infrastructure.http;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class EnvironmentController {
    private final Environment environment;
    public EnvironmentController(Environment environment) { this.environment = environment; }

    @GetMapping("/api/environment")
    public Map<String, Boolean> configuration() {
        return Map.of("publicDemo", environment.acceptsProfiles(Profiles.of("demo")));
    }
}
