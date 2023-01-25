package SpellChecker;

public class TrieCandidate extends Candidate {

    private int currentIndex;
    private double currentPenalty;

    /**
     * Constructs a TrieCandidate object.
     *
     * @param word the candidate word
     * @param currentIndex the current index of the candidate word
     * @param currentPenalty the currentPenalty associated with the candidate word
     */
    public TrieCandidate(String word, int currentIndex, double currentPenalty) {
        super(word, Operator.TRIE_BASED);
        this.currentIndex = currentIndex;
        this.currentPenalty = currentPenalty;
    }

    /**
     * Returns the current index of the candidate word.
     *
     * @return the current index of the candidate word
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Returns the currentPenalty value associated with the candidate word.
     *
     * @return the currentPenalty value associated with the candidate word
     */
    public double getCurrentPenalty() {
        return currentPenalty;
    }

    /**
     * Increments the current index of the candidate word by 1.
     */
    public void nextIndex() {
        currentIndex += 1;
    }

    /**
     * Determines if the candidate word has been fully traversed or not
     *
     * @return true if the current index is equal to the length of the word,
     *         false otherwise
     */
    public boolean isTraversed(){
        return currentIndex == name.length();
    }
}
