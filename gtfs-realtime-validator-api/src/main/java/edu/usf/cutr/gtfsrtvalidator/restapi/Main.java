package edu.usf.cutr.gtfsrtvalidator.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        int port = resolvePort();
        List<String> corsOrigins = resolveCorsOrigins();
        ValidationService service = new ValidationService();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(mapper));
            config.plugins.enableCors(cors -> cors.add(it -> {
                if (corsOrigins.isEmpty()) {
                    it.anyHost();
                } else {
                    it.allowHost(corsOrigins.get(0), corsOrigins.subList(1, corsOrigins.size()).toArray(new String[0]));
                }
            }));
        });

        app.get("/health", ctx -> ctx.json(Map.of("status", "ok")));

        app.post("/api/validate", ctx -> {
            ValidateRequest req = ctx.bodyAsClass(ValidateRequest.class);
            try {
                ValidationResponse response = service.validate(req.gtfsUrl, req.gtfsRtUrl);
                ctx.json(response);
            } catch (ValidationException e) {
                ctx.status(e.getStatus()).json(Map.of("error", e.getMessage()));
            } catch (Exception e) {
                log.error("Unexpected error during validation", e);
                ctx.status(500).json(Map.of("error", "Internal server error"));
            }
        });

        app.start(port);
        log.info("gtfs-realtime-validator-api listening on http://localhost:{}", port);
        if (corsOrigins.isEmpty()) {
            log.info("CORS: any host (set CORS_ALLOWED_ORIGINS to restrict)");
        } else {
            log.info("CORS: allowed origins = {}", corsOrigins);
        }
    }

    private static int resolvePort() {
        String env = System.getenv("PORT");
        if (env != null && !env.isBlank()) {
            try {
                return Integer.parseInt(env.trim());
            } catch (NumberFormatException e) {
                log.warn("Invalid PORT env value '{}', falling back to default", env);
            }
        }
        return 8090;
    }

    private static List<String> resolveCorsOrigins() {
        String env = System.getenv("CORS_ALLOWED_ORIGINS");
        if (env == null || env.isBlank()) {
            return List.of();
        }
        return Arrays.stream(env.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public static class ValidateRequest {
        public String gtfsUrl;
        public String gtfsRtUrl;
    }
}
