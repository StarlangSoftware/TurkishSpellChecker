package SpellChecker;

import Corpus.Sentence;

public interface SpellChecker {

    /**
     * The spellCheck method which takes a {@link Sentence} as an input.
     *
     * @param sentence {@link Sentence} type input.
     * @return Sentence result.
     */
    Sentence spellCheck(Sentence sentence);
}
