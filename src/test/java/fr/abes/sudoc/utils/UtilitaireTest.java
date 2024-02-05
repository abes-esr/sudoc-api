package fr.abes.sudoc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class UtilitaireTest {

    @Test
    void getEnumFromString() {
        Assertions.assertEquals(TYPE_ID.ISBN, Utilitaire.getEnumFromString("monograph"));
        Assertions.assertEquals(TYPE_ID.ISSN, Utilitaire.getEnumFromString("serial"));
        Assertions.assertEquals(TYPE_ID.ISBN, Utilitaire.getEnumFromString("MONOgraph"));
        Assertions.assertEquals(TYPE_ID.ISSN, Utilitaire.getEnumFromString("serIAL"));
        Assertions.assertThrows(IllegalStateException.class, () -> Utilitaire.getEnumFromString("test"));
    }

    @Test
    @DisplayName("parseJson : 1 seul ppn localisé")
    void testParseJson1() throws JsonProcessingException {
        String json = "{\"sudoc\":{\"query\":{\"issn\":\"1872-8871\",\"result\":{\"ppn\":\"038442922\"}}}}";
        List<String> ppns = Utilitaire.parseJson(json);
        Assertions.assertEquals(1, ppns.size());
        Assertions.assertEquals("038442922", ppns.get(0));
    }

    @Test
    @DisplayName("parseJson : 2 ppn localisés")
    void testParseJson2() throws JsonProcessingException {
        String json = "{\"sudoc\":{\"query\":{\"issn\":\"0177-610X\",\"result\":[{\"ppn\":\"088074439\"},{\"ppn\":\"25287403X\"}]}}}";
        List<String> ppns = Utilitaire.parseJson(json);
        Assertions.assertEquals(2, ppns.size());
        Assertions.assertEquals("088074439", ppns.get(0));
        Assertions.assertEquals("25287403X", ppns.get(1));
    }

    @Test
    @DisplayName("parseJson : 1 ppn non localisé")
    void testParseJson3() throws JsonProcessingException {
        String json = "{\"sudoc\":{\"query\":{\"issn\":\"1872-8871\",\"resultNoHolding\":{\"ppn\":\"038442922\"}}}}";
        List<String> ppns = Utilitaire.parseJson(json);
        Assertions.assertEquals(1, ppns.size());
        Assertions.assertEquals("038442922", ppns.get(0));
    }

    @Test
    @DisplayName("parseJson : 2 ppn non localisés")
    void testParseJson4() throws JsonProcessingException {
        String json = "{\"sudoc\":{\"query\":{\"issn\":\"0177-610X\",\"resultNoHolding\":[{\"ppn\":\"088074439\"},{\"ppn\":\"25287403X\"}]}}}";
        List<String> ppns = Utilitaire.parseJson(json);
        Assertions.assertEquals(2, ppns.size());
        Assertions.assertEquals("088074439", ppns.get(0));
        Assertions.assertEquals("25287403X", ppns.get(1));
    }

    @Test
    @DisplayName("parseJson : 1 ppn localisé, 1 ppn non localisé")
    void testParseJson5() throws JsonProcessingException {
        String json = "{\"sudoc\":{\"query\":{\"issn\":\"1872-8871\",\"result\":{\"ppn\":\"038442922\"},\"resultNoHolding\":{\"ppn\":\"108563596\"}}}}";
        List<String> ppns = Utilitaire.parseJson(json);
        Assertions.assertEquals(2, ppns.size());
        Assertions.assertEquals("038442922", ppns.get(0));
        Assertions.assertEquals("108563596", ppns.get(1));
    }

    @Test
    @DisplayName("parseJson : 2 ppn localisés, 2 ppn non localisés")
    void testParseJson6() throws JsonProcessingException {
        String json = "{\"sudoc\":{\"query\":{\"issn\":\"0177-610X\",\"result\":[{\"ppn\":\"088074439\"},{\"ppn\":\"25287403X\"}],\"resultNoHolding\":[{\"ppn\":\"111111111\"},{\"ppn\":\"222222222\"}]}}}";
        List<String> ppns = Utilitaire.parseJson(json);
        Assertions.assertEquals(4, ppns.size());
        Assertions.assertEquals("088074439", ppns.get(0));
        Assertions.assertEquals("25287403X", ppns.get(1));
        Assertions.assertEquals("111111111", ppns.get(2));
        Assertions.assertEquals("222222222", ppns.get(3));
    }

    @Test
    @DisplayName("parseJsonDoi : 1 seul résultat")
    void testParseJsonDoi1() throws JsonProcessingException {
        String json = "{\"sudoc\":{\"results\":{\"result\":{\"ppn\":\"198471483\",\"doi\":\"10.1039/9781849733458\"}}}}";
        List<String> ppns = Utilitaire.parseJsonDoi(json);
        Assertions.assertEquals(1, ppns.size());
        Assertions.assertEquals("198471483", ppns.get(0));
    }

    @Test
    @DisplayName("parseJsonDoi : 1 seul résultat")
    void testParseJsonDoi2() throws JsonProcessingException {
        String json = "{\"sudoc\":{\"results\":[{\"result\":{\"ppn\":\"224633333\",\"doi\":\"10.1002/9781118444726\"}},{\"result\":{\"ppn\":\"17886594X\",\"doi\":\"10.1002/9781118444726\"}}]}}";
        List<String> ppns = Utilitaire.parseJsonDoi(json);
        Assertions.assertEquals(2, ppns.size());
        Assertions.assertEquals("224633333", ppns.get(0));
        Assertions.assertEquals("17886594X", ppns.get(1));
    }

    @Test
    @DisplayName("test replaceDiacritics")
    void testReplaceDiacritics() {
        String chaine = "àâäéèêëîïôöùûüç";
        Assertions.assertEquals("aaaeeeeiioouuuc", Utilitaire.replaceDiacritics(chaine));

        chaine = "çüûùöôïîëêèéäâà";
        Assertions.assertEquals("cuuuooiieeeeaaa", Utilitaire.replaceDiacritics(chaine));
    }
}
