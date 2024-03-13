package SpellChecker;

import java.util.Locale;

public class Trie {
    private final TrieNode rootNode;

    /**
     * A constructor of {@link Trie} class which constructs a new Trie with an empty root node
     */
    public Trie() {
        rootNode = new TrieNode();
    }

    /**
     * Inserts a new word into the Trie
     *
     * @param word The word to be inserted
     */
    public void insert(String word) {
        TrieNode currentNode = rootNode;
        for (int i = 0; i < word.length(); i++) {
            char character = word.charAt(i);
            if (currentNode.getChild(character) == null) {
                currentNode.addChild(character, new TrieNode());
            }
            currentNode = currentNode.getChild(character);
        }
        currentNode.setIsWord(true);
    }

    /**
     * Checks if a word is in the Trie
     *
     * @param word The word to be searched for
     * @return true if the word is in the Trie, false otherwise
     */
    public boolean search(String word) {
        TrieNode node = getTrieNode(word.toLowerCase(new Locale("tr", "TR")));
        if (node == null) {
            return false;
        } else {
            return node.isWord();
        }
    }

    /**
     * Checks if a given prefix exists in the Trie
     *
     * @param prefix The prefix to be searched for
     * @return true if the prefix exists, false otherwise
     */
    public boolean startsWith(String prefix) {
        return getTrieNode(prefix.toLowerCase(new Locale("tr", "TR"))) != null;
    }

    /**
     * Returns the TrieNode corresponding to the last character of a given word
     *
     * @param word The word to be searched for
     * @return The TrieNode corresponding to the last character of the word
     */
    public TrieNode getTrieNode(String word) {
        TrieNode currentNode = rootNode;
        for (int i = 0; i < word.length(); i++) {
            char character = word.charAt(i);
            if (currentNode.getChild(character) == null) {
                return null;
            }
            currentNode = currentNode.getChild(character);
        }
        return currentNode;
    }
}
