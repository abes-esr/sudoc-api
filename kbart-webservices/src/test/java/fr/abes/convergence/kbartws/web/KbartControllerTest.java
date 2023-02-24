package fr.abes.convergence.kbartws.web;

import fr.abes.convergence.kbartws.exception.ExceptionControllerHandler;
import fr.abes.convergence.kbartws.service.IdentifiantFactory;
import fr.abes.convergence.kbartws.service.IsbnService;
import fr.abes.convergence.kbartws.service.IssnService;
import fr.abes.convergence.kbartws.service.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {KbartController.class})
@ContextConfiguration
class KbartControllerTest {
    @Autowired
    WebApplicationContext context;

    @InjectMocks
    KbartController controller;

    @Autowired
    IdentifiantFactory factory;

    @MockBean
    NoticeService noticeService;

    @MockBean
    IsbnService isbnService;

    @MockBean
    IssnService issnService;

    MockMvc mockMvc;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(context.getBean(KbartController.class))
                .setControllerAdvice(new ExceptionControllerHandler())
                .build();
    }

    @Test
    @DisplayName("test WS : Serial + ISSN ok + 1 PPN non supprimé de doc élec")
    void onlineIdentifier2PpnCas1() {

    }

    @Test
    @DisplayName("test WS : Serial + ISSN ok + 2 PPN non supprimés de doc élec")
    void onlineIdentifier2PpnCas2() {

    }

    @Test
    @DisplayName("test WS : Serial + ISSN ok + 1 PPN supprimé de doc élec")
    void onlineIdentifier2PpnCas3() {

    }

    @Test
    @DisplayName("test WS : Serial + ISSN ok + 2 PPN dont 1 supprimé de doc élec")
    void onlineIdentifier2PpnCas4() {

    }

    @Test
    @DisplayName("test WS : Serial + ISSN ok + 1 PPN non supprimé non doc élec")
    void onlineIdentifier2PpnCas5() {

    }

    @Test
    @DisplayName("test WS : Serial + ISSN ok + 2 PPN non supprimés non doc élec")
    void onlineIdentifier2PpnCas6() {

    }

    @Test
    @DisplayName("test WS : Serial + ISSN ok + 1 PPN supprimé non doc élec")
    void onlineIdentifier2PpnCas7() {

    }

    @Test
    @DisplayName("test WS : Serial + ISSN ok + 2 PPN dont 1 supprimé non doc élec")
    void onlineIdentifier2PpnCas8() {

    }
}