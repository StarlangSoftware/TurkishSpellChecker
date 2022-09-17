package SpellChecker;

import Dictionary.Word;

public class Candidate extends Word {
    private Operator operator;

    public Candidate(String candidate, Operator operator) {
        super(candidate);
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }
}
