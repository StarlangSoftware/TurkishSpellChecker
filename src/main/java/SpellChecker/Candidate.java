package SpellChecker;

import java.util.Objects;

public class Candidate {
    private String candidate;
    private Operator operator;

    public Candidate(String candidate, Operator operator) {
        this.candidate = candidate;
        this.operator = operator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate1 = (Candidate) o;
        return candidate.equals(candidate1.candidate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(candidate);
    }

    public String getCandidate() {
        return candidate;
    }

    public Operator getOperator() {
        return operator;
    }
}
