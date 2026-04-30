package edu.usf.cutr.gtfsrtvalidator.restapi;

import com.google.transit.realtime.GtfsRealtime;
import edu.usf.cutr.gtfsrtvalidator.lib.model.helper.ErrorListHelperModel;
import edu.usf.cutr.gtfsrtvalidator.lib.util.GtfsUtils;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.GtfsMetadata;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.interfaces.FeedEntityValidator;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.rules.CrossFeedDescriptorValidator;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.rules.FrequencyTypeOneValidator;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.rules.FrequencyTypeZeroValidator;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.rules.HeaderValidator;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.rules.StopTimeUpdateValidator;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.rules.StopValidator;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.rules.TimestampValidator;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.rules.TripDescriptorValidator;
import edu.usf.cutr.gtfsrtvalidator.lib.validation.rules.VehicleValidator;
import io.javalin.http.HttpStatus;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

public class ValidationService {

    private static final Logger log = LoggerFactory.getLogger(ValidationService.class);

    private static final int CONNECT_TIMEOUT_MS = 30_000;
    private static final int READ_TIMEOUT_MS = 60_000;

    private final List<FeedEntityValidator> validators;

    public ValidationService() {
        this.validators = List.of(
                new CrossFeedDescriptorValidator(),
                new VehicleValidator(),
                new TimestampValidator(),
                new StopTimeUpdateValidator(),
                new TripDescriptorValidator(),
                new StopValidator(),
                new FrequencyTypeZeroValidator(),
                new FrequencyTypeOneValidator(),
                new HeaderValidator()
        );
    }

    public ValidationResponse validate(String gtfsUrl, String gtfsRtUrl) throws ValidationException {
        requireHttpUrl(gtfsUrl, "gtfsUrl");
        requireHttpUrl(gtfsRtUrl, "gtfsRtUrl");

        Path gtfsTempFile = null;
        try {
            gtfsTempFile = downloadToTempFile(gtfsUrl);

            GtfsDaoImpl gtfsData = new GtfsDaoImpl();
            GtfsReader reader = new GtfsReader();
            try {
                reader.setInputLocation(gtfsTempFile.toFile());
                reader.setEntityStore(gtfsData);
                reader.run();
            } catch (IOException | RuntimeException e) {
                throw new ValidationException(HttpStatus.UNPROCESSABLE_CONTENT, "Failed to parse GTFS zip: " + e.getMessage(), e);
            }

            String agencyTimezone = firstAgencyTimezone(gtfsData.getAllAgencies());
            if (agencyTimezone == null) {
                throw new ValidationException(HttpStatus.UNPROCESSABLE_CONTENT, "GTFS feed has no agencies with a timezone");
            }

            GtfsMetadata gtfsMetadata = new GtfsMetadata(
                    gtfsTempFile.toAbsolutePath().toString(),
                    TimeZone.getTimeZone(agencyTimezone),
                    gtfsData,
                    false
            );

            byte[] gtfsRtBytes = downloadBytes(gtfsRtUrl);
            GtfsRealtime.FeedMessage feedMessage;
            try {
                feedMessage = GtfsRealtime.FeedMessage.parseFrom(gtfsRtBytes);
            } catch (IOException e) {
                throw new ValidationException(HttpStatus.UNPROCESSABLE_CONTENT, "Failed to parse GTFS-realtime protobuf: " + e.getMessage(), e);
            }

            GtfsRealtime.FeedMessage combinedFeed = GtfsUtils.isCombinedFeed(feedMessage) ? feedMessage : null;

            long currentTimeMillis = System.currentTimeMillis();
            long feedTimestampSeconds = feedMessage.hasHeader() ? feedMessage.getHeader().getTimestamp() : 0L;

            List<ValidationError> results = new ArrayList<>();
            List<SkippedRule> skippedRules = new ArrayList<>();
            for (FeedEntityValidator rule : validators) {
                String name = rule.getClass().getSimpleName();
                try {
                    List<ErrorListHelperModel> ruleResults = rule.validate(
                            currentTimeMillis, gtfsData, gtfsMetadata, feedMessage, null, combinedFeed);
                    if (ruleResults != null) {
                        ruleResults.stream().map(ValidationError::from).forEach(results::add);
                    }
                } catch (Exception e) {
                    skippedRules.add(new SkippedRule(name, e.getClass().getName(), e.getMessage()));
                    log.error("Rule {} threw {}", name, e.getClass().getSimpleName(), e);
                }
            }

            return new ValidationResponse(gtfsUrl, gtfsRtUrl, currentTimeMillis, feedTimestampSeconds, results, skippedRules);
        } finally {
            if (gtfsTempFile != null) {
                try {
                    Files.deleteIfExists(gtfsTempFile);
                } catch (IOException e) {
                    log.debug("Could not delete temp file {}: {}", gtfsTempFile, e.getMessage());
                }
            }
        }
    }

    private static void requireHttpUrl(String value, String field) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, field + " is required");
        }
        URI uri;
        try {
            uri = new URI(value).parseServerAuthority();
        } catch (URISyntaxException | IllegalArgumentException e) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, field + " is not a valid URL");
        }
        String scheme = uri.getScheme();
        if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, field + " must be an http or https URL");
        }
        if (uri.getHost() == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, field + " must include a host");
        }
    }

    private static Path downloadToTempFile(String url) throws ValidationException {
        Path file;
        try {
            file = Files.createTempFile("gtfs-", ".zip");
        } catch (IOException e) {
            throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create temp file: " + e.getMessage(), e);
        }
        // Best-effort cleanup if the JVM exits before the finally block runs.
        file.toFile().deleteOnExit();
        try {
            withConnection(url, in -> {
                Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
                return null;
            });
            return file;
        } catch (ValidationException e) {
            try { Files.deleteIfExists(file); } catch (IOException ignored) { /* best effort */ }
            throw e;
        }
    }

    private static byte[] downloadBytes(String url) throws ValidationException {
        return withConnection(url, InputStream::readAllBytes);
    }

    @FunctionalInterface
    private interface IOFunction<T, R> {
        R apply(T input) throws IOException;
    }

    private static <T> T withConnection(String url, IOFunction<InputStream, T> consumer) throws ValidationException {
        HttpURLConnection conn = openConnection(url);
        try (InputStream in = conn.getInputStream()) {
            return consumer.apply(in);
        } catch (IOException e) {
            throw new ValidationException(HttpStatus.BAD_GATEWAY, "Failed to download " + url + ": " + e.getMessage(), e);
        } finally {
            conn.disconnect();
        }
    }

    private static HttpURLConnection openConnection(String url) throws ValidationException {
        try {
            URL parsed = new URI(url).toURL();
            HttpURLConnection conn = (HttpURLConnection) parsed.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("User-Agent", "gtfs-realtime-validator-api");
            return conn;
        } catch (IOException | URISyntaxException | IllegalArgumentException e) {
            throw new ValidationException(HttpStatus.BAD_GATEWAY, "Could not open connection to " + url + ": " + e.getMessage(), e);
        }
    }

    private static String firstAgencyTimezone(Collection<Agency> agencies) {
        for (Agency a : agencies) {
            if (a.getTimezone() != null && !a.getTimezone().isBlank()) {
                return a.getTimezone();
            }
        }
        return null;
    }
}
