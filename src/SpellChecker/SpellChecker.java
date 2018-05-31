package SpellChecker;

import Corpus.Sentence;

public interface SpellChecker {

    Sentence spellCheck(Sentence sentence);
}
