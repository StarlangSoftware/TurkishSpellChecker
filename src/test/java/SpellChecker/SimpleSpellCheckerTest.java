package SpellChecker;

import Corpus.Sentence;
import Dictionary.Word;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.Assert.*;

public class SimpleSpellCheckerTest {

    @Test
    public void testSpellCheck() {
        Sentence[] original = {
                new Sentence("yeni sezon başladı"),
                new Sentence("sırtıkara adındaki canlı , bir balıktır"),
                new Sentence("siyah ayı , ayıgiller familyasına ait bir ayı türüdür"),
                new Sentence("yeni sezon başladı gibi"),
                new Sentence("alışveriş için markete gitti"),
                new Sentence("küçük bir yalıçapkını geçti"),
                new Sentence("meslek odaları birliği yeniden toplandı"),
                new Sentence("yeni yılın sonrasında vakalarda artış oldu"),
                new Sentence("atomik saatin 10 mhz sinyali kalibrasyon hizmetlerinde referans olarak kullanılmaktadır"),
                new Sentence("rehberimiz bu bölgedeki çıngıraklı yılan varlığı hakkında konuştu"),
                new Sentence("bu haksızlık da unutulup gitmişti"),
                new Sentence("4'lü tahıl zirvesi İstanbul'da gerçekleşti"),
                new Sentence("10'luk sistemden 100'lük sisteme geçiş yapılacak"),
                new Sentence("play-off maçlarına çıkacak takımlar belli oldu"),
                new Sentence("bu filmi daha önce görmemiş miydik diye sordu"),
                new Sentence("bu son model cihaz 24 inç ekran büyüklüğünde ve 9 kg ağırlıktadır")};
        Sentence[] modified = {
                new Sentence("yenisezon başladı"),
                new Sentence("sırtı kara adındaki canlı , bir balıktır"),
                new Sentence("siyahayı , ayıgiller familyasına ait bir ayı türüdür"),
                new Sentence("yeni se zon başladı gibs"),
                new Sentence("alis veriş için markete gitit"),
                new Sentence("kucuk bri yalı çapkını gecti"),
                new Sentence("mes lek odaları birliği yenidün toplandı"),
                new Sentence("yeniyılın sonrasında vakalarda artış oldu"),
                new Sentence("atomik saatin 10mhz sinyali kalibrasyon hizmetlerinde referans olarka kullanılmaktadır"),
                new Sentence("rehperimiz buı bölgedeki çıngıraklıyılan varlıgı hakkınd konustu"),
                new Sentence("bu haksızlıkda unutulup gitmişti"),
                new Sentence("4 lı tahıl zirvesi İstanbul' da gerçekleşti"),
                new Sentence("10 lük sistemden 100 lık sisteme geçiş yapılacak"),
                new Sentence("play - off maçlarına çıkacak takımlar belli oldu"),
                new Sentence("bu filmi daha önce görmemişmiydik diye sordu"),
                new Sentence("bu son model ciha 24inç ekran büyüklüğünde ve 9kg ağırlıktadır")};
        FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer();
        SimpleSpellChecker simpleSpellChecker = new SimpleSpellChecker(fsm);
        for (int i = 0; i < modified.length; i++) {
            assertEquals(original[i].toString(), simpleSpellChecker.spellCheck(modified[i]).toString());
        }
    }

    public void testDistinctWordList() {
        FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer();
        SimpleSpellChecker simpleSpellChecker = new SimpleSpellChecker(fsm);
        try {
            PrintWriter output1 = new PrintWriter("basic-spellcheck.txt");
            PrintWriter output2 = new PrintWriter("not-analyzed.txt");
            Scanner input = new Scanner(new File("distinct.txt"));
            int count = 0;
            while (input.hasNext()) {
                String word = input.next();
                count++;
                if (count % 1000 == 0) {
                    System.out.println(count);
                }
                Sentence sentence = new Sentence();
                boolean done = simpleSpellChecker.forcedShortcutSplitCheck(new Word(word), sentence);
                if (done) {
                    output1.println(word + "\t" + sentence.toString());
                } else {
                    sentence = new Sentence();
                    done = simpleSpellChecker.forcedDeDaSplitCheck(new Word(word), sentence);
                    if (done) {
                        output1.println(word + "\t" + sentence.toString());
                    } else {
                        sentence = new Sentence();
                        done = simpleSpellChecker.forcedQuestionSuffixSplitCheck(new Word(word), sentence);
                        if (done) {
                            output1.println(word + "\t" + sentence.toString());
                        } else {
                            output2.println(word);
                        }
                    }
                }
            }
            input.close();
            output1.close();
            output2.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}