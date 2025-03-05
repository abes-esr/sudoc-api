package fr.abes.sudoc.service;

import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.exception.IllegalPpnException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {IsbnService.class, BaseXmlFunctionsCaller.class})
class IsbnServiceTest {

    @Autowired
    IsbnService isbnService;

    @MockBean
    BaseXmlFunctionsCaller caller;

    @Test
    @DisplayName("Isbn nul")
    void checkFormatIsbnNull() {
        Assertions.assertFalse(isbnService.checkFormat(null));
    }

    @Test
    @DisplayName("Isbn vide")
    void checkFormatIsbnEmpty() {
        String isbn = "";
        Assertions.assertFalse(isbnService.checkFormat(isbn));
    }

    @Test
    @DisplayName("Isbn 10 caractères avec et sans trait(s) d'union")
    void checkFormatIsbn10Characters() {
        String isbn = "Z3672EDEDU";
        Assertions.assertFalse(isbnService.checkFormat(isbn));

        String isbn2 = "--------6-";
        Assertions.assertFalse(isbnService.checkFormat(isbn2));

        String isbn3 = "23672Y+} N5";
        Assertions.assertFalse(isbnService.checkFormat(isbn3));

        String isbn4 = "2222255555";
        Assertions.assertTrue(isbnService.checkFormat(isbn4));

        String isbn5 = "222225-5555";
        Assertions.assertTrue(isbnService.checkFormat(isbn5));

        String isbn6 = "-22225-555---3--1";
        Assertions.assertFalse(isbnService.checkFormat(isbn6));

        String isbn7 = "274758450X";
        Assertions.assertTrue(isbnService.checkFormat(isbn7));
    }

    @Test
    @DisplayName("Isbn n caractères avec et sans trait(s) d'union")
    void checkFormatIsbnNCharacters() {
        String isbn1 = "-22225-555---3--";
        Assertions.assertFalse(isbnService.checkFormat(isbn1));

        String isbn2 = "-22225-555---3--11";
        Assertions.assertFalse(isbnService.checkFormat(isbn2));

        String isbn3 = "-1243566666-";
        Assertions.assertFalse(isbnService.checkFormat(isbn3));


    }

    @Test
    @DisplayName("Isbn 13 caractères avec et sans trait(s) d'union")
    void checkFormatIsbn13Characters() {
        String isbn1 = "979-2-492283-49-9";
        Assertions.assertTrue(isbnService.checkFormat(isbn1));

        String isbn2 = "1234567891011";
        Assertions.assertTrue(isbnService.checkFormat(isbn2));

        String isbn3 = "123-45-67-891-010";
        Assertions.assertFalse(isbnService.checkFormat(isbn3));

        String isbn4 = "123--45-67-891-010";
        Assertions.assertFalse(isbnService.checkFormat(isbn4));

        String isbn5 = "-123-45-67-891-010";
        Assertions.assertFalse(isbnService.checkFormat(isbn5));
    }

    @Test
    @DisplayName("getPpnFromIdentifiant with UncategorizedSQLException")
    void testgetPpnUncategorizedException() {
        UncategorizedSQLException ex = new UncategorizedSQLException("no ppn matched", "select test", new SQLException());
        Mockito.doThrow(ex).when(caller).isbnToPpn(Mockito.anyString());
        Assertions.assertThrows(IllegalPpnException.class, () -> isbnService.getPpnFromIdentifiant("1111111111"));

        ex = new UncategorizedSQLException("trululu", "select test", new SQLException());
        Mockito.doThrow(ex).when(caller).isbnToPpn(Mockito.anyString());
        Assertions.assertThrows(IOException.class, () -> isbnService.getPpnFromIdentifiant("1111111111"));
    }

}
