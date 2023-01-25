package SpellChecker;

import Corpus.Sentence;
import Dictionary.Word;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParseList;
import Ngram.NGram;
import java.util.ArrayList;

public class NGramSpellChecker extends SimpleSpellChecker {
    private NGram<String> nGram;
    private SpellCheckerParameter parameter;

    /**
     * A constructor of {@link NGramSpellChecker} class which takes a {@link FsmMorphologicalAnalyzer} and an {@link NGram}
     * as inputs. Then, calls its super class {@link SimpleSpellChecker} with given {@link FsmMorphologicalAnalyzer}
     * assigns given {@link NGram} to the nGram variable and creates a {@link SpellCheckerParameter} with default values.
     *
     * @param fsm   {@link FsmMorphologicalAnalyzer} type input.
     * @param nGram {@link NGram} type input.
     */
    public NGramSpellChecker(FsmMorphologicalAnalyzer fsm, NGram<String> nGram) {
        super(fsm);
        this.nGram = nGram;
        parameter = new SpellCheckerParameter();
    }

    /**
     * Another constructor of {@link NGramSpellChecker} class which takes a {@link FsmMorphologicalAnalyzer}, an {@link NGram}
     * and a {@link SpellCheckerParameter} as inputs. Then, calls its super class {@link SimpleSpellChecker} with given {@link FsmMorphologicalAnalyzer}
     * assigns given {@link NGram} to the nGram variable and assigns given {@link SpellCheckerParameter} to the parameter variable.
     *
     * @param fsm   {@link FsmMorphologicalAnalyzer} type input.
     * @param nGram {@link NGram} type input.
     * @param parameter {@link SpellCheckerParameter} type input.
     */
    public NGramSpellChecker(FsmMorphologicalAnalyzer fsm, NGram<String> nGram, SpellCheckerParameter parameter) {
        super(fsm);
        this.nGram = nGram;
        this.parameter = parameter;
    }

