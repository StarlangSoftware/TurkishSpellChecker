package SpellChecker;

import Corpus.Sentence;
import Dictionary.Word;
import Language.TurkishLanguage;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParseList;
import Util.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SimpleSpellChecker implements SpellChecker {
    protected FsmMorphologicalAnalyzer fsm;
    private HashMap<String, String> mergedWords = new HashMap<>();
    private HashMap<String, String> splitWords = new HashMap<>();
    private static final ArrayList<String> shortcuts = new ArrayList<>(Arrays.asList("cc", "cm2", "cm", "gb", "ghz", "gr", "gram", "hz", "inc", "inch", "inç",
            "kg", "kw", "kva", "litre", "lt", "m2", "m3", "mah", "mb", "metre", "mg", "mhz", "ml", "mm", "mp", "ms",
            "mt", "mv", "tb", "tl", "va", "volt", "watt", "ah", "hp"));

    /**
     * The generateCandidateList method takes a String as an input. Firstly, it creates a String consists of lowercase Turkish letters
     * and an {@link ArrayList} candidates. Then, it loops i times where i ranges from 0 to the length of given word. It gets substring
     * from 0 to ith index and concatenates it with substring from i+1 to the last index as a new String called deleted. Then, adds
     * this String to the candidates {@link ArrayList}. Secondly, it loops j times where j ranges from 0 to length of
     * lowercase letters String and adds the jth character of this String between substring of given word from 0 to ith index
     * and the substring from i+1 to the last index, then adds it to the candidates {@link ArrayList}. Thirdly, it loops j
     * times where j ranges from 0 to length of lowercase letters String and adds the jth character of this String between
     * substring of given word from 0 to ith index and the substring from i to the last index, then adds it to the candidates {@link ArrayList}.
     *
     * @param word String input.
     * @return ArrayList candidates.
     */
    private ArrayList<Candidate> generateCandidateList(String word) {
        String s = TurkishLanguage.LOWERCASE_LETTERS;
        ArrayList<Candidate> candidates = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            if (i < word.length() - 1){
                Candidate swapped = new Candidate(word.substring(0, i) + word.charAt(i + 1) + word.charAt(i) + word.substring(i + 2), Operator.SPELL_CHECK);
                candidates.add(swapped);
            }
            if (TurkishLanguage.LETTERS.contains("" + word.charAt(i)) || "wxq".contains("" + word.charAt(i))){
                Candidate deleted = new Candidate(word.substring(0, i) + word.substring(i + 1), Operator.SPELL_CHECK);
                if (!deleted.getCandidate().matches("\\d+")){
                    candidates.add(deleted);
                }
                for (int j = 0; j < s.length(); j++) {
                    Candidate replaced = new Candidate(word.substring(0, i) + s.charAt(j) + word.substring(i + 1), Operator.SPELL_CHECK);
                    candidates.add(replaced);
                }
                for (int j = 0; j < s.length(); j++) {
                    Candidate added = new Candidate(word.substring(0, i) + s.charAt(j) + word.substring(i), Operator.SPELL_CHECK);
                    candidates.add(added);
                }

            }

        }
        return candidates;
    }

    /**
     * The candidateList method takes a {@link Word} as an input and creates a candidates {@link ArrayList} by calling generateCandidateList
     * method with given word. Then, it loops i times where i ranges from 0 to size of candidates {@link ArrayList} and creates a
     * {@link FsmParseList} by calling morphologicalAnalysis with each item of candidates {@link ArrayList}. If the size of
     * {@link FsmParseList} is 0, it then removes the ith item.
     *
     * @param word Word input.
     * @return candidates {@link ArrayList}.
     */
    public ArrayList<Candidate> candidateList(Word word) {
        ArrayList<Candidate> candidates;
        candidates = generateCandidateList(word.getName());
        for (int i = 0; i < candidates.size(); i++) {
            FsmParseList fsmParseList = fsm.morphologicalAnalysis(candidates.get(i).getCandidate());
            if (fsmParseList.size() == 0) {
                String newCandidate = fsm.getDictionary().getCorrectForm(candidates.get(i).getCandidate());
                if (newCandidate != null && fsm.morphologicalAnalysis(newCandidate).size() > 0){
                    candidates.set(i, new Candidate(newCandidate,Operator.MISSPELLED_REPLACE));
                } else {
                    candidates.remove(i);
                    i--;
                }
            }
        }
        return candidates;
    }

    /**
     * A constructor of {@link SimpleSpellChecker} class which takes a {@link FsmMorphologicalAnalyzer} as an input and
     * assigns it to the fsm variable.
     *
     * @param fsm {@link FsmMorphologicalAnalyzer} type input.
     */
    public SimpleSpellChecker(FsmMorphologicalAnalyzer fsm) {
        this.fsm = fsm;
        loadDictionaries();
    }

    /**
     * The spellCheck method takes a {@link Sentence} as an input and loops i times where i ranges from 0 to size of words in given sentence.
     * Then, it calls morphologicalAnalysis method with each word and assigns it to the {@link FsmParseList}, if the size of
     * {@link FsmParseList} is equal to the 0, it adds current word to the candidateList and assigns it to the candidates {@link ArrayList}.
     * if the size of candidates greater than 0, it generates a random number and selects an item from candidates {@link ArrayList} with
     * this random number and assign it as newWord. If the size of candidates is not greater than 0, it directly assigns the
     * current word as newWord. At the end, it adds the newWord to the result {@link Sentence}.
     *
     * @param sentence {@link Sentence} type input.
     * @return Sentence result.
     */
    public Sentence spellCheck(Sentence sentence){
        Word word, newWord;
        int randomCandidate;
        Random random = new Random();
        ArrayList<Candidate> candidates;
        Sentence result = new Sentence();

        for (int repeat = 0; repeat < 2; repeat++) {
            for (int i = 0; i < sentence.wordCount(); i++) {

                word = sentence.getWord(i);
                Word nextWord = null;
                Word previousWord = null;

                if (i > 0){
                    previousWord = sentence.getWord(i - 1);
                }
                if (i < sentence.wordCount() - 1){
                    nextWord = sentence.getWord(i + 1);
                }

                if (forcedMisspellCheck(word, result) || forcedBackwardMergeCheck(word, result, previousWord)){
                    continue;
                }
                if (forcedForwardMergeCheck(word, result, nextWord)){
                    i++;
                    continue;
                }
                if (forcedSplitCheck(word, result) || forcedShortcutCheck(word, result, previousWord)){
                    continue;
                }

                FsmParseList fsmParseList = fsm.morphologicalAnalysis(word.getName());
                if (fsmParseList.size() == 0) {
                    candidates = candidateList(word);
                    if (candidates.size() < 1) {
                        candidates.addAll(mergedCandidatesList(previousWord, word, nextWord));
                    }
                    if (candidates.size() < 1) {
                        candidates.addAll(splitCandidatesList(word));
                    }
                    if (candidates.size() > 0) {
                        randomCandidate = random.nextInt(candidates.size());
                        newWord = new Word(candidates.get(randomCandidate).getCandidate());

                        if (candidates.get(randomCandidate).getOperator() == Operator.BACKWARD_MERGE){
                            result.replaceWord(i - 1, newWord);
                            continue;
                        }
                        if (candidates.get(randomCandidate).getOperator() == Operator.FORWARD_MERGE){
                            i++;
                        }
                    } else {
                        newWord = word;
                    }
                } else {
                    newWord = word;
                }
                result.addWord(newWord);
            }
            sentence = new Sentence(result.toString());
            if (repeat < 1){
                result = new Sentence();
            }
        }
        return result;
    }

    protected boolean forcedMisspellCheck(Word word, Sentence result){
        String forcedReplacement = fsm.getDictionary().getCorrectForm(word.getName());
        if (forcedReplacement != null){
            result.addWord(new Word(forcedReplacement));
            return true;
        }
        return false;
    }

    protected boolean forcedBackwardMergeCheck(Word word, Sentence result, Word previousWord){
        if (previousWord != null){
            String forcedReplacement = getCorrectForm(result.getWord(result.wordCount() - 1) + " " + word.getName(), mergedWords);
            if (forcedReplacement != null) {
                result.replaceWord(result.wordCount() - 1, new Word(forcedReplacement));
                return true;
            }
        }
        return false;
    }

    protected boolean forcedForwardMergeCheck(Word word, Sentence result, Word nextWord){
        if (nextWord != null){
            String forcedReplacement = getCorrectForm(word.getName() + " " + nextWord.getName(), mergedWords);
            if (forcedReplacement != null) {
                result.addWord(new Word(forcedReplacement));
                return true;
            }
        }
        return false;
    }

    protected boolean forcedSplitCheck(Word word, Sentence result){
        String forcedReplacement = getCorrectForm(word.getName(), splitWords);
        if (forcedReplacement != null){
            result.addWord(new Word(forcedReplacement));
            return true;
        }
        return false;
    }

    protected boolean forcedShortcutCheck(Word word, Sentence result, Word previousWord){
        String forcedReplacement;
        String shortcutRegex = "[0-9]+(" + shortcuts.get(0);
        for (int i = 1; i < shortcuts.size(); i++){
            shortcutRegex += "|" + shortcuts.get(i);
        }
        shortcutRegex += ")";

        if (shortcuts.contains(word.getName()) && previousWord.getName().matches("[0-9]+")){
            result.addWord(word);
            return true;
        }

        if ((word.getName().matches(shortcutRegex))){
            String[] pair = getSplitPair(word);
            forcedReplacement = pair[0] + " " + pair[1];
            result.addWord(new Word(forcedReplacement));
            return true;
        }
        return false;
    }

    public ArrayList<Candidate> mergedCandidatesList(Word previousWord, Word word, Word nextWord) {
        ArrayList<Candidate> mergedCandidates = new ArrayList<>();
        Candidate backwardMergeCandidate = null;
        Candidate forwardMergeCandidate = null;

        if (previousWord != null){
            backwardMergeCandidate = new Candidate(previousWord.getName() + word.getName(), Operator.BACKWARD_MERGE);
            mergedCandidates.add(backwardMergeCandidate);
        }
        if (nextWord != null){
            forwardMergeCandidate = new Candidate(word.getName() + nextWord.getName(), Operator.FORWARD_MERGE);
            if (backwardMergeCandidate == null || !(backwardMergeCandidate.getCandidate().equals(forwardMergeCandidate.getCandidate()))){
                mergedCandidates.add(forwardMergeCandidate);
            }
        }

        for (int i = 0; i < mergedCandidates.size(); i++) {
            FsmParseList fsmParseList = fsm.morphologicalAnalysis(mergedCandidates.get(i).getCandidate());
            if (fsmParseList.size() == 0) {
                mergedCandidates.remove(i);
                i--;
            }
        }
        return mergedCandidates;
    }

    public ArrayList<Candidate> splitCandidatesList(Word word) {
        ArrayList<Candidate> splitCandidates = new ArrayList<>();
        for (int i = 4; i < word.getName().length() - 3; i++) {
            String firstPart = word.getName().substring(0, i);
            String secondPart = word.getName().substring(i);
            FsmParseList fsmParseListFirst = fsm.morphologicalAnalysis(firstPart);
            FsmParseList fsmParseListSecond = fsm.morphologicalAnalysis(secondPart);
            if (fsmParseListFirst.size() > 0 && fsmParseListSecond.size() > 0){
                splitCandidates.add(new Candidate(firstPart + " " + secondPart, Operator.SPLIT));
            }
        }
        return splitCandidates;
    }

    private void loadDictionaries() {
        String line;
        String[] list;
        try{
            BufferedReader mergedReader = new BufferedReader(new InputStreamReader(FileUtils.getInputStream("merged.txt"), StandardCharsets.UTF_8));
            BufferedReader splitReader = new BufferedReader(new InputStreamReader(FileUtils.getInputStream("split.txt"), StandardCharsets.UTF_8));

            line = mergedReader.readLine();
            while (line != null) {
                list = line.split(" ");
                mergedWords.put(list[0] + " " + list[1], list[2]);
                line = mergedReader.readLine();
            }

            line = splitReader.readLine();
            while (line != null) {
                list = line.split(" ");
                splitWords.put(list[0], list[1] + " " + list[2]);
                line = splitReader.readLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getCorrectForm(String wordName, HashMap<String, String> dictionary){
        if (dictionary.containsKey(wordName)){
            return dictionary.get(wordName);
        }
        return null;
    }

    private String[] getSplitPair(Word word){
        String[] result = new String[2];
        result[0] = "";
        int j;
        for (j = 0; j < word.getName().length(); j++){
            if (word.getName().charAt(j) >= '0' && word.getName().charAt(j) <= '9') {
                result[0] += word.getName().charAt(j);
            } else {
                break;
            }
        }
        result[1] = word.getName().substring(j);
        return result;
    }
}
