package SpellChecker;

import java.util.HashMap;

public class TrieNode {
    private HashMap<Character, TrieNode> children;
    private boolean isWord;

    /**
     * A constructor of {@link TrieNode} class which constructs a new TrieNode with an empty children HashMap
     */
    public TrieNode() {
        this.children = new HashMap<>();
    }

    /**
     * Returns the child TrieNode with the given character as its value.
     * @param character The character value of the child TrieNode.
     * @return TrieNode with the given character value.
     */
    public TrieNode getChild(Character character) {
        return children.get(character);
    }

    /**
     * Returns the HashMap of children for the current TrieNode.
     * @return HashMap of children for the current TrieNode.
     */
    public HashMap<Character, TrieNode> getChildren() {
        return children;
    }

    /**
     * Returns whether the current TrieNode represents the end of a word.
     * @return true if the current TrieNode represents the end of a word, false otherwise.
     */
    public boolean isWord() {
        return isWord;
    }

    /**
     * Sets whether the current TrieNode represents the end of a word.
     * @param isWord true if the current TrieNode represents the end of a word, false otherwise.
     */
    public void setIsWord(boolean isWord) {
        this.isWord = isWord;
    }
}
