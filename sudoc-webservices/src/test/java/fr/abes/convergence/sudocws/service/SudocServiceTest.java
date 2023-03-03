package fr.abes.convergence.sudocws.service;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.convergence.sudocws.exception.ControllerExceptionHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@SpringBootTest(classes = {SudocService.class, ControllerExceptionHandler.class})
class SudocServiceTest {

    @Autowired
    WebApplicationContext context;

    @MockBean
    ProcessCBS cbs;

    @Autowired
    SudocService service;

    @Test
    @DisplayName("getPpnFromDatWithAnneeAuteurTitre")
    void getPpnFromDatWithAnneeAuteurTitre() throws CBSException {

        Integer annee = 2008;
        String auteur = "Auteur";
        String titre = "Ours";

        Mockito.when(cbs.search("tno t ; tdo b ; apu " + annee + " ; che aut " + auteur + " et mti " + titre)).thenReturn("123456789");
        Mockito.when(cbs.getListePpn()).thenReturn(new StringBuilder("123456789;987654321"));
        Mockito.when(cbs.getNbNotices()).thenReturn(2);

        List<String> result = service.getPpnFromDat(annee, auteur, titre);

        Assertions.assertEquals( "123456789", result.get(0));
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
