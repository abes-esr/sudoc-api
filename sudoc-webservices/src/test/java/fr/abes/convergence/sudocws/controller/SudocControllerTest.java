package fr.abes.convergence.sudocws.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.cbs.exception.CBSException;
import fr.abes.convergence.sudocws.dto.ResultWebDto;
import fr.abes.convergence.sudocws.dto.SearchDatWebDto;
import fr.abes.convergence.sudocws.exception.ControllerExceptionHandler;
import fr.abes.convergence.sudocws.service.SudocService;
import org.junit.jupiter.api.Assertions;
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

@SpringBootTest(classes = {SudocController.class, SudocService.class, ControllerExceptionHandler.class})
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
                .setControllerAdvice(ControllerExceptionHandler.class)
                .build();
    }

    @Test
    @DisplayName("datToPpn titreIsNull")
    void datToPpnTitreIsNull() throws Exception {

        SearchDatWebDto searchDatRequest = new SearchDatWebDto();
        searchDatRequest.setDate(2008);
        searchDatRequest.setAuteur("");
        searchDatRequest.setTitre(null);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(searchDatRequest);

        this.mockMvc.perform(post("/api/v1/dat2ppn")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue((result.getResolvedException() instanceof IllegalArgumentException)));
    }

    @Test
    @DisplayName("datToPpn tryCatchWorks")
    void datToPpnTryCatchWorks() throws Exception {

        SearchDatWebDto searchDatRequest = new SearchDatWebDto();
        searchDatRequest.setDate(2008);
        searchDatRequest.setAuteur("");
        searchDatRequest.setTitre("Ours");

        List<String> ppnList = new ArrayList<>();
        ppnList.add("123456789");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(searchDatRequest);

        Mockito.when(service.getPpnFromDat(searchDatRequest.getDate(), searchDatRequest.getAuteur(), searchDatRequest.getTitre())).thenReturn(ppnList);

        this.mockMvc.perform(post("/api/v1/dat2ppn")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[0]").value("123456789"));
    }

    @Test
    @DisplayName("datToPpn tryCatchNotWorks")
    void datToPpnTryCatchNotWorks() throws Exception {

        SearchDatWebDto searchDatRequest = new SearchDatWebDto();
        searchDatRequest.setDate(2008);
        searchDatRequest.setAuteur("");
        searchDatRequest.setTitre("Ours");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(searchDatRequest);

        CBSException cbsException = new CBSException("404", "La rechercher n'a pas donné de résultat");

        Mockito.when(service.getPpnFromDat(searchDatRequest.getDate(), searchDatRequest.getAuteur(), searchDatRequest.getTitre())).thenThrow(cbsException);

        this.mockMvc.perform(post("/api/v1/dat2ppn")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.erreurs[0]").value("La rechercher n'a pas donné de résultat"));
    }
}
