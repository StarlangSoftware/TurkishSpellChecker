package SpellChecker;

import Corpus.Sentence;
import Dictionary.Word;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParseList;
import Ngram.NGram;

import java.util.ArrayList;

public class NGramSpellChecker extends SimpleSpellChecker {
    private NGram<String> nGram;
    private boolean rootNGram;
    private double threshold = 0.0;

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
    private Word checkAnalysisAndSetRoot(String word){
        FsmParseList fsmParses = fsm.morphologicalAnalysis(word);
        if (fsmParses.size() != 0){
            if (rootNGram){
                return fsmParses.getParseWithLongestRootWord().getWord();
            } else {
                return new Word(word);
            }
        }
        return null;
    }

    public void setThreshold(double threshold){
        this.threshold = threshold;
    }

    private double getProbability(String word1, String word2){
        return nGram.getProbability(word1, word2);
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
        Candidate bestCandidate;
        double previousProbability, nextProbability, bestProbability;
        ArrayList<Candidate> candidates;
        Sentence result = new Sentence();
        root = checkAnalysisAndSetRoot(sentence, 0);
        nextRoot = checkAnalysisAndSetRoot(sentence, 1);

        for (int repeat = 0; repeat < 2; repeat++) {
            for (int i = 0; i < sentence.wordCount(); i++) {
                Word nextWord = null;
                Word previousWord = null;
                Word nextNextWord = null;
                Word previousPreviousWord = null;
                word = sentence.getWord(i);

                if(i > 0)
                    previousWord = sentence.getWord(i-1);
                if(i > 1)
                    previousPreviousWord = sentence.getWord(i-2);
                if(i < sentence.wordCount()-1)
                    nextWord = sentence.getWord(i+1);
                if(i < sentence.wordCount()-2)
                    nextNextWord = sentence.getWord(i+2);

                if(forcedMisspellCheck(word,result) || forcedBackwardMergeCheck(word,result,previousWord)){
                    continue;
                }
                if(forcedForwardMergeCheck(word,result,nextWord)){
                    i++;
                    continue;
                }
                if(forcedSplitCheck(word,result) || forcedShortcutCheck(word,result,previousWord)){
                    continue;
                }

                if (root == null) {
                    candidates = candidateList(word);
                    candidates.addAll(mergedCandidatesList(previousWord,word,nextWord));
                    candidates.addAll(splitCandidatesList(word));
                    bestCandidate = new Candidate(word.getName(), Operator.NO_CHANGE);
                    bestRoot = word;
                    bestProbability = threshold;

                    for (Candidate candidate : candidates) {

                        if(candidate.getOperator().equals(Operator.SPELL_CHECK) || candidate.getOperator().equals(Operator.MISSPELLED_REPLACE)){
                            root = checkAnalysisAndSetRoot(candidate.getCandidate());
                        }

                        if(candidate.getOperator().equals(Operator.BACKWARD_MERGE) && previousWord != null && previousPreviousWord != null){
                            root = checkAnalysisAndSetRoot(previousWord.getName()+word.getName());
                            previousRoot = checkAnalysisAndSetRoot(previousPreviousWord.getName());
                        }

                        if(candidate.getOperator().equals(Operator.FORWARD_MERGE) && nextWord != null && nextNextWord != null){
                            root = checkAnalysisAndSetRoot(word.getName()+nextWord.getName());
                            nextRoot = checkAnalysisAndSetRoot(nextNextWord.getName());
                        }

                        if (previousRoot != null) {
                            if(candidate.getOperator().equals(Operator.SPLIT)){
                                root = checkAnalysisAndSetRoot(candidate.getCandidate().split(" ")[0]);
                            }
                            previousProbability = getProbability(previousRoot.getName(), root.getName());
                        } else {
                            previousProbability = 0.0;
                        }
                        if (nextRoot != null) {
                            if(candidate.getOperator().equals(Operator.SPLIT)){
                                root = checkAnalysisAndSetRoot(candidate.getCandidate().split(" ")[1]);
                            }
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
                    if(bestCandidate.getOperator().equals(Operator.FORWARD_MERGE)) {
                        i++;
                    }
                    if(bestCandidate.getOperator().equals(Operator.BACKWARD_MERGE)) {
                        result.replaceWord(i - 1, new Word(bestCandidate.getCandidate()));
                    }
                    else{
                        result.addWord(new Word(bestCandidate.getCandidate()));
                    }
                    root = bestRoot;
                } else {
                    result.addWord(word);
                }
                previousRoot = root;
                root = nextRoot;
                nextRoot = checkAnalysisAndSetRoot(sentence, i + 2);
            }
            sentence = new Sentence(result.toString());
            if(repeat<1){
                result = new Sentence();
                previousRoot = null;
                root = checkAnalysisAndSetRoot(sentence, 0);
                nextRoot = checkAnalysisAndSetRoot(sentence, 1);
            }
        }
        return result;
    }
}
