package fr.abes.sudoc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utilitaire {
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
        ObjectMapper objectMapper = new ObjectMapper();
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
            }
            else {
                listePpn.add(elements.next().asText());
            }
        }
    }

    /**
     * Méthode permettant de remplacer les caractères diacritiques d'une chaine par leur équivalent non diacritique
     * @param src chaine à transformer
     * @return chaine transformée
     */
    public static String replaceDiacritics(String src) {
        StringBuffer result = new StringBuffer();
        if(src!=null && src.length()!=0) {
            int index = -1;
            char c;
            String chars= "àâäéèêëîïôöùûüç";
            String replace= "aaaeeeeiioouuuc";
            for(int i=0; i<src.length(); i++) {
                c = src.charAt(i);
                if( (index=chars.indexOf(c))!=-1 )
                    result.append(replace.charAt(index));
                else
                    result.append(c);
            }
        }
        return result.toString();
    }
}

