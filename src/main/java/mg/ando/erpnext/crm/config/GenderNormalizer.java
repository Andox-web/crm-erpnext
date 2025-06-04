package mg.ando.erpnext.crm.config;

import java.util.HashMap;
import java.util.Map;

public class GenderNormalizer {

    private static final Map<String, String> GENDER_SYNONYMS = new HashMap<>();
    
    static {    
        // Masculin - Français
        GENDER_SYNONYMS.put("m", "Male");
        GENDER_SYNONYMS.put("Masculin", "Male");
        GENDER_SYNONYMS.put("homme", "Male");
        GENDER_SYNONYMS.put("hommes", "Male");
        GENDER_SYNONYMS.put("garçon", "Male");
        GENDER_SYNONYMS.put("garcons", "Male");
        GENDER_SYNONYMS.put("mâle", "Male");
        GENDER_SYNONYMS.put("male", "Male");

        
        // Masculin - Anglais
        GENDER_SYNONYMS.put("male", "Male");
        GENDER_SYNONYMS.put("man", "Male");
        GENDER_SYNONYMS.put("men", "Male");
        GENDER_SYNONYMS.put("boy", "Male");
        GENDER_SYNONYMS.put("boys", "Male");
        GENDER_SYNONYMS.put("m", "Male");
        
        // Féminin - Français
        GENDER_SYNONYMS.put("f", "Female");
        GENDER_SYNONYMS.put("féminin", "Female");
        GENDER_SYNONYMS.put("Feminin","Female");
        GENDER_SYNONYMS.put("femme", "Female");
        GENDER_SYNONYMS.put("femmes", "Female");
        GENDER_SYNONYMS.put("fille", "Female");
        GENDER_SYNONYMS.put("filles", "Female");
        GENDER_SYNONYMS.put("féminine", "Female");
        
        // Féminin - Anglais
        GENDER_SYNONYMS.put("female", "Female");
        GENDER_SYNONYMS.put("woman", "Female");
        GENDER_SYNONYMS.put("women", "Female");
        GENDER_SYNONYMS.put("girl", "Female");
        GENDER_SYNONYMS.put("girls", "Female");
        GENDER_SYNONYMS.put("f", "Female");
    }

    public static String normalizeGender(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        
        String normalized = input.trim();
        return GENDER_SYNONYMS.getOrDefault(normalized, input);
    }
}