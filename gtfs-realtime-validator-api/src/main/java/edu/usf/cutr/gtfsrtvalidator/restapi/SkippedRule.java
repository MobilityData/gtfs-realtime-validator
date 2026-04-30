package edu.usf.cutr.gtfsrtvalidator.restapi;

public class SkippedRule {

    private final String name;
    private final String exceptionClass;
    private final String message;

    public SkippedRule(String name, String exceptionClass, String message) {
        this.name = name;
        this.exceptionClass = exceptionClass;
        this.message = message;
    }

    public String getName() { return name; }
    public String getExceptionClass() { return exceptionClass; }
    public String getMessage() { return message; }
}
