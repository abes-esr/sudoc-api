package fr.abes.sudoc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Utilitaire {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern REMOVE_PATTERN = Pattern.compile("[&,=\\?\\{\\}\\\\\\(\\)\\[\\]\\-\\~\\|\\$!;>\\*_%']");
    private static final Pattern patternReservecWords = Pattern.compile("\\b(ABOUT|ACCUM|AND|BT|BTG|BTI|BTP|EQUIV|FUZZY|HASPATH|INPATH|MDATA|MINUS|NEAR|NOT|NT|NTG|NTI|NTP|OR|PATTERN|PT|RT|SQE|SYN|TR|TRSYN|TT|WITHIN)\\b");
    private static final Pattern patternStopWords = Pattern.compile("\\b(a|all|almost|also|although|an|and|any|are|as|at|be|because|been|both|but|by|can|could|d|did|do|does|either|for|from|had|has|have|having|he|her|here|hers|him|his|how|however|i|if|in|into|is|it|its|just|ll|me|might|Mr|Mrs|Ms|my|no|non|nor|not|of|on|one|only|onto|or|our|ours|s|shall|she|should|since|so|some|still|such|t|than|that|the|their|them|then|there|therefore|these|they|this|those|though|through|thus|to|too|until|ve|very|was|we|were|what|when|where|whether|which|while|who|whose|why|will|with|would|yet|you|your|yours)\\b");
    private static final Map<Character, Character> ACCENT_MAP = Map.ofEntries(
            Map.entry('à', 'a'),
            Map.entry('â', 'a'),
            Map.entry('ä', 'a'),
            Map.entry('é', 'e'),
            Map.entry('è', 'e'),
            Map.entry('ê', 'e'),
            Map.entry('ë', 'e'),
            Map.entry('î', 'i'),
            Map.entry('ï', 'i'),
            Map.entry('ô', 'o'),
            Map.entry('ö', 'o'),
            Map.entry('ù', 'u'),
            Map.entry('û', 'u'),
            Map.entry('ü', 'u'),
            Map.entry('ç', 'c')
    );
    public static TYPE_ID getEnumFromString(String type) throws IllegalStateException {
        return switch (type.toLowerCase()) {
            case "monograph" -> TYPE_ID.ISBN;
            case "serial" -> TYPE_ID.ISSN;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    /**
     * Méthode permettant d'extraire une liste de ppn du json en retour d'un appel à une procédure stockée pl/sql issn2ppn ou isbn2ppn
     * le json est composé d'un objet résult contenant une liste de ppn localisés (champ ppn) et une liste de ppn sans localisation (champ resultNoHolding)
     *
     * @param json le json à parser
     * @return la liste des ppn résultat de l'appel au ws
     */
    public static List<String> parseJson(String json) throws JsonProcessingException {
        //la correspondance pouvant retourner plusieurs fois un ppn, on crée une multimap pour récupérer le résultat
        List<String> listePpn = new ArrayList<>();
        //parse de l'input json
        JsonNode sudocnode = objectMapper.readTree(json);

        extractPpnFromNode(listePpn, sudocnode, "result");
        extractPpnFromNode(listePpn, sudocnode, "resultNoHolding");
        return listePpn;
    }

    private static void extractPpnFromNode(List<String> listePpn, JsonNode sudocnode, String typeNode) {
        JsonNode resultnode = sudocnode.findValue(typeNode);
        if (null != resultnode) {
            Iterator<JsonNode> elements = resultnode.elements();
            if (elements.getClass().toString().contains("ArrayList")) {
                while (elements.hasNext()) {
                    JsonNode record = elements.next();
                    listePpn.add(record.path("ppn").asText());
                }
            } else {
                listePpn.add(elements.next().asText());
            }
        }
    }

    public static List<String> parseJsonDoi(String json) throws JsonProcessingException {
        //la correspondance pouvant retourner plusieurs fois un ppn, on crée une multimap pour récupérer le résultat
        List<String> listePpn = new ArrayList<>();
        //parse de l'input json
        JsonNode sudocnode = objectMapper.readTree(json);
        JsonNode resultsNode = sudocnode.findValue("results");
        if (resultsNode != null) {
            Iterator<JsonNode> elements = resultsNode.elements();
            while (elements.hasNext()) {
                JsonNode record = elements.next();
                listePpn.add(record.findValue("ppn").asText());
            }
        }
        return listePpn;
    }


    /**
     * Méthode permettant de remplacer les caractères diacritiques d'une chaine par leur équivalent non diacritique
     *
     * @param src chaine à transformer
     * @return chaine transformée
     */
    public static String replaceDiacritics(String src) {
        StringBuffer result = new StringBuffer();
        if (src != null && !src.isEmpty()) {
            char c;
            for (int i = 0; i < src.length(); i++) {
                c = src.charAt(i);
                result.append(ACCENT_MAP.getOrDefault(c, c));
            }
        }
        return result.toString();
    }

    public static String formatString(String chaine) {
        if (chaine != null && !chaine.isEmpty()) {
            return ajoutNearBetweenWords(banalisationReservedWords(suppStopWords(suppCaracters(chaine))));
        }
        return chaine;
    }

    private static String suppCaracters(String chaine) {
        // Construire une expression régulière avec les caractères à supprimer
        return REMOVE_PATTERN.matcher(chaine).replaceAll(" ");
    }

    private static String banalisationReservedWords(String chaine) {
        // Remplacer les mots réservés par eux-mêmes entourés d'accolades
        return patternReservecWords.matcher(chaine.toUpperCase()).replaceAll("{$1}");
    }

    private static String ajoutNearBetweenWords(String chaine) {
        // Split la chaîne en mots en éliminant les espaces multiples
        String[] words = chaine.trim().split("\\s+");

        // Utilise String.join pour insérer "NEAR" entre les mots
        return String.join(" NEAR ", words);
    }

    private static String suppStopWords(String chaine) {
        return patternStopWords.matcher(chaine.toLowerCase()).replaceAll("");
    }

}

