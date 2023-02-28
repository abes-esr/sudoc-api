package fr.abes.convergence.kbartws.service;

import fr.abes.convergence.kbartws.component.BaseXmlFunctionsCaller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

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
        Assertions.assertTrue(isbnService.checkFormat(isbn6));
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



}
