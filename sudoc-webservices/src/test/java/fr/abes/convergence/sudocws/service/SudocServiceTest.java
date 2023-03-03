package fr.abes.convergence.sudocws.service;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.convergence.sudocws.exception.ControllerExceptionHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {SudocService.class, ControllerExceptionHandler.class})
class SudocServiceTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @MockBean
    ProcessCBS cbs;

    @Autowired
    SudocService service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(context.getBean(SudocService.class))
                .setControllerAdvice(ControllerExceptionHandler.class)
                .build();
    }

    @Test
    @DisplayName("getPpnFromDat")
    void getPpnFromDat() throws CBSException {

        Integer annee = 2008;
        String auteur = "Auteur";
        String titre = "Ours";

        java.lang.String serveur = "";
        java.lang.String port = "";
        java.lang.String login = "";
        java.lang.String passwd = "";

        List<String> ppnList = new ArrayList<>();
        ppnList.add("123456789");

//        Mockito.doReturn("123456789").when(cbs.search("tno t ; tdo b ; apu " + annee + " ; che aut " + auteur + " et mti " + titre));
//        Mockito.when(cbs.search("tno t ; tdo b ; apu " + annee + " ; che aut " + auteur + " et mti " + titre)).thenReturn()


//        Mockito.doNothing().when(cbs.authenticate(serveur, port, login, passwd));
//        Mockito.doNothing().when(cbs.search("tno t ; tdo b ; apu " + annee + " ; che aut " + auteur + " et mti " + titre));
//        Mockito.when(cbs.getListePpn()).thenReturn(new StringBuilder(ppnList.toString()));
//        Mockito.when(cbs.getNbNotices()).thenReturn(ppnList.size());
//
//        List<String> result = service.getPpnFromDat(annee, auteur, titre);
//
//        Assertions.assertEquals( "123456789", result.toString());
    }
}
