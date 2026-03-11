package fr.abes.sudoc.service;

import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.exception.IllegalPpnException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {IsbnService.class, BaseXmlFunctionsCaller.class})
class IsbnServiceTest {

    @Autowired
    IsbnService isbnService;

    @MockitoBean
    BaseXmlFunctionsCaller caller;

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("ISBN null ou vide")
    void checkFormatIsbnNullOrEmpty(String isbn) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> isbnService.checkFormat(isbn));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Z3672EDEDU",
            "--------6-",
            "23672Y+} N5",
            "-22225-555---3--1",
            "-22225-555---3--",
            "-22225-555---3--11",
            "-1243566666-",
            "123-45-67-891-010",
            "123--45-67-891-010",
            "-123-45-67-891-010"
    })
    @DisplayName("ISBN au format invalide")
    void checkFormatIsbnInvalid(String isbn) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> isbnService.checkFormat(isbn));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2222255555",
            "222225-5555",
            "274758450X",
            "979-2-492283-49-9",
            "1234567891011"
    })
    @DisplayName("ISBN au format valide")
    void checkFormatIsbnValid(String isbn) {
        Assertions.assertDoesNotThrow(() -> isbnService.checkFormat(isbn));
    }

    @Test
    @DisplayName("getPpnFromIdentifiant avec SQLException 'no ppn matched' lève IllegalPpnException")
    void testGetPpnNoPpnMatched() throws SQLException {
        SQLException ex = new SQLException("no ppn matched", "select test", new SQLException());
        Mockito.doThrow(ex).when(caller).isbnToPpn(Mockito.anyString());
        Assertions.assertThrows(IllegalPpnException.class, () -> isbnService.getPpnFromIdentifiant("1111111111"));
    }

    @Test
    @DisplayName("getPpnFromIdentifiant avec SQLException générique lève IOException")
    void testGetPpnSqlException() throws SQLException {
        SQLException ex = new SQLException("trululu", "select test", new SQLException());
        Mockito.doThrow(ex).when(caller).isbnToPpn(Mockito.anyString());
        Assertions.assertThrows(IOException.class, () -> isbnService.getPpnFromIdentifiant("1111111111"));
    }
}