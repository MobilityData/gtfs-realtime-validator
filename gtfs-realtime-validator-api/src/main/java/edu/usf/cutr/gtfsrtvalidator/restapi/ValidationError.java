package edu.usf.cutr.gtfsrtvalidator.restapi;

import edu.usf.cutr.gtfsrtvalidator.lib.model.OccurrenceModel;
import edu.usf.cutr.gtfsrtvalidator.lib.model.ValidationRule;
import edu.usf.cutr.gtfsrtvalidator.lib.model.helper.ErrorListHelperModel;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationError {

    private final String errorId;
    private final String severity;
    private final String title;
    private final String description;
    private final String suffix;
    private final String errorDetails;
    private final List<String> occurrences;

    public ValidationError(String errorId, String severity, String title, String description,
                           String suffix, String errorDetails, List<String> occurrences) {
        this.errorId = errorId;
        this.severity = severity;
        this.title = title;
        this.description = description;
        this.suffix = suffix;
        this.errorDetails = errorDetails;
        this.occurrences = List.copyOf(occurrences);
    }

    public static ValidationError from(ErrorListHelperModel model) {
        ValidationRule rule = model.getErrorMessage() != null ? model.getErrorMessage().getValidationRule() : null;
        String errorDetails = model.getErrorMessage() != null ? model.getErrorMessage().getErrorDetails() : null;
        List<String> occurrences = model.getOccurrenceList() == null
                ? List.of()
                : model.getOccurrenceList().stream()
                        .map(OccurrenceModel::getPrefix)
                        .collect(Collectors.toUnmodifiableList());
        return new ValidationError(
                rule != null ? rule.getErrorId() : null,
                rule != null ? rule.getSeverity() : null,
                rule != null ? rule.getTitle() : null,
                rule != null ? rule.getErrorDescription() : null,
                rule != null ? rule.getOccurrenceSuffix() : null,
                errorDetails,
                occurrences
        );
    }

    public String getErrorId() { return errorId; }
    public String getSeverity() { return severity; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSuffix() { return suffix; }
    public String getErrorDetails() { return errorDetails; }
    public List<String> getOccurrences() { return occurrences; }
}
