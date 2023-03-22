package fr.abes.convergence.kbartws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.convergence.kbartws.component.BaseXmlFunctionsCaller;
import fr.abes.convergence.kbartws.exception.IllegalPpnException;
import fr.abes.convergence.kbartws.utils.Utilitaire;
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

import java.sql.SQLRecoverableException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {IssnService.class, BaseXmlFunctionsCaller.class})
class IssnServiceTest {

    @Autowired
    IssnService issnService;

    @MockBean
    BaseXmlFunctionsCaller caller;

    @MockBean
    Utilitaire utilitaire;

    @Test
    @DisplayName("Issn null")
    void checkFormatIssnNull() {
        Assertions.assertFalse(issnService.checkFormat(null));
    }

    @Test
    @DisplayName("Issn vide")
    void checkFormatIssnEmpty() {
        String issn = "";
        Assertions.assertFalse(issnService.checkFormat(issn));
    }

    @Test
    @DisplayName("Issn 8 caractères avec et sans trait d'union")
    void checkFormatIssn8Characters() {
        String issn1 = "0000-000";
        Assertions.assertFalse(issnService.checkFormat(issn1));

        String issn2 = "00000000";
        Assertions.assertTrue(issnService.checkFormat(issn2));

        String issn3 = "0000000X";
        Assertions.assertTrue(issnService.checkFormat(issn3));

        String issn4 = "000X0000";
        Assertions.assertFalse(issnService.checkFormat(issn4));

        String issn5 = "0000000x";
        Assertions.assertTrue(issnService.checkFormat(issn5));
    }

    @Test
    @DisplayName("Issn 9 caractères")
    void checkFormatIssn9Characters() {
        String issn1 = "0000-0000";
        Assertions.assertTrue(issnService.checkFormat(issn1));

        String issn2 = "0000-0001";
        Assertions.assertTrue(issnService.checkFormat(issn2));

        String issn3 = "000000001";
        Assertions.assertFalse(issnService.checkFormat(issn3));

        String issn4 = "0000-000X";
        Assertions.assertTrue(issnService.checkFormat(issn4));

        String issn5 = "000X-0000";
        Assertions.assertFalse(issnService.checkFormat(issn5));

        String issn6 = "0000-000x";
        Assertions.assertTrue(issnService.checkFormat(issn6));

        String issn7 = "00000-000";
        Assertions.assertFalse(issnService.checkFormat(issn7));

        String issn8 = "0345--322";
        Assertions.assertFalse(issnService.checkFormat(issn8));

        String issn9 = "-345-032-";
        Assertions.assertFalse(issnService.checkFormat(issn9));
    }

    @Test
    @DisplayName("getPpnFromIdentifiant with UncategorizedSQLException")
    void testgetPpnUncategorizedException() throws SQLRecoverableException {
        Mockito.doThrow(UncategorizedSQLException.class).when(caller).issnToPpn(Mockito.anyString());
        Assertions.assertThrows(IllegalPpnException.class, () -> issnService.getPpnFromIdentifiant("11111111"));
    }
}