    /**
     * Checks the morphological analysis of the given word in the given index. If there is no misspelling, it returns
     * the longest root word of the possible analysis.
     *
     * @param sentence Sentence to be analyzed.
     * @param index Index of the word
     * @return If the word is misspelled, null; otherwise the longest root word of the possible analysis.
     */
    private Word checkAnalysisAndSetRootForWordAtIndex(Sentence sentence, int index) {
        if (index < sentence.wordCount()) {
            String wordName = sentence.getWord(index).getName();
            if ((wordName.matches(".*\\d+.*") && wordName.matches(".*[a-zA-ZçöğüşıÇÖĞÜŞİ]+.*")
                    && !wordName.contains("'")) || wordName.length() < parameter.getMinWordLength()) {
                return sentence.getWord(index);
            }
            FsmParseList fsmParses = fsm.morphologicalAnalysis(wordName);
            if (fsmParses.size() != 0) {
                if (parameter.isRootNGram()) {
                    return fsmParses.getParseWithLongestRootWord().getWord();
                } else {
                    return sentence.getWord(index);
                }
            }
            else {
                String upperCaseWordName = Word.toCapital(wordName);
                FsmParseList upperCaseFsmParses = fsm.morphologicalAnalysis(upperCaseWordName);
                if (upperCaseFsmParses.size() != 0) {
                    if (parameter.isRootNGram()) {
                        return upperCaseFsmParses.getParseWithLongestRootWord().getWord();
                    } else {
                        return sentence.getWord(index);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks the morphological analysis of the given word. If there is no misspelling, it returns
     * the longest root word of the possible analysis.
     *
     * @param word Word to be analyzed.
     * @return If the word is misspelled, null; otherwise the longest root word of the possible analysis.
     */
    private Word checkAnalysisAndSetRoot(String word) {
        FsmParseList fsmParsesOfWord = fsm.morphologicalAnalysis(word);
        if (fsmParsesOfWord.size() != 0) {
            if (parameter.isRootNGram()) {
                return fsmParsesOfWord.getParseWithLongestRootWord().getWord();
            }
            return new Word(word);
        }
        FsmParseList fsmParsesOfCapitalizedWord = fsm.morphologicalAnalysis(Word.toCapital(word));
        if (fsmParsesOfCapitalizedWord.size() != 0) {
            if (parameter.isRootNGram()) {
                return fsmParsesOfCapitalizedWord.getParseWithLongestRootWord().getWord();
            }
            return new Word(word);
        }
        return null;
    }

    private double getProbability(String word1, String word2) {
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
        root = checkAnalysisAndSetRootForWordAtIndex(sentence, 0);
        nextRoot = checkAnalysisAndSetRootForWordAtIndex(sentence, 1);
        for (int i = 0; i < sentence.wordCount(); i++) {
            Word nextWord = null;
            Word previousWord = null;
            Word nextNextWord = null;
            Word previousPreviousWord = null;
            word = sentence.getWord(i);
            if (i > 0){
                previousWord = sentence.getWord(i - 1);
            }
            if (i > 1){
                previousPreviousWord = sentence.getWord(i - 2);
            }
            if (i < sentence.wordCount() - 1){
                nextWord = sentence.getWord(i + 1);
            }
            if (i < sentence.wordCount() - 2){
                nextNextWord = sentence.getWord(i + 2);
            }
            if (forcedMisspellCheck(word, result)){
                previousRoot = checkAnalysisAndSetRootForWordAtIndex(result, result.wordCount() - 1);
                root = nextRoot;
                nextRoot = checkAnalysisAndSetRootForWordAtIndex(sentence, i + 2);
                continue;
            }
            if (forcedBackwardMergeCheck(word, result, previousWord) || forcedSuffixMergeCheck(word, result, previousWord)) {
                previousRoot = checkAnalysisAndSetRootForWordAtIndex(result, result.wordCount() - 1);
                root = checkAnalysisAndSetRootForWordAtIndex(sentence, i + 1);
                nextRoot = checkAnalysisAndSetRootForWordAtIndex(sentence, i + 2);
                continue;
            }
            if (forcedForwardMergeCheck(word, result, nextWord) || forcedHyphenMergeCheck(word, result, previousWord, nextWord)) {
                i++;
                previousRoot = checkAnalysisAndSetRootForWordAtIndex(result, result.wordCount() - 1);
                root = checkAnalysisAndSetRootForWordAtIndex(sentence, i + 1);
                nextRoot = checkAnalysisAndSetRootForWordAtIndex(sentence, i + 2);
                continue;
            }
            if (forcedSplitCheck(word, result) || forcedShortcutSplitCheck(word, result)) {
                previousRoot = checkAnalysisAndSetRootForWordAtIndex(result, result.wordCount() - 1);
                root = nextRoot;
                nextRoot = checkAnalysisAndSetRootForWordAtIndex(sentence, i + 2);
                continue;
            }
            if (parameter.deMiCheck()) {
                if (forcedDeDaSplitCheck(word, result) || forcedQuestionSuffixSplitCheck(word, result)) {
                    previousRoot = checkAnalysisAndSetRootForWordAtIndex(result, result.wordCount() - 1);
                    root = nextRoot;
                    nextRoot = checkAnalysisAndSetRootForWordAtIndex(sentence, i + 2);
                    continue;
                }
            }
            if (root == null || (word.getName().length() < parameter.getMinWordLength() && fsm.morphologicalAnalysis(word.getName()).size() == 0)) {
                candidates = new ArrayList<>();
                if (root == null) {
                    candidates.addAll(candidateList(word, sentence));
                    candidates.addAll(splitCandidatesList(word));
                }
                candidates.addAll(mergedCandidatesList(previousWord, word, nextWord));
                bestCandidate = new Candidate(word.getName(), Operator.NO_CHANGE);
                bestRoot = word;
                bestProbability = parameter.getThreshold();
                for (Candidate candidate : candidates) {
                    if (candidate.getOperator() == Operator.SPELL_CHECK || candidate.getOperator() == Operator.MISSPELLED_REPLACE
                            || candidate.getOperator() == Operator.CONTEXT_BASED || candidate.getOperator() == Operator.TRIE_BASED) {
                        root = checkAnalysisAndSetRoot(candidate.getName());
                    }
                    if (candidate.getOperator() == Operator.BACKWARD_MERGE && previousWord != null) {
                        root = checkAnalysisAndSetRoot(previousWord.getName() + word.getName());
                        if (previousPreviousWord != null) {
                            previousRoot = checkAnalysisAndSetRoot(previousPreviousWord.getName());
                        }
                    }
                    if (candidate.getOperator() == Operator.FORWARD_MERGE && nextWord != null) {
                        root = checkAnalysisAndSetRoot(word.getName() + nextWord.getName());
                        if (nextNextWord != null) {
                            nextRoot = checkAnalysisAndSetRoot(nextNextWord.getName());
                        }
                    }
                    if (previousRoot != null) {
                        if (candidate.getOperator() == Operator.SPLIT){
                            root = checkAnalysisAndSetRoot(candidate.getName().split(" ")[0]);
                        }
                        previousProbability = getProbability(previousRoot.getName(), root.getName());
                    } else {
                        previousProbability = 0.0;
                    }
                    if (nextRoot != null) {
                        if (candidate.getOperator() == Operator.SPLIT){
                            root = checkAnalysisAndSetRoot(candidate.getName().split(" ")[1]);
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
                if (bestCandidate.getOperator() == Operator.FORWARD_MERGE) {
                    i++;
                }
                if (bestCandidate.getOperator() == Operator.BACKWARD_MERGE) {
                    result.replaceWord(result.wordCount() - 1, new Word(bestCandidate.getName()));
                } else{
                    if (bestCandidate.getOperator() == Operator.SPLIT){
                        addSplitWords(bestCandidate.getName(), result);
                    } else {
                        result.addWord(new Word(bestCandidate.getName()));
                    }
                }
                root = bestRoot;
            } else {
                result.addWord(word);
            }
            previousRoot = root;
            root = nextRoot;
            nextRoot = checkAnalysisAndSetRootForWordAtIndex(sentence, i + 2);
        }
        return result;
    }
}
