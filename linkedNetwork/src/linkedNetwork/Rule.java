package linkedNetwork;

public class Rule {
    private String sourcePattern;
    private String targetPattern;
    private String label;

    public Rule(String sourcePattern, String targetPattern, String label) {
        this.sourcePattern = sourcePattern;
        this.targetPattern = targetPattern;
        this.label = label;
    }

    public boolean matches(String source, String target) {
        return source.startsWith(sourcePattern) && target.startsWith(targetPattern);
    }

    public String getLabel() {
        return label;
    }
}