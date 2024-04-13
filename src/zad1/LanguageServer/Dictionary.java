package zad1.LanguageServer;

import java.util.HashMap;

public class Dictionary {
    private final String lang;
    private final String fullName;
    private final HashMap<String, String> dict;

    public Dictionary(String lang, String fullName, HashMap<String, String> dict) {
        this.lang = lang;
        this.fullName = fullName;
        this.dict = dict;
    }

    public String getLang() {
        return lang;
    }

    public String getFullName() {
        return fullName;
    }

    public String getTranslation(String word) {
        return dict.getOrDefault(word, null);
    }

    @Override
    public String toString() {
        return "Lang: " + lang+
                "\nFullName: " + fullName+
                "\nDictionary: " + dict.toString();
    }
}