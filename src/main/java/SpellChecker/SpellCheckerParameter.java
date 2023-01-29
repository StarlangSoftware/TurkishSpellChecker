package SpellChecker;

import Corpus.Sentence;
import Dictionary.Word;

public class SpellCheckerParameter {
    private double threshold = 0.0;
    private boolean deMiCheck = true;
    private boolean rootNGram = true;
    private int minWordLength = 4;

    /**
     * Constructs a SpellCheckerParameter object with default values.
     *
     * The default threshold is 0.0, the De-Mi check is enabled, the root ngram is enabled and
     * the minimum word length is 4.
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
     * Enables or disables De-Mi check for the spell checker.
     * @see SimpleSpellChecker#forcedDeDaSplitCheck(Word, Sentence)
     * @see SimpleSpellChecker#forcedQuestionSuffixSplitCheck(Word, Sentence) (Word, Sentence)
     *
     * @param deMiCheck a boolean indicating whether the De-Mi check should be enabled (true) or disabled (false)
     */
    public void setDeMiCheck(boolean deMiCheck) {
        this.deMiCheck = deMiCheck;
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
     * Returns the threshold value used in calculating the n-gram probabilities.
     *
     * @return the threshold for the spell checker
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * Returns whether De-Mi check is enabled for the spell checker.
     * @see SimpleSpellChecker#forcedDeDaSplitCheck(Word, Sentence)
     * @see SimpleSpellChecker#forcedQuestionSuffixSplitCheck(Word, Sentence) (Word, Sentence)
     *
     * @return a boolean indicating whether De-Mi check is enabled for the spell checker
     */
    public boolean deMiCheck() {
        return deMiCheck;
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
}
