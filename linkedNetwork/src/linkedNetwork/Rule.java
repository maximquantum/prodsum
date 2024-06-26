package linkedNetwork;

public class Rule {
    private String sourcePattern;
    private String targetPattern;
    private String label;
    private boolean exactMatch;

    public Rule(String sourcePattern, String targetPattern, String label, boolean exactMatch) {
        this.sourcePattern = sourcePattern;
        this.targetPattern = targetPattern;
        this.label = label;
        this.exactMatch = exactMatch;
    }

    public boolean matches(String source, String target) {
        if (exactMatch) {
            return source.equals(sourcePattern) && target.equals(targetPattern);
        } else {
            return source.startsWith(sourcePattern) && target.startsWith(targetPattern);
        }
    }

    public String getLabel() {
        return label;
    }
}