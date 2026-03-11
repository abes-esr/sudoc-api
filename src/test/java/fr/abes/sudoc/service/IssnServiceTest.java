package fr.abes.sudoc.service;

import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.utils.Utilitaire;
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
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {IssnService.class, BaseXmlFunctionsCaller.class})
class IssnServiceTest {

    @Autowired
    IssnService issnService;

    @MockitoBean
    BaseXmlFunctionsCaller caller;

    @MockitoBean
    Utilitaire utilitaire;

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("ISSN null ou vide")
    void checkFormatIssnNullOrEmpty(String issn) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> issnService.checkFormat(issn));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0000-000",
            "000X0000",
            "000000001",
            "000X-0000",
            "00000-000",
            "0345--322",
            "-345-032-"
    })
    @DisplayName("ISSN au format invalide")
    void checkFormatIssnInvalid(String issn) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> issnService.checkFormat(issn));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000",
            "0000000X",
            "0000000x",
            "0000-0000",
            "0000-0001",
            "0000-000X",
            "0000-000x"
    })
    @DisplayName("ISSN au format valide")
    void checkFormatIssnValid(String issn) {
        Assertions.assertDoesNotThrow(() -> issnService.checkFormat(issn));
    }

    @Test
    @DisplayName("getPpnFromIdentifiant avec UncategorizedSQLException lève IOException")
    void testGetPpnUncategorizedException() throws SQLException {
        Mockito.doThrow(UncategorizedSQLException.class).when(caller).issnToPpn(Mockito.anyString());
        Assertions.assertThrows(IOException.class, () -> issnService.getPpnFromIdentifiant("11111111"));
    }
}