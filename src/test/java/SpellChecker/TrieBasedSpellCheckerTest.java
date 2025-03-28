package SpellChecker;

import Corpus.Sentence;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import Ngram.NGram;
import Ngram.NoSmoothing;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TrieBasedSpellCheckerTest {
    FsmMorphologicalAnalyzer fsm;
    NGram<String> nGram;
    SpellCheckerParameter spellCheckerParameter;

    @Before
    public void setUp() {
        fsm = new FsmMorphologicalAnalyzer();
        nGram = new NGram<String>("ngram.txt");
        nGram.calculateNGramProbabilities(new NoSmoothing<>());
        spellCheckerParameter = new SpellCheckerParameter();
    }

    @Test
    public void testSpellCheck() {
        Sentence[] original = {
                new Sentence("antibiyotik direnci bir mikroorganizmanın antibiyotiklerin etkilerine karşı durabilme yeteneğidir"),
                new Sentence("bugünkü ortaöğretim sisteminin oluşumu Cumhuriyet döneminde gerçekleşmiştir"),
                new Sentence("bilinçsizce dağıtılan kredilerin sorun yaratmayacağını düşünmelerinin nedeni bankaların büyüklüğünden kaynaklanıyordu"),
                new Sentence("Yemen'de ekonomik durgunluk nedeniyle yeni bir insani kriz yaşanabileceği uyarısı yapıldı"),
                new Sentence("hafif ticari araç bariyerlere çarptı"),
                new Sentence("sırtıkara adındaki canlı , bir balıktır"),
                new Sentence("siyah ayı , ayıgiller familyasına ait bir ayı türüdür"),
                new Sentence("alışveriş için markete gitti"),
                new Sentence("küçük bir yalıçapkını geçti"),
                new Sentence("yeni yılın sonrasında vakalarda artış oldu"),
                new Sentence("atomik saatin 10 mhz sinyali kalibrasyon hizmetlerinde referans olarak kullanılmaktadır"),
                new Sentence("rehberimiz bu bölgedeki çıngıraklı yılan varlığı hakkında konuştu"),
                new Sentence("bu haksızlık da unutulup gitmişti"),
                new Sentence("4'lü tahıl zirvesi İstanbul'da gerçekleşti"),
                new Sentence("10'luk sistemden 100'lük sisteme geçiş yapılacak"),
                new Sentence("play-off maçlarına çıkacak takımlar belli oldu"),
                new Sentence("bu son model cihaz 24 inç ekran büyüklüğünde ve 9 kg ağırlıktadır")};
        Sentence[] modified = {
                new Sentence("antibiodik direnci bir mikrorkanismanın antibiyotiklerin etkilerine karşı durabilme yeteneğidir"),
                new Sentence("bugünki ortögretim sisteminin oluşumu Cumhuriyet dönmeinde gerçekleşmiştir"),
                new Sentence("billinçiszce dağıtılan kredilerin sorun yaratmayacağını düşünmelerinin nedeni bankaların büyüklüğünden kaynaklanıyordu"),
                new Sentence("Yemen'de ekonomik durrğunlk nedeniyle yeni bir insani kriz yaşanabileceği uyaarisi yapıldı"),
                new Sentence("hafif ricarii araç aryerlere çarptı"),
                new Sentence("sırtı kara adındaki canlı , bir balıktır"),
                new Sentence("siyahayı , ayıgiller familyasına ait bir ayı türüdür"),
                new Sentence("alis veriş için markete gitit"),
                new Sentence("kucuk bri yalı çapkını gecti"),
                new Sentence("yeniyılın sonrasında vakalarda artış oldu"),
                new Sentence("atomik saatin 10mhz sinyali kalibrasyon hizmetlerinde referans olarka kullanılmaktadır"),
                new Sentence("rehperimiz buı bölgedeki çıngıraklıyılan varlıgı hakkınd konustu"),
                new Sentence("bu haksızlıkda unutulup gitmişti"),
                new Sentence("4 lı tahıl zirvesi İstanbul'da gerçekleşti"),
                new Sentence("10 lük sistemden 100 lık sisteme geçiş yapılacak"),
                new Sentence("play - off maçlarına çıkacak takımlar belli oldu"),
                new Sentence("bu son model ciha 24inç ekran büyüklüğünde ve 9kg ağırlıktadır")};
        TrieBasedSpellChecker trieBasedSpellChecker = new TrieBasedSpellChecker(fsm, nGram, spellCheckerParameter);
        for (int i = 0; i < modified.length; i++){
            assertEquals(original[i].toString(), trieBasedSpellChecker.spellCheck(modified[i]).toString());
        }
    }

    @Test
    public void testSpellCheckSurfaceForm() {
        spellCheckerParameter.setRootNGram(false);
        TrieBasedSpellChecker trieBasedSpellChecker = new TrieBasedSpellChecker(fsm, nGram, spellCheckerParameter);
        assertEquals("noter hakkında", trieBasedSpellChecker.spellCheck(new Sentence("noter hakkınad")).toString());
        assertEquals("arçelik'in çamaşır", trieBasedSpellChecker.spellCheck(new Sentence("arçelik'in çamşaır")).toString());
        assertEquals("ruhsat yanında", trieBasedSpellChecker.spellCheck(new Sentence("ruhset yanında")).toString());
    }
}
