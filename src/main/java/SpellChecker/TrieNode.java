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

    public void addChild(Character ch, TrieNode child){
        children.put(ch, child);
    }

    public String childrenToString(){
        StringBuilder result = new StringBuilder();
        for (Character ch : children.keySet()){
            result.append(ch);
        }
        return result.toString();
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
