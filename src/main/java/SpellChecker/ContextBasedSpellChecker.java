package SpellChecker;

import Corpus.Sentence;
import Dictionary.Word;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import Ngram.NGram;
import Util.FileUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ContextBasedSpellChecker extends NGramSpellChecker {
    HashMap<String, ArrayList<String>> contextList;

    /**
     * A constructor of {@link ContextBasedSpellChecker} class which takes a {@link FsmMorphologicalAnalyzer}, an {@link NGram}
     * and a {@link SpellCheckerParameter} as inputs. Then, calls its super class {@link NGramSpellChecker} with given inputs.
     *
     * @param fsm {@link FsmMorphologicalAnalyzer} type input.
     * @param nGram {@link NGram} type input.
     * @param parameter {@link SpellCheckerParameter} type input.
     */
    public ContextBasedSpellChecker(FsmMorphologicalAnalyzer fsm, NGram<String> nGram, SpellCheckerParameter parameter) {
        super(fsm, nGram, parameter);
    }

    /**
     * {@inheritDoc}
     * This method also loads context information from a file.
     */
    @Override
    protected void loadDictionaries() {
        super.loadDictionaries();
        String line;
        ArrayList<String> proximityWords;
        contextList = new HashMap<>();
        try {
            BufferedReader proximityReader = new BufferedReader(new InputStreamReader(FileUtils.getInputStream("context_list.txt"), StandardCharsets.UTF_8));
            while ((line = proximityReader.readLine()) != null) {
                String word = line.split("\t")[0];
                String[] otherWords = line.split("\t")[1].split(" ");
                proximityWords = new ArrayList<>(Arrays.asList(otherWords));
                contextList.put(word, proximityWords);
            }
            proximityReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses context information to generate candidates for a misspelled word.
     * The candidates are the words that are in the context of the neighbouring words of the misspelled word.
     * Uses the {@Link damerauLevenshteinDistance(String,String) method to calculate the distance between the misspelled word and
     * the candidates and to determine whether the candidates are valid.
     *
     * @param word the misspelled word
     * @param sentence the sentence containing the misspelled word
     * @return an ArrayList of valid candidates for the misspelled word
     */
    @Override
    protected ArrayList<Candidate> candidateList(Word word, Sentence sentence) {
        ArrayList<Word> words = new ArrayList<>(sentence.getWords());
        ArrayList<String> roots = new ArrayList<>();
        HashSet<Candidate> candidates = new HashSet<>();
        ArrayList<Candidate> validCandidates = new ArrayList<>();
        words.remove(word);
        for (Word w : words) {
            if(fsm.morphologicalAnalysis(w.getName()).size() > 0) {
                roots.add(fsm.morphologicalAnalysis(w.getName()).getParseWithLongestRootWord().getWord().getName());
            }
        }
        for (String root : roots) {
            if(contextList.containsKey(root)) {
                for (String s : contextList.get(root)) {
                    candidates.add(new Candidate(s, Operator.CONTEXT_BASED));
                }
            }
        }
        for(Candidate candidate : candidates) {
            int distance;
            if(candidate.getName().length() < 5) {
                distance = 1;
            }
            else {
                if(candidate.getName().length() < 7) {
                    distance = 2;
                }
                else {
                    distance = 3;
                }
            }
            if(damerauLevenshteinDistance(word.getName(), candidate.getName()) <= distance) {
                validCandidates.add(candidate);
            }
        }
        return validCandidates;
    }

    /**
     * Calculates the Damerau-Levenshtein distance between two strings.
     * This method also allows for the transposition of adjacent characters,
     * which is not possible in a standard Levenshtein distance calculation.
     *
     * @param first the first string
     * @param second the second string
     * @return the Damerau-Levenshtein distance between the two strings
     */
    private int damerauLevenshteinDistance(String first, String second) {
        if (first == null || first.isEmpty())  {
            return second == null || second.isEmpty() ? 0 : second.length();
        }
        else if (second == null || second.isEmpty()) {
            return first.length();
        }
        int firstLength = first.length();
        int secondLength = second.length();
        int[][] distanceMatrix = new int[firstLength + 1][secondLength + 1];

        for (int firstIndex = 0; firstIndex <= firstLength; firstIndex++) {
            distanceMatrix[firstIndex][0] = firstIndex;
        }
        for (int secondIndex = 0; secondIndex <= secondLength; secondIndex++) {
            distanceMatrix[0][secondIndex] = secondIndex;
        }
        for (int firstIndex = 1; firstIndex <= firstLength; firstIndex++) {
            for (int secondIndex = 1; secondIndex <= secondLength; secondIndex++) {
                int cost = first.charAt(firstIndex - 1) == second.charAt(secondIndex - 1) ? 0 : 1;
                distanceMatrix[firstIndex][secondIndex] = Math.min(Math.min(distanceMatrix[firstIndex - 1][secondIndex] + 1,
                        distanceMatrix[firstIndex][secondIndex - 1] + 1), distanceMatrix[firstIndex - 1][secondIndex - 1] + cost);
                if (firstIndex == 1 || secondIndex == 1) {
                    continue;
                }
                if (first.charAt(firstIndex - 1) == second.charAt(secondIndex - 2) && first.charAt(firstIndex - 2) == second.charAt(secondIndex - 1)) {
                    distanceMatrix[firstIndex][secondIndex] = Math.min(distanceMatrix[firstIndex][secondIndex], distanceMatrix[firstIndex - 2][secondIndex - 2] + cost);
                }
            }
        }
        return distanceMatrix[firstLength][secondLength];
    }
}
