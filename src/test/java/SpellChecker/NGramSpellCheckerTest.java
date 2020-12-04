package SpellChecker;

import Corpus.Sentence;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import Ngram.NGram;
import Ngram.NoSmoothing;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NGramSpellCheckerTest {
    FsmMorphologicalAnalyzer fsm;
    NGram<String> nGram;

    @Before
    public void setUp(){
        fsm = new FsmMorphologicalAnalyzer();
        nGram = new NGram<String>("ngram.txt");
        nGram.calculateNGramProbabilities(new NoSmoothing<>());
    }

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
                new Sentence("son derece kısıtlı kelimeler çerçevesinde kendilerini uzun cümlelerle ifade edebiliyorlar"),
                new Sentence("kedi köpek"),
                new Sentence("minibüs durağı"),
                new Sentence("noter belgesi"),
                new Sentence("")};
        Sentence[] modified = {new Sentence("demokratik cumhüriyet rn kımetli varlıgımızdır"),
                new Sentence("bu tblodaki değerlğr zedelenmeyecüktir"),
                new Sentence("milliyet'in geeneksel yılin spoşcusu ankşti 43. yeşını doldürdu"),
                new Sentence("demokrasinin icşdı buf ayrmıı bulandürdı"),
                new Sentence("dışişleri mütseşarı Öymen'in 1997'nin iljk aylğrında Bağdat'a gitmesi öngörülüyor"),
                new Sentence("büyüdü , palazandı , devltei eöe geçridi"),
                new Sentence("her makenin cültte aklma sürdsi farlkıdır"),
                new Sentence("yılın sno ayında 10 gazteci gözlatına alündı"),
                new Sentence("iki piotun kulçandığı uçkata üir hotes görçv alyıor"),
                new Sentence("son deece kısütlı keilmeler çeçevesinde kendülerini uzuü cümllerle ifüde edbeiliyorlar"),
                new Sentence("krdi köpek"),
                new Sentence("minibü durağı"),
                new Sentence("ntoer belgesi"),
                new Sentence("")};
        NGramSpellChecker nGramSpellChecker = new NGramSpellChecker(fsm, nGram, true);
        for (int i = 0; i < modified.length; i++){
            assertEquals(original[i].toString(), nGramSpellChecker.spellCheck(modified[i]).toString());
        }
    }

    @Test
    public void testSpellCheckSurfaceForm() {
        NGramSpellChecker nGramSpellChecker = new NGramSpellChecker(fsm, nGram, false);
        assertEquals("noter hakkında", nGramSpellChecker.spellCheck(new Sentence("noter hakkınad")).toString());
        assertEquals("arçelik'in çamaşır", nGramSpellChecker.spellCheck(new Sentence("arçelik'in çamşaır")).toString());
        assertEquals("ruhsat yanında", nGramSpellChecker.spellCheck(new Sentence("ruhset yanında")).toString());
    }

}