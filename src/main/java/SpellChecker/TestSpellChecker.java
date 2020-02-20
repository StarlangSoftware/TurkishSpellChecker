package SpellChecker;

import Corpus.Sentence;
import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;

public class TestSpellChecker {

    private static Sentence spellCheck(Sentence sentence, FsmMorphologicalAnalyzer fsm){
        SpellChecker spellChecker = new SimpleSpellChecker(fsm);
        return spellChecker.spellCheck(sentence);
    }

    public static void main(String[] args){
        FsmMorphologicalAnalyzer fsm;
        fsm = new FsmMorphologicalAnalyzer();
        Sentence s1 = new Sentence("istabnulda eğleneerk gezdielr .");
        System.out.println(s1);
        Sentence s2 = spellCheck(s1, fsm);
        System.out.println(s2);
    }
}
