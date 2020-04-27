package SpellChecker;

import Corpus.Sentence;
import Dictionary.TurkishWordComparator;
import Dictionary.TxtDictionary;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import Ngram.NGram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Scanner;

public class TestSpellChecker {

    private static void spellCheck(){
        FsmMorphologicalAnalyzer fsm;
        fsm = new FsmMorphologicalAnalyzer();
        SpellChecker s = new SimpleSpellChecker(fsm);
        Sentence s1 = new Sentence("cekwt .");
        System.out.println(s1);
        Sentence s2 = s.spellCheck(s1);
        System.out.println(s2);
    }

    private static void analyzeUnique() throws FileNotFoundException {
        FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer(new TxtDictionary("tourism_dictionary.txt", new TurkishWordComparator(), "tourism_misspellings.txt"));
        SpellChecker spellChecker = new SimpleSpellChecker(fsm);
        PrintWriter pw = new PrintWriter("output.txt");
        Scanner input = new Scanner(new File("data.txt"));
        while (input.hasNext()){
            String item = input.next();
            if (fsm.getDictionary().getCorrectForm(item) == null){
                if (fsm.morphologicalAnalysis(item).size() == 0){
                    String item1 = item.toUpperCase(new Locale("tr"));
                    if (fsm.morphologicalAnalysis(item1).size() == 0){
                        Sentence s = spellChecker.spellCheck(new Sentence(item));
                        if (!s.getWord(0).getName().equals(item)){
                            pw.println(item + " " + s.getWord(0).getName());
                        } else {
                            pw.println(item);
                        }
                    }
                }
            }
        }
        pw.close();
        input.close();
    }

    public static void main(String[] args) throws FileNotFoundException{
        spellCheck();
    }
}
