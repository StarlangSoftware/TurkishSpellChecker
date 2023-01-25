package SpellChecker;

import Corpus.Sentence;
import Dictionary.TxtWord;
import Dictionary.Word;
import Language.TurkishLanguage;
import MorphologicalAnalysis.FsmMorphologicalAnalyzer;
import MorphologicalAnalysis.FsmParseList;
import Util.FileUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SimpleSpellChecker implements SpellChecker {
    protected FsmMorphologicalAnalyzer fsm;
    private HashMap<String, String> mergedWords = new HashMap<>();
    private HashMap<String, String> splitWords = new HashMap<>();
    private static final ArrayList<String> shortcuts = new ArrayList<>(Arrays.asList("cc", "cm2", "cm", "gb", "ghz", "gr", "gram", "hz", "inc", "inch", "inç",
            "kg", "kw", "kva", "litre", "lt", "m2", "m3", "mah", "mb", "metre", "mg", "mhz", "ml", "mm", "mp", "ms", "kb", "mb", "gb", "tb", "pb", "kbps",
            "mt", "mv", "tb", "tl", "va", "volt", "watt", "ah", "hp", "oz", "rpm", "dpi", "ppm", "ohm", "kwh", "kcal", "kbit", "mbit", "gbit", "bit", "byte",
            "mbps", "gbps", "cm3", "mm2", "mm3", "khz", "ft", "db", "sn"));
    private static final ArrayList<String> conditionalShortcuts = new ArrayList<>(Arrays.asList("g", "v", "m", "l", "w", "s"));
    private static final ArrayList<String> questionSuffixList = new ArrayList<>(Arrays.asList("mi", "mı", "mu", "mü", "miyim", "misin", "miyiz", "midir",
            "miydi", "mıyım", "mısın", "mıyız", "mıdır", "mıydı", "muyum", "musun", "muyuz", "mudur", "muydu", "müyüm", "müsün", "müyüz", "müdür", "müydü",
            "miydim", "miydin", "miydik", "miymiş", "mıydım", "mıydın", "mıydık", "mıymış", "muydum", "muydun", "muyduk", "muymuş", "müydüm", "müydün",
            "müydük", "müymüş", "misiniz", "mısınız", "musunuz", "müsünüz", "miyimdir", "misindir", "miyizdir", "miydiniz", "miydiler", "miymişim", "miymişiz",
            "mıyımdır", "mısındır", "mıyızdır", "mıydınız", "mıydılar", "mıymışım", "mıymışız", "muyumdur", "musundur", "muyuzdur", "muydunuz", "muydular",
            "muymuşum", "muymuşuz", "müyümdür", "müsündür", "müyüzdür", "müydünüz", "müydüler", "müymüşüm", "müymüşüz", "miymişsin", "miymişler", "mıymışsın",
            "mıymışlar", "muymuşsun", "muymuşlar", "müymüşsün", "müymüşler", "misinizdir", "mısınızdır", "musunuzdur", "müsünüzdür"));

    /**
     * A constructor of {@link SimpleSpellChecker} class which takes a {@link FsmMorphologicalAnalyzer} as an input,
     * assigns it to the fsm variable and calls the loadDictionaries method.
     *
     * @param fsm {@link FsmMorphologicalAnalyzer} type input.
     */
    public SimpleSpellChecker(FsmMorphologicalAnalyzer fsm) {
        this.fsm = fsm;
        loadDictionaries();
    }

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
            if (TurkishLanguage.LETTERS.contains("" + word.charAt(i)) || "wxq".contains("" + word.charAt(i))) {
                Candidate deleted = new Candidate(word.substring(0, i) + word.substring(i + 1), Operator.SPELL_CHECK);
                if (!deleted.getName().matches("\\d+")){
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
    protected ArrayList<Candidate> candidateList(Word word, Sentence sentence) {
        ArrayList<Candidate> candidates;
        candidates = generateCandidateList(word.getName());
        for (int i = 0; i < candidates.size(); i++) {
            FsmParseList fsmParseList = fsm.morphologicalAnalysis(candidates.get(i).getName());
            if (fsmParseList.size() == 0) {
                String newCandidate = fsm.getDictionary().getCorrectForm(candidates.get(i).getName());
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
    public Sentence spellCheck(Sentence sentence) {
        Word word, newWord;
        int randomCandidate;
        Random random = new Random();
        ArrayList<Candidate> candidates;
        Sentence result = new Sentence();
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
            if (forcedMisspellCheck(word, result) || forcedBackwardMergeCheck(word, result, previousWord) || forcedSuffixMergeCheck(word, result, previousWord)) {
                continue;
            }
            if (forcedForwardMergeCheck(word, result, nextWord) || forcedHyphenMergeCheck(word, result, previousWord, nextWord)) {
                i++;
                continue;
            }
            if (forcedSplitCheck(word, result) || forcedShortcutSplitCheck(word, result) || forcedDeDaSplitCheck(word, result) || forcedQuestionSuffixSplitCheck(word, result)) {
                continue;
            }
            FsmParseList fsmParseList = fsm.morphologicalAnalysis(word.getName());
            FsmParseList upperCaseFsmParseList = fsm.morphologicalAnalysis(Word.toCapital(word.getName()));
            if (fsmParseList.size() == 0 && upperCaseFsmParseList.size() == 0) {
                candidates = mergedCandidatesList(previousWord, word, nextWord);
                if (candidates.size() < 1) {
                    candidates = candidateList(word, sentence);
                }
                if (candidates.size() < 1) {
                    candidates.addAll(splitCandidatesList(word));
                }
                if (candidates.size() > 0) {
                    randomCandidate = random.nextInt(candidates.size());
                    newWord = new Word(candidates.get(randomCandidate).getName());
                    if (candidates.get(randomCandidate).getOperator() == Operator.BACKWARD_MERGE){
                        result.replaceWord(result.wordCount() - 1, newWord);
                        continue;
                    }
                    if (candidates.get(randomCandidate).getOperator() == Operator.FORWARD_MERGE){
                        i++;
                    }
                    if (candidates.get(randomCandidate).getOperator() == Operator.SPLIT){
                        addSplitWords(candidates.get(randomCandidate).getName(), result);
                        continue;
                    }
                } else {
                    newWord = word;
                }
            } else {
                newWord = word;
            }
            result.addWord(newWord);
        }
        return result;
    }

    /**
     * Checks if the given word is a misspelled word according to the misspellings list,
     * and if it is, then replaces it with its correct form in the given sentence.
     *
     * @param word the word to check for misspelling
     * @param result the sentence that the word belongs to
     *
     * @return true if the word was corrected, false otherwise
     */
    protected boolean forcedMisspellCheck(Word word, Sentence result) {
        String forcedReplacement = fsm.getDictionary().getCorrectForm(word.getName());
        if (forcedReplacement != null){
            result.addWord(new Word(forcedReplacement));
            return true;
        }
        return false;
    }

    /**
     * Checks if the given word and its preceding word need to be merged according to the merged list.
     * If the merge is needed, the word and its preceding word are replaced with their merged form in the given sentence.
     *
     * @param word the word to check for merge
     * @param result the sentence that the word belongs to
     * @param previousWord the preceding word of the given word
     *
     * @return true if the word was merged, false otherwise
     */
    protected boolean forcedBackwardMergeCheck(Word word, Sentence result, Word previousWord) {
        if (previousWord != null){
            String forcedReplacement = getCorrectForm(result.getWord(result.wordCount() - 1).getName() + " " + word.getName(), mergedWords);
            if (forcedReplacement != null) {
                result.replaceWord(result.wordCount() - 1, new Word(forcedReplacement));
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given word and its next word need to be merged according to the merged list.
     * If the merge is needed, the word and its next word are replaced with their merged form in the given sentence.
     *
     * @param word the word to check for merge
     * @param result the sentence that the word belongs to
     * @param nextWord the next word of the given word
     *
     * @return true if the word was merged, false otherwise
     */
    protected boolean forcedForwardMergeCheck(Word word, Sentence result, Word nextWord) {
        if (nextWord != null){
            String forcedReplacement = getCorrectForm(word.getName() + " " + nextWord.getName(), mergedWords);
            if (forcedReplacement != null) {
                result.addWord(new Word(forcedReplacement));
                return true;
            }
        }
        return false;
    }

    /**
     * Given a multiword form, splits it and adds it to the given sentence.
     *
     * @param multiWord multiword form to split
     * @param result the sentence to add the split words to
     */
    protected void addSplitWords(String multiWord, Sentence result) {
        String[] words = multiWord.split(" ");
        result.addWord(new Word(words[0]));
        result.addWord(new Word(words[1]));
    }

    /**
     * Checks if the given word needs to be split according to the split list.
     * If the split is needed, the word is replaced with its split form in the given sentence.
     *
     * @param word the word to check for split
     * @param result the sentence that the word belongs to
     *
     * @return true if the word was split, false otherwise
     */
    protected boolean forcedSplitCheck(Word word, Sentence result) {
        String forcedReplacement = getCorrectForm(word.getName(), splitWords);
        if (forcedReplacement != null){
            addSplitWords(forcedReplacement, result);
            return true;
        }
        return false;
    }

    /**
     * Checks if the given word is a shortcut form, such as "5kg" or "2.5km".
     * If it is, it splits the word into its number and unit form and adds them to the given sentence.
     *
     * @param word the word to check for shortcut split
     * @param result the sentence that the word belongs to
     *
     * @return true if the word was split, false otherwise
     */
    protected boolean forcedShortcutSplitCheck(Word word, Sentence result) {
        StringBuilder shortcutRegex = new StringBuilder("(([1-9][0-9]*)|[0])(([.]|[,])[0-9]*)?(" + shortcuts.get(0));
        for (int i = 1; i < shortcuts.size(); i++){
            shortcutRegex.append("|").append(shortcuts.get(i));
        }
        shortcutRegex.append(")");

        StringBuilder conditionalShortcutRegex = new StringBuilder("(([1-9][0-9]{0,2})|[0])(([.]|[,])[0-9]*)?(" + conditionalShortcuts.get(0));
        for (int i = 1; i < conditionalShortcuts.size(); i++){
            conditionalShortcutRegex.append("|").append(conditionalShortcuts.get(i));
        }
        conditionalShortcutRegex.append(")");

        if (word.getName().matches(shortcutRegex.toString()) || word.getName().matches(conditionalShortcutRegex.toString())) {
            AbstractMap.SimpleEntry<String, String> pair = getSplitPair(word);
            result.addWord(new Word(pair.getKey()));
            result.addWord(new Word(pair.getValue()));
            return true;
        }
        return false;
    }

    /**
     * Checks if the given word has a "da" or "de" suffix that needs to be split according to a predefined set of rules.
     * If the split is needed, the word is replaced with its bare form and "da" or "de" in the given sentence.
     *
     * @param word the word to check for "da" or "de" split
     * @param result the sentence that the word belongs to
     *
     * @return true if the word was split, false otherwise
     */
    protected boolean forcedDeDaSplitCheck(Word word, Sentence result) {
        String wordName = word.getName();
        String capitalizedWordName = Word.toCapital(wordName);
        TxtWord txtWord = null;
        if (wordName.endsWith("da") || wordName.endsWith("de")) {
            if (fsm.morphologicalAnalysis(wordName).size() == 0 && fsm.morphologicalAnalysis(capitalizedWordName).size() == 0) {
                String newWordName = wordName.substring(0, wordName.length() - 2);
                FsmParseList fsmParseList = fsm.morphologicalAnalysis(newWordName);
                TxtWord txtNewWord = (TxtWord)fsm.getDictionary().getWord(newWordName.toLowerCase(new Locale("tr", "TR")));
                if (txtNewWord != null && txtNewWord.isProperNoun()) {
                    if (fsm.morphologicalAnalysis(newWordName + "'" + "da").size() > 0) {
                        result.addWord(new Word(newWordName + "'" + "da"));
                    }
                    else {
                        result.addWord(new Word(newWordName + "'" + "de"));
                    }
                    return true;
                }
                if (fsmParseList.size() > 0) {
                    txtWord = (TxtWord)fsm.getDictionary().getWord(fsmParseList.getParseWithLongestRootWord().getWord().getName());
                }
                if (txtWord != null && !txtWord.isCode()) {
                    result.addWord(new Word(newWordName));
                    if (TurkishLanguage.isBackVowel(Word.lastVowel(newWordName))) {
                        if (txtWord.notObeysVowelHarmonyDuringAgglutination()) {
                            result.addWord(new Word("de"));
                        }
                        else {
                            result.addWord(new Word("da"));
                        }
                    } else {
                        if (txtWord.notObeysVowelHarmonyDuringAgglutination()) {
                            result.addWord(new Word("da"));
                        }
                        else {
                            result.addWord(new Word("de"));
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the given word is a suffix like 'li' or 'lik' that needs to be merged with its preceding word which is a number.
     * If the merge is needed, the word and its preceding word are replaced with their merged form in the given sentence.
     *
     * @param word the word to check for merge
     * @param sentence the sentence that the word belongs to
     * @param previousWord the preceding word of the given word
     *
     * @return true if the word was merged, false otherwise
     */
    protected boolean forcedSuffixMergeCheck(Word word, Sentence sentence, Word previousWord) {
        ArrayList<String> liList = new ArrayList<>(Arrays.asList("li", "lı", "lu", "lü"));
        ArrayList<String> likList = new ArrayList<>(Arrays.asList("lik", "lık", "luk", "lük"));
        if (liList.contains(word.getName()) || likList.contains(word.getName())) {
            if (previousWord != null && previousWord.getName().matches("[0-9]+")) {
                for (String suffix: liList) {
                    if (word.getName().length() == 2 && fsm.morphologicalAnalysis(previousWord.getName() + "'" + suffix).size() > 0) {
                        sentence.replaceWord(sentence.wordCount() - 1, new Word(previousWord.getName() + "'" + suffix));
                        return true;
                    }
                }
                for (String suffix: likList) {
                    if (word.getName().length() == 3 && fsm.morphologicalAnalysis(previousWord.getName() + "'" + suffix).size() > 0) {
                        sentence.replaceWord(sentence.wordCount() - 1, new Word(previousWord.getName() + "'" + suffix));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the next word and the previous word can be merged if the current word is a hyphen,
     * an en-dash or an em-dash.
     * If the previous word and the next word exist and they are valid words,
     * it merges the previous word and the next word into a single word and add the new word to the sentence
     * If the merge is valid, it returns true.
     * @param word current word
     * @param result the sentence that the word belongs to
     * @param previousWord the word before current word
     * @param nextWord the word after current word
     * @return true if merge is valid, false otherwise
     */
    protected boolean forcedHyphenMergeCheck(Word word, Sentence result, Word previousWord, Word nextWord) {
        if (word.getName().equals("-") || word.getName().equals("–") || word.getName().equals("—")) {
            if (previousWord != null && nextWord != null && previousWord.getName().matches("[a-zA-ZçöğüşıÇÖĞÜŞİ]+") && nextWord.getName().matches("[a-zA-ZçöğüşıÇÖĞÜŞİ]+")) {
                String newWordName = previousWord.getName() + "-" + nextWord.getName();
                if (fsm.morphologicalAnalysis(newWordName).size() > 0) {
                    result.replaceWord(result.wordCount() - 1, new Word(newWordName));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the current word ends with a valid question suffix and split it if it does.
     * It splits the word with the question suffix and adds the two new words to the sentence.
     * If the split is valid, it returns true.
     * @param word current word
     * @param result the sentence that the word belongs to
     * @return true if split is valid, false otherwise
     */
    protected boolean forcedQuestionSuffixSplitCheck(Word word, Sentence result) {
        String wordName = word.getName();
        if (fsm.morphologicalAnalysis(wordName).size() > 0) {
            return false;
        }
        for (String questionSuffix: questionSuffixList) {
            if (wordName.endsWith(questionSuffix)) {
                String newWordName = wordName.substring(0, wordName.lastIndexOf(questionSuffix));
                TxtWord txtWord = (TxtWord)fsm.getDictionary().getWord(newWordName);
                if (fsm.morphologicalAnalysis(newWordName).size() > 0 && txtWord != null && !txtWord.isCode()) {
                    result.addWord(new Word(newWordName));
                    result.addWord(new Word(questionSuffix));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Generates a list of merged candidates for the word and previous and next words.
     *
     * @param previousWord The previous word in the sentence.
     * @param word The word currently being checked.
     * @param nextWord The next word in the sentence.
     * @return A list of merged candidates.
     */
    protected ArrayList<Candidate> mergedCandidatesList(Word previousWord, Word word, Word nextWord) {
        ArrayList<Candidate> mergedCandidates = new ArrayList<>();
        Candidate backwardMergeCandidate = null;
        Candidate forwardMergeCandidate;
        if (previousWord != null){
            backwardMergeCandidate = new Candidate(previousWord.getName() + word.getName(), Operator.BACKWARD_MERGE);
            FsmParseList fsmParseList = fsm.morphologicalAnalysis(backwardMergeCandidate.getName());
            if (fsmParseList.size() != 0){
                mergedCandidates.add(backwardMergeCandidate);
            }
        }
        if (nextWord != null){
            forwardMergeCandidate = new Candidate(word.getName() + nextWord.getName(), Operator.FORWARD_MERGE);
            if (backwardMergeCandidate == null || !(backwardMergeCandidate.getName().equals(forwardMergeCandidate.getName()))){
                FsmParseList fsmParseList = fsm.morphologicalAnalysis(forwardMergeCandidate.getName());
                if (fsmParseList.size() != 0){
                    mergedCandidates.add(forwardMergeCandidate);
                }
            }
        }
        return mergedCandidates;
    }

    /**
     * Generates a list of split candidates for the given word.
     *
     * @param word The word currently being checked.
     * @return A list of split candidates.
     */
    protected ArrayList<Candidate> splitCandidatesList(Word word) {
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

    /**
     * Loads the merged and split lists from the specified files.
     */
    protected void loadDictionaries() {
        String line;
        String[] list;
        StringBuilder result;
        try {
            BufferedReader mergedReader = new BufferedReader(new InputStreamReader(FileUtils.getInputStream("merged.txt"), StandardCharsets.UTF_8));
            BufferedReader splitReader = new BufferedReader(new InputStreamReader(FileUtils.getInputStream("split.txt"), StandardCharsets.UTF_8));
            line = mergedReader.readLine();
            while (line != null) {
                list = line.split(" ");
                mergedWords.put(list[0] + " " + list[1], list[2]);
                line = mergedReader.readLine();
            }
            mergedReader.close();
            line = splitReader.readLine();
            while (line != null) {
                result = new StringBuilder();
                list = line.split(" ");
                for (int i = 1; i < list.length; i++) {
                    result.append(list[i]).append(" ");
                }
                splitWords.put(list[0], result.toString());
                line = splitReader.readLine();
            }
            splitReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getCorrectForm(String wordName, HashMap<String, String> dictionary) {
        if (dictionary.containsKey(wordName)){
            return dictionary.get(wordName);
        }
        return null;
    }

    private AbstractMap.SimpleEntry<String, String> getSplitPair(Word word) {
        String key = "";
        int j;
        for (j = 0; j < word.getName().length(); j++){
            if (word.getName().charAt(j) >= '0' && word.getName().charAt(j) <= '9' || word.getName().charAt(j) == '.' || word.getName().charAt(j) == ',') {
                key += word.getName().charAt(j);
            } else {
                break;
            }
        }
        String value = word.getName().substring(j);
        return new AbstractMap.SimpleEntry<>(key, value);
    }
}
