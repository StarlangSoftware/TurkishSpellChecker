package SpellChecker;

import Corpus.Sentence;
import Dictionary.Word;
import Language.TurkishLanguage;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParseList;

import java.util.ArrayList;
import java.util.Random;

public class SimpleSpellChecker implements SpellChecker {
    protected FsmMorphologicalAnalyzer fsm;

    /**
     * The generateCandidateList method takes a String as an input. Firstly, it creates a String consists of lowercase Turkish letters
     * and an {@link ArrayList} candidates. Then, it loops i times where i ranges from 0 to the length of given word. It gets substring
     * from 0 to ith index and concatenates it with substring from i+1 to the last index as a new String called deleted. Then, adds
     * this String to the candidates {@link ArrayList}. Secondly, it loops j times where j ranges from 0 to length of
     * lowercase letters String and adds the jth character of this String between substring of given word from 0 to ith index
     * and the substring from i+1 to the last index, then adds it to the candidates {@link ArrayList}. Thirdly, it loops j
     * times where j ranges from 0 to length of lowercase letters String and adds the jth character of this String between
     * substring of given word from 0 to ith index and the substring from i to the last index, then adds it to the candidates {@link ArrayList}.
     *
     * @param word String input.
     * @return ArrayList candidates.
     */
    private ArrayList<String> generateCandidateList(String word) {
        String s = TurkishLanguage.LOWERCASE_LETTERS;
        ArrayList<String> candidates = new ArrayList<String>();
        for (int i = 0; i < word.length(); i++) {
            if (i < word.length() - 1){
                String swapped = word.substring(0, i) + word.charAt(i + 1) + word.charAt(i) + word.substring(i + 2);
                candidates.add(swapped);
            }
            if (TurkishLanguage.LETTERS.contains("" + word.charAt(i)) || "wxq".contains("" + word.charAt(i))){
                String deleted = word.substring(0, i) + word.substring(i + 1);
                candidates.add(deleted);
                for (int j = 0; j < s.length(); j++) {
                    String replaced = word.substring(0, i) + s.charAt(j) + word.substring(i + 1);
                    candidates.add(replaced);
                }
                for (int j = 0; j < s.length(); j++) {
                    String added = word.substring(0, i) + s.charAt(j) + word.substring(i);
                    candidates.add(added);
                }
            }
        }
        return candidates;
    }

    /**
     * The candidateList method takes a {@link Word} as an input and creates a candidates {@link ArrayList} by calling generateCandidateList
     * method with given word. Then, it loop i times where i ranges from 0 to size of candidates {@link ArrayList} and creates a
     * {@link FsmParseList} by calling morphologicalAnalysis with each item of candidates {@link ArrayList}. If the size of
     * {@link FsmParseList} is 0, it then removes the ith item.
     *
     * @param word Word input.
     * @return candidates {@link ArrayList}.
     */
    protected ArrayList<String> candidateList(Word word) {
        ArrayList<String> candidates;
        candidates = generateCandidateList(word.getName());
        for (int i = 0; i < candidates.size(); i++) {
            FsmParseList fsmParseList = fsm.morphologicalAnalysis(candidates.get(i));
            if (fsmParseList.size() == 0) {
                String newCandidate = fsm.getDictionary().getCorrectForm(candidates.get(i));
                if (newCandidate != null && fsm.morphologicalAnalysis(newCandidate).size() > 0){
                    candidates.set(i, newCandidate);
                } else {
                    candidates.remove(i);
                    i--;
                }
            }
        }
        return candidates;
    }

    /**
     * A constructor of {@link SimpleSpellChecker} class which takes a {@link FsmMorphologicalAnalyzer} as an input and
     * assigns it to the fsm variable.
     *
     * @param fsm {@link FsmMorphologicalAnalyzer} type input.
     */
    public SimpleSpellChecker(FsmMorphologicalAnalyzer fsm) {
        this.fsm = fsm;
    }

    /**
     * The spellCheck method takes a {@link Sentence} as an input and loops i times where i ranges from 0 to size of words in given sentence.
     * Then, it calls morphologicalAnalysis method with each word and assigns it to the {@link FsmParseList}, if the size of
     * {@link FsmParseList} is equal to the 0, it adds current word to the candidateList and assigns it to the candidates {@link ArrayList}.
     * if the size of candidates greater than 0, it generates a random number and selects an item from candidates {@link ArrayList} with
     * this random number and assign it as newWord. If the size of candidates is not greater than 0, it directly assigns the
     * current word as newWord. At the end, it adds the newWord to the result {@link Sentence}.
     *
     * @param sentence {@link Sentence} type input.
     * @return Sentence result.
     */
    public Sentence spellCheck(Sentence sentence) {
        Word word, newWord;
        int randomCandidate;
        Random random = new Random();
        ArrayList<String> candidates;
        Sentence result = new Sentence();
        for (int i = 0; i < sentence.wordCount(); i++) {
            word = sentence.getWord(i);
            FsmParseList fsmParseList = fsm.morphologicalAnalysis(word.getName());
            if (fsmParseList.size() == 0) {
                candidates = candidateList(word);
                if (candidates.size() > 0) {
                    randomCandidate = random.nextInt(candidates.size());
                    newWord = new Word(candidates.get(randomCandidate));
                } else {
                    newWord = word;
                }
            } else {
                newWord = word;
            }
            result.addWord(newWord);
        }
        return result;
    }
}
