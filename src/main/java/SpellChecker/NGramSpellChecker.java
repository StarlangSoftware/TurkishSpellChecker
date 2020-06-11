package SpellChecker;

import Corpus.Sentence;
import Dictionary.Word;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParseList;
import Ngram.NGram;

import java.util.ArrayList;

public class NGramSpellChecker extends SimpleSpellChecker {
    private NGram<String> nGram;

    /**
     * A constructor of {@link NGramSpellChecker} class which takes a {@link FsmMorphologicalAnalyzer} and an {@link NGram}
     * as inputs. Then, calls its super class {@link SimpleSpellChecker} with given {@link FsmMorphologicalAnalyzer} and
     * assigns given {@link NGram} to the nGram variable.
     *
     * @param fsm   {@link FsmMorphologicalAnalyzer} type input.
     * @param nGram {@link NGram} type input.
     */
    public NGramSpellChecker(FsmMorphologicalAnalyzer fsm, NGram<String> nGram) {
        super(fsm);
        this.nGram = nGram;
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
        Word previousRoot = null, root;
        String bestCandidate;
        FsmParseList fsmParses;
        double probability, bestProbability;
        ArrayList<String> candidates;
        Sentence result = new Sentence();
        for (int i = 0; i < sentence.wordCount(); i++) {
            word = sentence.getWord(i);
            fsmParses = fsm.morphologicalAnalysis(word.getName());
            if (fsmParses.size() == 0) {
                candidates = candidateList(word);
                bestCandidate = word.getName();
                bestRoot = word;
                bestProbability = 0;
                for (String candidate : candidates) {
                    fsmParses = fsm.morphologicalAnalysis(candidate);
                    root = fsmParses.getParseWithLongestRootWord().getWord();
                    if (previousRoot != null) {
                        probability = nGram.getProbability(previousRoot.getName(), root.getName());
                    } else {
                        probability = nGram.getProbability(root.getName());
                    }
                    if (probability > bestProbability) {
                        bestCandidate = candidate;
                        bestRoot = root;
                        bestProbability = probability;
                    }
                }
                previousRoot = bestRoot;
                result.addWord(new Word(bestCandidate));
            } else {
                result.addWord(word);
                previousRoot = fsmParses.getParseWithLongestRootWord().getWord();
            }
        }
        return result;
    }
}
