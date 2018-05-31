package SpellChecker;

import Corpus.Sentence;
import Dictionary.Word;
import Language.TurkishLanguage;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParseList;

import java.util.ArrayList;
import java.util.Random;

public class SimpleSpellChecker implements SpellChecker{
    protected FsmMorphologicalAnalyzer fsm;

    private ArrayList<String> generateCandidateList(String word){
        String s = TurkishLanguage.LOWERCASE_LETTERS;
        ArrayList<String> candidates = new ArrayList<String>();
        for (int i = 0; i < word.length(); i++){
            String deleted = word.substring(0, i) + word.substring(i + 1);
            candidates.add(deleted);
            for (int j = 0; j < s.length(); j++){
                String replaced = word.substring(0, i) + s.charAt(j) + word.substring(i + 1);
                candidates.add(replaced);
            }
            for (int j = 0; j < s.length(); j++){
                String added = word.substring(0, i) + s.charAt(j) + word.substring(i);
                candidates.add(added);
            }
        }
        return candidates;
    }

    protected ArrayList<String> candidateList(Word word){
        ArrayList<String> candidates;
        candidates = generateCandidateList(word.getName());
        for (int i = 0; i < candidates.size(); i++){
            FsmParseList fsmParseList = fsm.morphologicalAnalysis(candidates.get(i));
            if (fsmParseList.size() == 0){
                candidates.remove(i);
                i--;
            }
        }
        return candidates;
    }

    public SimpleSpellChecker(FsmMorphologicalAnalyzer fsm){
        this.fsm = fsm;
    }

    public Sentence spellCheck(Sentence sentence) {
        Word word, newWord;
        int randomCandidate;
        Random random = new Random();
        ArrayList<String> candidates;
        Sentence result = new Sentence();
        for (int i = 0; i < sentence.wordCount(); i++){
            word = sentence.getWord(i);
            FsmParseList fsmParseList = fsm.morphologicalAnalysis(word.getName());
            if (fsmParseList.size() == 0){
                candidates = candidateList(word);
                if (candidates.size() > 0){
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
