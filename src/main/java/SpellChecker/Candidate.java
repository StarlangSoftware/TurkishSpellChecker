package SpellChecker;

import Dictionary.Word;

public class Candidate extends Word {
    private final Operator operator;

    /**
     * Constructs a new Candidate object with the specified candidate and operator.
     *
     * @param candidate The word candidate to be checked for spelling.
     * @param operator The operator to be applied to the candidate in the spell checking process.
     */
    public Candidate(String candidate, Operator operator) {
        super(candidate);
        this.operator = operator;
    }

    /**
     * Returns the operator associated with this candidate.
     *
     * @return The operator associated with this candidate.
     */
    public Operator getOperator() {
        return operator;
    }
}
