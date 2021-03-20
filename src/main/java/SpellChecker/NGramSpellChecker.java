package SpellChecker;

import Corpus.Sentence;
import Dictionary.Word;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParseList;
import Ngram.NGram;
import Ngram.SimpleNGram;

import java.util.ArrayList;

public class NGramSpellChecker extends SimpleSpellChecker {
    private NGram<String> nGram;
    private SimpleNGram simpleNGram;
    private boolean rootNGram;
    private double threshold = 0.0;
    private boolean simple;

    /**
     * A constructor of {@link NGramSpellChecker} class which takes a {@link FsmMorphologicalAnalyzer} and an {@link NGram}
     * as inputs. Then, calls its super class {@link SimpleSpellChecker} with given {@link FsmMorphologicalAnalyzer} and
     * assigns given {@link NGram} to the nGram variable.
     *
     * @param fsm   {@link FsmMorphologicalAnalyzer} type input.
     * @param nGram {@link NGram} type input.
     * @param rootNGram This parameter must be true, if the nGram is NGram generated from the root words; false otherwise.
     */
    public NGramSpellChecker(FsmMorphologicalAnalyzer fsm, NGram<String> nGram, boolean rootNGram) {
        super(fsm);
        this.nGram = nGram;
        this.rootNGram = rootNGram;
        simple = false;
    }

    /**
     * A constructor of {@link NGramSpellChecker} class which takes a {@link FsmMorphologicalAnalyzer} and an {@link NGram}
     * as inputs. Then, calls its super class {@link SimpleSpellChecker} with given {@link FsmMorphologicalAnalyzer} and
     * assigns given {@link NGram} to the nGram variable.
     *
     * @param fsm   {@link FsmMorphologicalAnalyzer} type input.
     * @param simpleNGram {@link SimpleNGram} type input.
     * @param rootNGram This parameter must be true, if the nGram is NGram generated from the root words; false otherwise.
     */
    public NGramSpellChecker(FsmMorphologicalAnalyzer fsm, SimpleNGram simpleNGram, boolean rootNGram) {
        super(fsm);
        this.simpleNGram = simpleNGram;
        this.rootNGram = rootNGram;
        simple = true;
    }

    /**
     * Checks the morphological analysis of the given word in the given index. If there is no misspelling, it returns
     * the longest root word of the possible analyses.
     * @param sentence Sentence to be analyzed.
     * @param index Index of the word
     * @return If the word is misspelled, null; otherwise the longest root word of the possible analyses.
     */
    private Word checkAnalysisAndSetRoot(Sentence sentence, int index){
        if (index < sentence.wordCount()){
            FsmParseList fsmParses = fsm.morphologicalAnalysis(sentence.getWord(index).getName());
            if (fsmParses.size() != 0){
                if (rootNGram){
                    return fsmParses.getParseWithLongestRootWord().getWord();
                } else {
                    return sentence.getWord(index);
                }
            }
        }
        return null;
    }

    public void setThreshold(double threshold){
        this.threshold = threshold;
    }

    private double getProbability(String word1, String word2){
        if (simple){
            return simpleNGram.getProbability(word1 + " " + word2);
        } else {
            return nGram.getProbability(word1, word2);
        }
    }

    /**
     * The spellCheck method takes a {@link Sentence} as an input and loops i times where i ranges from 0 to size of words in given sentence.
     * Then, it calls morphologicalAnalysis method with each word and assigns it to the {@link FsmParseList}, if the size of
     * {@link FsmParseList} is equal to the 0, it adds current word to the candidateList and assigns it to the candidates {@link ArrayList}.
     * <p>
     * Later on, it loops through candidates {@link ArrayList} and calls morphologicalAnalysis method with each word and
     * assigns it to the {@link FsmParseList}. Then, it gets the root from {@link FsmParseList}. For the first time, it defines a previousRoot
     * by calling getProbability method with root, and for the following times it calls getProbability method with previousRoot and root.
     * Then, it finds out the best probability and the corresponding candidate as best candidate and adds it to the result {@link Sentence}.
     * <p>
     * If the size of {@link FsmParseList} is not equal to 0, it directly adds the current word to the result {@link Sentence} and finds
     * the previousRoot directly from the {@link FsmParseList}.
     *
     * @param sentence {@link Sentence} type input.
     * @return Sentence result.
     */
    public Sentence spellCheck(Sentence sentence) {
        Word word, bestRoot;
        Word previousRoot = null, root, nextRoot;
        String bestCandidate;
        FsmParseList fsmParses;
        double previousProbability, nextProbability, bestProbability;
        ArrayList<String> candidates;
        Sentence result = new Sentence();
        root = checkAnalysisAndSetRoot(sentence, 0);
        nextRoot = checkAnalysisAndSetRoot(sentence, 1);
        for (int i = 0; i < sentence.wordCount(); i++) {
            word = sentence.getWord(i);
            if (root == null) {
                candidates = candidateList(word);
                bestCandidate = word.getName();
                bestRoot = word;
                bestProbability = threshold;
                for (String candidate : candidates) {
                    fsmParses = fsm.morphologicalAnalysis(candidate);
                    if (rootNGram){
                        root = fsmParses.getParseWithLongestRootWord().getWord();
                    } else {
                        root = new Word(candidate);
                    }
                    if (previousRoot != null) {
                        previousProbability = getProbability(previousRoot.getName(), root.getName());
                    } else {
                        previousProbability = 0.0;
                    }
                    if (nextRoot != null) {
                        nextProbability = getProbability(root.getName(), nextRoot.getName());
                    } else {
                        nextProbability = 0.0;
                    }
                    if (Math.max(previousProbability, nextProbability) > bestProbability) {
                        bestCandidate = candidate;
                        bestRoot = root;
                        bestProbability = Math.max(previousProbability, nextProbability);
                    }
                }
                root = bestRoot;
                result.addWord(new Word(bestCandidate));
            } else {
                result.addWord(word);
            }
            previousRoot = root;
            root = nextRoot;
            nextRoot = checkAnalysisAndSetRoot(sentence, i + 2);
        }
        return result;
    }
}
