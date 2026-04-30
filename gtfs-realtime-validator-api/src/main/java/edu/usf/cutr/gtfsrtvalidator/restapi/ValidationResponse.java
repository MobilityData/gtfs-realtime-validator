package edu.usf.cutr.gtfsrtvalidator.restapi;

import java.util.List;

public class ValidationResponse {

    private final String gtfsUrl;
    private final String gtfsRtUrl;
    private final long currentTimeMillis;
    private final long feedTimestampSeconds;
    private final List<ValidationError> results;
    private final List<SkippedRule> skippedRules;

    public ValidationResponse(String gtfsUrl, String gtfsRtUrl, long currentTimeMillis,
                              long feedTimestampSeconds, List<ValidationError> results,
                              List<SkippedRule> skippedRules) {
        this.gtfsUrl = gtfsUrl;
        this.gtfsRtUrl = gtfsRtUrl;
        this.currentTimeMillis = currentTimeMillis;
        this.feedTimestampSeconds = feedTimestampSeconds;
        this.results = List.copyOf(results);
        this.skippedRules = List.copyOf(skippedRules);
    }

    public String getGtfsUrl() { return gtfsUrl; }
    public String getGtfsRtUrl() { return gtfsRtUrl; }
    public long getCurrentTimeMillis() { return currentTimeMillis; }
    public long getFeedTimestampSeconds() { return feedTimestampSeconds; }
    public List<ValidationError> getResults() { return results; }
    public List<SkippedRule> getSkippedRules() { return skippedRules; }
}
