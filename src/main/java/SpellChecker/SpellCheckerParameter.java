package SpellChecker;

import Corpus.Sentence;
import Dictionary.Word;

public class SpellCheckerParameter {
    private double threshold = 0.0;
    private boolean suffixCheck = true;
    private boolean rootNGram = true;
    private int minWordLength = 4;
    private String domain = null;

    /**
     * Constructs a SpellCheckerParameter object with default values.
     * The default threshold is 0.0, the suffix check is enabled, the root ngram is enabled,
     * the minimum word length is 4 and domain name value is null.
     */
    public SpellCheckerParameter() {
    }

    /**
     * Sets the threshold value used in calculating the n-gram probabilities.
     *
     * @param threshold the threshold for the spell checker
     */
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    /**
     * Enables or disables suffix check for the spell checker.
     * @see SimpleSpellChecker#forcedDeDaSplitCheck(Word, Sentence)
     * @see SimpleSpellChecker#forcedQuestionSuffixSplitCheck(Word, Sentence) (Word, Sentence)
     * @see SimpleSpellChecker#forcedSuffixSplitCheck(Word, Sentence)
     *
     * @param suffixCheck a boolean indicating whether the suffix check should be enabled (true) or disabled (false)
     */
    public void setSuffixCheck(boolean suffixCheck) {
        this.suffixCheck = suffixCheck;
    }

    /**
     * Enables or disables the root n-gram for the spell checker.
     *
     * @param rootNGram a boolean indicating whether the root n-gram should be enabled (true) or disabled (false)
     */
    public void setRootNGram(boolean rootNGram) {
        this.rootNGram = rootNGram;
    }

    /**
     * Sets the minimum length of words viable for spell checking.
     *
     * @param minWordLength the minimum word length for the spell checker
     */
    public void setMinWordLength(int minWordLength) {
        this.minWordLength = minWordLength;
    }

    /**
     * Sets the domain name to the specified value.
     *
     * @param domain the new domain name to set for this object
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Returns the threshold value used in calculating the n-gram probabilities.
     *
     * @return the threshold for the spell checker
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * Returns whether suffix check is enabled for the spell checker.
     * @see SimpleSpellChecker#forcedDeDaSplitCheck(Word, Sentence)
     * @see SimpleSpellChecker#forcedQuestionSuffixSplitCheck(Word, Sentence) (Word, Sentence)
     * @see SimpleSpellChecker#forcedSuffixSplitCheck(Word, Sentence)
     *
     * @return a boolean indicating whether suffix check is enabled for the spell checker
     */
    public boolean suffixCheck() {
        return suffixCheck;
    }

    /**
     * Returns whether the root n-gram is enabled for the spell checker.
     *
     * @return a boolean indicating whether the root n-gram is enabled for the spell checker
     */
    public boolean isRootNGram() {
        return rootNGram;
    }

    /**
     * Returns the minimum length of words viable for spell checking.
     *
     * @return the minimum word length for the spell checker
     */
    public int getMinWordLength() {
        return minWordLength;
    }

    /**
     * Returns the domain name
     *
     * @return the domain name
     */
    public String getDomain() {
        return domain;
    }
}
