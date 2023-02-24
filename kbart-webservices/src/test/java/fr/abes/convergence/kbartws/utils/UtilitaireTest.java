package fr.abes.convergence.kbartws.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilitaireTest {

    @Test
    void getEnumFromString() {
        Assertions.assertEquals(TYPE_ID.ISBN, Utilitaire.getEnumFromString("monograph"));
        Assertions.assertEquals(TYPE_ID.ISSN, Utilitaire.getEnumFromString("serial"));
        Assertions.assertThrows(IllegalStateException.class, () -> Utilitaire.getEnumFromString("test"));
    }
}