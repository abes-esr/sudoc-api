package fr.abes.sudoc.service;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.sudoc.exception.ExceptionControllerHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@SpringBootTest(classes = {SudocService.class, ExceptionControllerHandler.class})
@ExtendWith({SpringExtension.class})
class SudocServiceTest {

    @Autowired
    WebApplicationContext context;

    @MockBean
    ProcessCBS cbs;

    @Autowired
    SudocService service;

    @Test
    @DisplayName("getPpnFromDatWithAnneeAuteurTitre Many results")
    void getPpnFromDatWithAnneeAuteurTitreMany() throws CBSException {

        Integer annee = 2008;
        String auteur = "Auteur";
        String titre = "Ours";

        Mockito.doNothing().when(cbs).authenticate(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(cbs.search("tno t ; tdo b ; apu " + annee + " ; che aut " + auteur + " et mti " + titre)).thenReturn("123456789");
        Mockito.when(cbs.getListePpn()).thenReturn(new StringBuilder("123456789;987654321"));
        Mockito.when(cbs.getNbNotices()).thenReturn(2);

        List<String> result = service.getPpnFromDat(annee, auteur, titre);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals( "123456789", result.get(0));
    }

    @Test
    @DisplayName("getPpnFromDatWithAnneeAuteurTitre one result")
    void getPpnFromDatWithAnneeAuteurTitreOne() throws CBSException {

        Integer annee = 2008;
        String auteur = "Auteur";
        String titre = "Ours";

        Mockito.doNothing().when(cbs).authenticate(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(cbs.search("tno t ; tdo b ; apu " + annee + " ; che aut " + auteur + " et mti " + titre)).thenReturn("123456789");
        Mockito.when(cbs.getPpnEncours()).thenReturn("123456789");
        Mockito.when(cbs.getNbNotices()).thenReturn(1);


        List<String> result = service.getPpnFromDat(annee, auteur, titre);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals( "123456789", result.get(0));
    }

    @Test
    @DisplayName("getPpnFromDatWithAnneeAuteurTitre no result")
    void getPpnFromDatWithAnneeAuteurTitreZero() throws CBSException {
        Integer annee = 2008;
        String auteur = "Auteur";
        String titre = "Ours";

        Mockito.doNothing().when(cbs).authenticate(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(cbs.search("tno t ; tdo b ; apu " + annee + " ; che aut " + auteur + " et mti " + titre)).thenReturn("123456789");
        Mockito.when(cbs.getNbNotices()).thenReturn(0);


        List<String> result = service.getPpnFromDat(annee, auteur, titre);
        Assertions.assertEquals(0, result.size());
        Assertions.assertEquals("tno t ; apu " + annee + " ; che aut " + auteur + " et mti " + titre, service.getQuery());
    }

    @Test
    @DisplayName("getPpnFromDatWithAnneeAuteurTitre no aut no yop")
    void getPpnFromDatWithAnneeAuteurTitreNoAutNoYop() throws CBSException {
        String titre = "Ours";
        Integer annee = null;
        String auteur = null;

        Mockito.doNothing().when(cbs).authenticate(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(cbs.search(Mockito.anyString())).thenReturn("123456789");

        service.getPpnFromDat(annee, auteur, titre);
        Assertions.assertEquals("tno t ; che mti " + titre, service.getQuery());
    }

    @Test
    @DisplayName("getPpnFromDatWithAnneeAuteurTitre no yop")
    void getPpnFromDatWithAnneeAuteurTitreNoYop() throws CBSException {
        String titre = "Ours";
        Integer annee = null;
        String auteur = "Petit";

        Mockito.doNothing().when(cbs).authenticate(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(cbs.search(Mockito.anyString())).thenReturn("123456789");

        service.getPpnFromDat(annee, auteur, titre);
        Assertions.assertEquals("tno t ; che aut " + auteur + " et mti " + titre, service.getQuery());
    }

    @Test
    @DisplayName("getPpnFromDatWithAnneeAuteurTitre no aut")
    void getPpnFromDatWithAnneeAuteurTitreNoAut() throws CBSException {
        String titre = "Ours";
        Integer annee = 2000;
        String auteur = null;

        Mockito.doNothing().when(cbs).authenticate(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(cbs.search(Mockito.anyString())).thenReturn("123456789");

        service.getPpnFromDat(annee, auteur, titre);
        Assertions.assertEquals("tno t ; apu " + annee + " ; che mti " + titre, service.getQuery());
    }

    @Test
    @DisplayName("getPpnFromDatAllIsNull")
    void getPpnFromDatAllIsNull() throws CBSException {

        Integer annee = null;
        String auteur = null;
        String titre = null;

        List<String> result = service.getPpnFromDat(annee, auteur, titre);

        Assertions.assertEquals( 0, result.size());
    }
}
