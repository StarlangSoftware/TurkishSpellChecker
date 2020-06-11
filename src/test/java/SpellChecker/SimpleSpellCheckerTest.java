package SpellChecker;

import Corpus.Sentence;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.*;

public class SimpleSpellCheckerTest {

    @org.junit.Test
    public void testSpellCheck() throws FileNotFoundException {
        FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer();
        SimpleSpellChecker simpleSpellChecker = new SimpleSpellChecker(fsm);
        Scanner input = new Scanner(new File("misspellings.txt"));
        while (input.hasNext()){
            String misspelled = input.next();
            String corrected = input.next();
            assertEquals(corrected, simpleSpellChecker.spellCheck(new Sentence(misspelled)).toString());
        }
        input.close();
    }
}