package SpellChecker;

import Corpus.Sentence;
import Dictionary.Word;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParseList;
import Ngram.NGram;

import java.util.ArrayList;

public class NGramSpellChecker extends SimpleSpellChecker{
    private NGram nGram;

    public NGramSpellChecker(FsmMorphologicalAnalyzer fsm, NGram nGram){
        super(fsm);
        this.nGram = nGram;
    }

    public Sentence spellCheck(Sentence sentence) {
        Word word, bestRoot;
        Word previousRoot = null, root;
        String bestCandidate;
        FsmParseList fsmParses;
        double probability, bestProbability;
        ArrayList<String> candidates;
        Sentence result = new Sentence();
        for (int i = 0; i < sentence.wordCount(); i++){
            word = sentence.getWord(i);
            fsmParses = fsm.morphologicalAnalysis(word.getName());
            if (fsmParses.size() == 0){
                candidates = candidateList(word);
                bestCandidate = null;
                bestRoot = null;
                bestProbability = 0;
                for (String candidate : candidates){
                    fsmParses = fsm.morphologicalAnalysis(candidate);
                    root = fsmParses.getFsmParse(0).getWord();
                    if (previousRoot != null){
                        probability = nGram.getProbability(previousRoot, root);
                    } else {
                        probability = nGram.getProbability(root);
                    }
                    if (probability > bestProbability){
                        bestCandidate = candidate;
                        bestRoot = root;
                        bestProbability = probability;
                    }
                }
                previousRoot = bestRoot;
                result.addWord(new Word(bestCandidate));
            } else {
                result.addWord(word);
                previousRoot = fsmParses.getFsmParse(0).getWord();
            }
        }
        return result;
    }
}
