package fr.abes.convergence.sudocws.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.convergence.sudocws.dto.ResultWebDto;
import fr.abes.convergence.sudocws.dto.SearchDatWebDto;
import fr.abes.convergence.sudocws.service.SudocService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {SudocController.class, SudocService.class})
@ContextConfiguration(classes = {})
class SudocControllerTest {

    @Autowired
    WebApplicationContext context;

    @InjectMocks
    SudocController controller;

    MockMvc mockMvc;

    @MockBean
    SudocService service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(context.getBean(SudocController.class))
                .build();
    }

    @Test
    @DisplayName("datToPpn try work")
    void datToPpn() throws Exception {

        SearchDatWebDto searchDatRequest = new SearchDatWebDto();
        searchDatRequest.setDate(2008);
        searchDatRequest.setAuteur("");
        searchDatRequest.setTitre("Ours");

        List<String> ppnList = new ArrayList<>();
        ppnList.add("123456789");

        ResultWebDto result = new ResultWebDto();
        result.addPpn("123456789");

        Mockito.when(service.getPpnFromDat(searchDatRequest.getDate(), searchDatRequest.getAuteur(), searchDatRequest.getTitre())).thenReturn(ppnList);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(searchDatRequest);

        this.mockMvc.perform(post("/api/v1/dat2ppn")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[0]").value("123456789"));
    }
}

//    @Test
//    @DisplayName("test WS : serial + ISSN ok + exception erreur SQL")
//    void onlineIdentifier2PpnCas3() throws Exception, IllegalPpnException {
//        String type = "serial";
//        String onlineIdentifier = "1234-1234";
//
//        Controlfield ctrlPpn = new Controlfield();
//        ctrlPpn.setTag("001");
//        ctrlPpn.setValue("123456789");
//
//        Controlfield ctrlType = new Controlfield();
//        ctrlType.setTag("008");
//        ctrlType.setValue("Oax3");
//
//        NoticeXml notice = new NoticeXml();
//        notice.setLeader("     gam0 22        450 ");
//        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));
//        Mockito.when(issnService.checkFormat("1234-1234")).thenReturn(true);
//        Mockito.when(issnService.getPpnFromIdentifiant("1234-1234")).thenReturn(Lists.newArrayList("123456789"));
//        Mockito.doThrow(SQLException.class).when(noticeService).getNoticeByPpn(Mockito.any());
//
//        this.mockMvc.perform(get("/v1/online_identifier_2_ppn/" + type + "/" + onlineIdentifier))
//                .andExpect(status().isServiceUnavailable());
//    }
//
//    @Test
//    @DisplayName("test WS : Serial + ISSN au mauvais format")
//    void onlineIdentifier2PpnCas4() throws Exception {
//        String type = "serial";
//        String onlineIdentifier = "1234ZE234";
//
//        Mockito.when(issnService.checkFormat("1234ZE234")).thenReturn(false);
//
//        this.mockMvc.perform(get("/v1/online_identifier_2_ppn/" + type + "/" + onlineIdentifier))
//                .andExpect(status().isBadRequest());
//    }
//}
