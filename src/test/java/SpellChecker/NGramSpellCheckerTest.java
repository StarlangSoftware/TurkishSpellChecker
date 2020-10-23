package SpellChecker;

import Corpus.Sentence;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import Ngram.NGram;
import Ngram.NoSmoothing;
import org.junit.Test;

import static org.junit.Assert.*;

public class NGramSpellCheckerTest {

    @Test
    public void testSpellCheck() {
        Sentence[] original = {new Sentence("demokratik cumhuriyet en kıymetli varlığımızdır"),
                new Sentence("bu tablodaki değerler zedelenmeyecektir"),
                new Sentence("milliyet'in geleneksel yılın sporcusu anketi 43. yaşını doldurdu"),
                new Sentence("demokrasinin icadı bu ayrımı bulandırdı"),
                new Sentence("dışişleri müsteşarı Öymen'in 1997'nin ilk aylarında Bağdat'a gitmesi öngörülüyor"),
                new Sentence("büyüdü , palazlandı , devleti ele geçirdi"),
                new Sentence("her maskenin ciltte kalma süresi farklıdır"),
                new Sentence("yılın son ayında 10 gazeteci gözaltına alındı"),
                new Sentence("iki pilotun kullandığı uçakta bir hostes görev alıyor"),
                new Sentence("son derece kısıtlı kelimeler çerçevesinde kendilerini uzun cümlelerle ifade edebiliyorlar")};
        Sentence[] modified = {new Sentence("demokratik cumhüriyet en kımetli varlıgımızdır"),
                new Sentence("bu tblodaki değerlğr zedelenmeyecüktir"),
                new Sentence("milliyet'in geeneksel yılın spoşcusu ankşti 43. yeşını doldürdu"),
                new Sentence("demokrasinin icşdı bu ayrmıı bulandürdı"),
                new Sentence("dışişleri mütseşarı Öymen'in 1997'nin ilk aylğrında Bağdat'a gitmesi öngşrülüyor"),
                new Sentence("büyüdü , palazandı , devltei ele geçridi"),
                new Sentence("her makenin cültte kalma sürdsi farlkıdır"),
                new Sentence("yılın sno ayında 10 gazteci gözlatına alündı"),
                new Sentence("iki piotun kulçandığı uçkata üir hotes görçv alyıor"),
                new Sentence("son deece kısütlı keilmeler çeçevesinde kendülerini uzuü cümllerle ifüde edbeiliyorlar")};
        FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer();
        NGram<String> nGram = new NGram<String>("ngram.txt");
        nGram.calculateNGramProbabilities(new NoSmoothing<>());
        NGramSpellChecker nGramSpellChecker = new NGramSpellChecker(fsm, nGram);
        for (int i = 0; i < modified.length; i++){
            assertEquals(original[i].toString(), nGramSpellChecker.spellCheck(modified[i]).toString());
        }
    }

    @Test
    public void testSpellCheck2() {
        FsmMorphologicalAnalyzer fsm = new FsmMorphologicalAnalyzer();
        NGram<String> nGram = new NGram<String>("ngram.txt");
        nGram.calculateNGramProbabilities(new NoSmoothing<>());
        NGramSpellChecker nGramSpellChecker = new NGramSpellChecker(fsm, nGram);
        assertEquals("kedi köpek", nGramSpellChecker.spellCheck(new Sentence("krdi köpek")).toString());
        assertEquals("minibüs durağı", nGramSpellChecker.spellCheck(new Sentence("minibü durağı")).toString());
        assertEquals("noter belgesi", nGramSpellChecker.spellCheck(new Sentence("nter belgesi")).toString());
        assertEquals("ev telefonu", nGramSpellChecker.spellCheck(new Sentence("rv telefonu")).toString());
        assertEquals("kitap okudum", nGramSpellChecker.spellCheck(new Sentence("krtap okudum")).toString());
    }
}