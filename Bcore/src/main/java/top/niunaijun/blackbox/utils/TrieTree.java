package top.niunaijun.blackbox.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of a Trie (prefix tree) data structure for efficient string
 * matching and prefix-based searching. Supports adding individual words or bulk
 * word lists, and searching for the shortest matching prefix of a given input string.
 *
 * <p>This is commonly used for package name or URL matching in the virtual environment
 * to quickly determine if a string starts with any registered prefix.
 */
public class TrieTree {

    //The root node of TrieTree
    private final TrieNode root = new TrieNode();

    //The node type of TrieTree
    private static class TrieNode {
        char content;
        String word;
        boolean isEnd = false; // This node is whether the end of a word
        List<TrieNode> children = new LinkedList<>();

        public TrieNode() {}

        public TrieNode(char content, String word) {
            this.content = content;
            this.word    = word;
        }


        @Override
        public boolean equals(Object object) {
            if (object instanceof TrieNode) {
                return ((TrieNode) object).content == content;
            }
            return false;
        }

        public TrieNode nextNode(char content) {
            for (TrieNode childNode : children) {
                if (childNode.content == content)
                    return childNode;
            }
            return null;
        }
    }

    /**
     * Adds a word to the trie. Each character of the word becomes a node in the tree,
     * and the last character's node is marked as a word end.
     *
     * @param word the word to insert into the trie
     */
    public void add(String word) {
        TrieNode current = root;
        StringBuilder wordBuilder = new StringBuilder();
        for (int index = 0; index < word.length(); ++index) {
            char content = word.charAt(index);
            wordBuilder.append(content);
            TrieNode node = new TrieNode(content, wordBuilder.toString());
            if (current.children.contains(node)) {
                current = current.nextNode(content);
            } else {
                current.children.add(node);
                current = node;
            }

            if (index == (word.length() - 1))
                current.isEnd = true;
        }
    }

    /**
     * Adds all words from the given list to the trie.
     *
     * @param words the list of words to insert
     */
    public void addAll(List<String> words) {
        for (String word : words) {
            add(word);
        }
    }

    /**
     * Searches the trie for the shortest prefix of the given word that exists as a
     * complete word in the trie. The search proceeds character by character and returns
     * as soon as a word-end node is encountered.
     *
     * @param word the input string to search for a matching prefix
     * @return the matched prefix string if found, or {@code null} if no prefix in the
     *         trie matches the beginning of the input
     */
    public String search(String word) {
        TrieNode current = root;
        for (int index = 0; index < word.length(); ++index) {
            char content = word.charAt(index);

            TrieNode node = new TrieNode(content, null);
            if (current.children.contains(node))
                current = current.nextNode(content);
            else
                return null;

            if (current.isEnd)
                return current.word;
        }
        return null;
    }
}
