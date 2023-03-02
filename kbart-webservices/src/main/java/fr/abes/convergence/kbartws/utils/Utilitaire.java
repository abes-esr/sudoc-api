package fr.abes.convergence.kbartws.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utilitaire {
    public static TYPE_ID getEnumFromString(String type) {
        return switch (type.toLowerCase()) {
            case "monograph" -> TYPE_ID.ISBN;
            case "serial" -> TYPE_ID.ISSN;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    /**
     * Méthode permettant d'extraire une liste de ppn du json en retour d'un appel à une procédure stockée pl/sql
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
}

