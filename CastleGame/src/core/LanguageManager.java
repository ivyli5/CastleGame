package core;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private Map<String, String> english;
    private Map<String, String> spanish;
    private Map<String, String> currentLanguage;
    private boolean inEnglish;

    public LanguageManager() {
        english = new HashMap<>();
        spanish = new HashMap<>();
        loadLanguages();
        setLanguage("English");  // Default to English
        inEnglish = true;
    }

    private void loadLanguages() {
        // English
        english.put("title", "CS61B: The Game <3");
        english.put("new_game", "New Game (N)");
        english.put("load_game", "Load Game (L)");
        english.put("quit", "Quit (Q)");
        english.put("change_language", "Change Language to Spanish (C)");

        // Spanish
        spanish.put("title", "CS61B: El Juego <3");
        spanish.put("new_game", "Nuevo Juego (N)");
        spanish.put("load_game", "Cargar Juego (L)");
        spanish.put("quit", "Salir (Q)");
        spanish.put("change_language", "Cambiar Idioma a Ingles (C)");
    }

    public void setLanguage(String language) {
        if ("Spanish".equals(language)) {
            currentLanguage = spanish;
            inEnglish = false;
        } else {
            currentLanguage = english;
            inEnglish = true;
        }
    }

    public boolean isInEnglish() {
        return inEnglish;
    }

    public String getText(String key) {
        return currentLanguage.getOrDefault(key, "Key not found");
    }
}
