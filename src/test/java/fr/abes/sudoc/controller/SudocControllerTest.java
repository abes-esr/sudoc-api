package fr.abes.sudoc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.sudoc.dto.SearchDatWebDto;
import fr.abes.sudoc.dto.provider.ElementDto;
import fr.abes.sudoc.entity.notice.Controlfield;
import fr.abes.sudoc.entity.notice.Datafield;
import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.entity.notice.SubField;
import fr.abes.sudoc.exception.ExceptionControllerHandler;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.service.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {SudocController.class, DatService.class, ExceptionControllerHandler.class})
@ContextConfiguration(classes = {IdentifiantFactory.class})
class SudocControllerTest {

    public static final String SERIAL = "serial";
    public static final String PROVIDER_OK = "CAIRN";
    @Autowired
    WebApplicationContext context;

    @InjectMocks
    SudocController controler;

    @Autowired
    IdentifiantFactory factory;

    @MockitoBean
    DatService service;

    @MockitoBean
    NoticeService noticeService;

    @MockitoBean
    ProviderService providerService;

    @MockitoBean
    IsbnService isbnService;

    @MockitoBean
    IssnService issnService;

    @MockitoBean
    DoiService doiService;

    MockMvc mockMvc;

    public static final String ISSN_IDENTIFIER_OK = "1234-1234";
    public static final String ISSN_IDENTIFIER_KO = "1234ZE234";

    public static final String PPN_OK = "123456789";
    public static final String DOI_OK = "10.1006/jmbi.1998.2354";


    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(context.getBean(SudocController.class))
                .setControllerAdvice(ExceptionControllerHandler.class)
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
        ppnList.add(PPN_OK);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(searchDatRequest);

        Controlfield ctrlPpn = new Controlfield();
        ctrlPpn.setTag("001");
        ctrlPpn.setValue(PPN_OK);
        Controlfield ctrlType = new Controlfield();
        ctrlType.setTag("008");
        ctrlType.setValue("Oax3");
        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));

        Mockito.when(providerService.getProviderDisplayName(Mockito.any())).thenReturn(Optional.of(new ElementDto()));
        Mockito.when(service.getPpnFromDat(searchDatRequest.getDate(), searchDatRequest.getAuteur(), searchDatRequest.getTitre())).thenReturn(ppnList);
        Mockito.when(noticeService.getNoticeByPpn(PPN_OK)).thenReturn(notice);

        this.mockMvc.perform(post("/api/v1/dat2ppn")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[0].ppn").value(PPN_OK));
    }

    @Test
    @DisplayName("datToPpn getPpnFromDat n'a donné aucun résultat")
    void datToPpnTryCatchNotWorks() throws Exception {

        SearchDatWebDto searchDatRequest = new SearchDatWebDto();
        searchDatRequest.setDate(2008);
        searchDatRequest.setAuteur("");
        searchDatRequest.setTitre("Ours");
        searchDatRequest.setProviderName("");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(searchDatRequest);

        IllegalPpnException illegalPpnException = new IllegalPpnException("La rechercher n'a pas donné de résultat");

        Mockito.when(service.getPpnFromDat(searchDatRequest.getDate(), searchDatRequest.getAuteur(), searchDatRequest.getTitre())).thenThrow(illegalPpnException);

        this.mockMvc.perform(post("/api/v1/dat2ppn")
                        .accept(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding(StandardCharsets.UTF_8)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.erreurs[0]").value("La rechercher n'a pas donné de résultat"));
    }


    @Test
    @DisplayName("test WS online_identifier_2_ppn : serial + ISSN ok + 1 PPN non supprimé de doc élec")
    void onlineIdentifier2PpnCas1() throws Exception {


        Controlfield ctrlPpn = new Controlfield();
        ctrlPpn.setTag("001");
        ctrlPpn.setValue(PPN_OK);

        Controlfield ctrlType = new Controlfield();
        ctrlType.setTag("008");
        ctrlType.setValue("Oax3");

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));
        Mockito.doNothing().when(issnService).checkFormat(ISSN_IDENTIFIER_OK);
        Mockito.when(issnService.getPpnFromIdentifiant(ISSN_IDENTIFIER_OK)).thenReturn(Lists.newArrayList(PPN_OK));
        Mockito.when(noticeService.getNoticeByPpn(Mockito.any())).thenReturn(notice);

        this.mockMvc.perform(get("/api/v1/online_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_OK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[0].ppn").value(PPN_OK))
                .andExpect(jsonPath("$.ppns[0].providerPresent").value(false));
    }


    @Test
    @DisplayName("test WS online_identifier_2_ppn : serial + ISSN ok + 2 PPN non supprimés dont un ppn qui n'est pas une notice electronique")
    void onlineIdentifier2PpnCas2() throws Exception {


        Controlfield ctrlPpn = new Controlfield();
        ctrlPpn.setTag("001");
        ctrlPpn.setValue(PPN_OK);

        Controlfield ctrlType = new Controlfield();
        ctrlType.setTag("008");
        ctrlType.setValue("Oax3");

        Controlfield ctrlPpn2 = new Controlfield();
        ctrlPpn2.setTag("001");
        ctrlPpn2.setValue("123456000");

        Controlfield ctrlType2 = new Controlfield();
        ctrlType2.setTag("008");
        ctrlType2.setValue("Aax3");

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));

        NoticeXml notice2 = new NoticeXml();
        notice2.setLeader("     gam0 22        450 ");
        notice2.setControlfields(Lists.newArrayList(ctrlPpn2, ctrlType2));

        Mockito.doNothing().when(issnService).checkFormat(ISSN_IDENTIFIER_OK);
        Mockito.when(issnService.getPpnFromIdentifiant(ISSN_IDENTIFIER_OK)).thenReturn(Lists.newArrayList(PPN_OK, "123456000"));
        Mockito.when(noticeService.getNoticeByPpn(PPN_OK)).thenReturn(notice);
        Mockito.when(noticeService.getNoticeByPpn("123456000")).thenReturn(notice2);

        this.mockMvc.perform(get("/api/v1/online_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_OK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[0].ppn").value(PPN_OK))
                .andExpect(jsonPath("$.ppns[0].providerPresent").value(false))
                .andExpect(jsonPath("$.erreurs[0]").value("Le PPN " + ctrlPpn2.getValue() + " n'est pas une ressource électronique"));
    }

    @Test
    @DisplayName("test WS online_identifier_2_ppn : serial + ISSN ok + exception erreur SQL")
    void onlineIdentifier2PpnCas3() throws Exception {

        Controlfield ctrlPpn = new Controlfield();
        ctrlPpn.setTag("001");
        ctrlPpn.setValue(PPN_OK);

        Controlfield ctrlType = new Controlfield();
        ctrlType.setTag("008");
        ctrlType.setValue("Oax3");

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));
        Mockito.doNothing().when(issnService).checkFormat(ISSN_IDENTIFIER_OK);
        Mockito.when(issnService.getPpnFromIdentifiant(ISSN_IDENTIFIER_OK)).thenReturn(Lists.newArrayList(PPN_OK));
        Mockito.doThrow(IOException.class).when(noticeService).getNoticeByPpn(Mockito.any());

        this.mockMvc.perform(get("/api/v1/online_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_OK))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue((result.getResolvedException() instanceof IOException)));
    }

    @Test
    @DisplayName("test WS online_identifier_2_ppn : Serial + ISSN au mauvais format")
    void onlineIdentifier2PpnCas4() throws Exception {


        Mockito.doThrow(IllegalArgumentException.class).when(issnService).checkFormat(ISSN_IDENTIFIER_KO);

        this.mockMvc.perform(get("/api/v1/online_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_KO))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue((result.getResolvedException() instanceof IllegalArgumentException)));
    }

    @Test
    @DisplayName("test WS online_identifier_2_ppn : check provider non ok")
    void onlineIdentifierCheckProviderNonOk() throws Exception {


        Controlfield ctrlPpn = new Controlfield();
        ctrlPpn.setTag("001");
        ctrlPpn.setValue(PPN_OK);

        Controlfield ctrlType = new Controlfield();
        ctrlType.setTag("008");
        ctrlType.setValue("Oax3");

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));

        ElementDto providerDto = new ElementDto("CAIRN", "CAIRN", 81);

        Mockito.when(providerService.getProviderDisplayName(Mockito.any())).thenReturn(Optional.of(providerDto));
        Mockito.doNothing().when(issnService).checkFormat(ISSN_IDENTIFIER_OK);
        Mockito.when(issnService.getPpnFromIdentifiant(ISSN_IDENTIFIER_OK)).thenReturn(Lists.newArrayList(PPN_OK));
        Mockito.when(noticeService.getNoticeByPpn(Mockito.any())).thenReturn(notice);

        this.mockMvc.perform(get("/api/v1/online_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_OK + "/" + PROVIDER_OK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[0].providerPresent").value(false));
    }

    @Test
    @DisplayName("test WS online_identifier_2_ppn : check provider diacritics")
    void onlineIdentifierCheckProviderDiacritics() throws Exception {


        String provider_ko = "CAèRN";

        Controlfield ctrlPpn = new Controlfield();
        ctrlPpn.setTag("001");
        ctrlPpn.setValue(PPN_OK);

        Controlfield ctrlType = new Controlfield();
        ctrlType.setTag("008");
        ctrlType.setValue("Oax3");

        Datafield datafield = new Datafield();
        datafield.setTag("210");
        SubField subField = new SubField();
        subField.setCode("c");
        subField.setValue("CAERN");
        datafield.setSubFields(Lists.newArrayList(subField));

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));
        notice.setDatafields(Lists.newArrayList(datafield));

        Mockito.doNothing().when(issnService).checkFormat(ISSN_IDENTIFIER_OK);
        Mockito.when(issnService.getPpnFromIdentifiant(ISSN_IDENTIFIER_OK)).thenReturn(Lists.newArrayList(PPN_OK));
        Mockito.when(noticeService.getNoticeByPpn(Mockito.any())).thenReturn(notice);
        Mockito.when(providerService.checkProviderDansNoticeGeneral(Mockito.any(), Mockito.any())).thenReturn(true);

        this.mockMvc.perform(get("/api/v1/online_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_OK + "/" + provider_ko))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[0].ppn").value(PPN_OK))
                .andExpect(jsonPath("$.ppns[0].providerPresent").value(true));
    }

    @Test
    @DisplayName("test WS online_identifier_2_ppn : erreur appel ws provider")
    void onlineIdentifer2PpnErreurAppelWs() throws Exception {


        Controlfield ctrlPpn = new Controlfield();
        ctrlPpn.setTag("001");
        ctrlPpn.setValue(PPN_OK);

        Controlfield ctrlType = new Controlfield();
        ctrlType.setTag("008");
        ctrlType.setValue("Oax3");

        Datafield datafield = new Datafield();
        datafield.setTag("210");
        SubField subField = new SubField();
        subField.setCode("c");
        subField.setValue("CAIRN");
        datafield.setSubFields(Lists.newArrayList(subField));

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));
        notice.setDatafields(Lists.newArrayList(datafield));

        Mockito.when(providerService.checkProviderDansNoticeGeneral(Mockito.any(), Mockito.any())).thenThrow(new IOException());
        Mockito.doNothing().when(issnService).checkFormat(ISSN_IDENTIFIER_OK);
        Mockito.when(issnService.getPpnFromIdentifiant(ISSN_IDENTIFIER_OK)).thenReturn(Lists.newArrayList(PPN_OK));
        Mockito.when(noticeService.getNoticeByPpn(Mockito.any())).thenReturn(notice);

        this.mockMvc.perform(get("/api/v1/online_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_OK + "/" + PROVIDER_OK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[0].ppn").value(PPN_OK))
                .andExpect(jsonPath("$.ppns[0].providerPresent").value(false))
                .andExpect(jsonPath("$.erreurs[0]").value("Impossible d'analyser le provider en raison d'un problème technique, poursuite du traitement"));
    }

    @Test
    @DisplayName("test WS print_identifier_2_ppn : serial + ISSN ok + 1 PPN non supprimé de doc imprimé")
    void printIdentifier2PpnCas1() throws Exception {


        Controlfield ctrlPpn = new Controlfield();
        ctrlPpn.setTag("001");
        ctrlPpn.setValue(PPN_OK);

        Controlfield ctrlType = new Controlfield();
        ctrlType.setTag("008");
        ctrlType.setValue("Aax3");

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));

        Mockito.doNothing().when(issnService).checkFormat(ISSN_IDENTIFIER_OK);
        Mockito.when(issnService.getPpnFromIdentifiant(ISSN_IDENTIFIER_OK)).thenReturn(Lists.newArrayList(PPN_OK));
        Mockito.when(noticeService.getNoticeByPpn(Mockito.any())).thenReturn(notice);

        this.mockMvc.perform(get("/api/v1/print_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_OK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[0].ppn").value(PPN_OK))
                .andExpect(jsonPath("$.ppns[0].providerPresent").value(false));
    }

    @Test
    @DisplayName("test WS print_identifier_2_ppn : serial + ISSN KO 0 ppn ne correspond")
    void printIdentifier2PpnCas0Ppn() throws Exception, IllegalPpnException {
        Mockito.doNothing().when(issnService).checkFormat(ISSN_IDENTIFIER_OK);
        Mockito.when(issnService.getPpnFromIdentifiant(ISSN_IDENTIFIER_OK)).thenReturn(Lists.newArrayList());

        this.mockMvc.perform(get("/api/v1/print_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_OK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.erreurs[0]").value("Aucun PPN ne correspond au " + ISSN_IDENTIFIER_OK));
    }

    @Test
    @DisplayName("test WS print_identifier_2_ppn : serial + ISSN ok + 1 PPN supprimé de doc imprimé")
    void printIdentifier2PpnCas1Supprime() throws Exception {
        Controlfield ctrlPpn = new Controlfield();
        ctrlPpn.setTag("001");
        ctrlPpn.setValue(PPN_OK);

        Controlfield ctrlType = new Controlfield();
        ctrlType.setTag("008");
        ctrlType.setValue("Aax3");

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     dam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));

        Mockito.doNothing().when(issnService).checkFormat(ISSN_IDENTIFIER_OK);
        Mockito.when(issnService.getPpnFromIdentifiant(ISSN_IDENTIFIER_OK)).thenReturn(Lists.newArrayList(PPN_OK));
        Mockito.when(noticeService.getNoticeByPpn(Mockito.any())).thenReturn(notice);

        this.mockMvc.perform(get("/api/v1/print_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_OK))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("test WS print_identifier_2_ppn : serial + ISSN ok + 2 PPN non supprimés dont un ppn qui n'est pas une notice imprimée")
    void printIdentifier2PpnCas2() throws Exception {
        Controlfield ctrlPpn = new Controlfield();
        ctrlPpn.setTag("001");
        ctrlPpn.setValue(PPN_OK);

        Controlfield ctrlType = new Controlfield();
        ctrlType.setTag("008");
        ctrlType.setValue("Oax3");

        Controlfield ctrlPpn2 = new Controlfield();
        ctrlPpn2.setTag("001");
        ctrlPpn2.setValue("123456000");

        Controlfield ctrlType2 = new Controlfield();
        ctrlType2.setTag("008");
        ctrlType2.setValue("Aax3");

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));

        NoticeXml notice2 = new NoticeXml();
        notice2.setLeader("     gam0 22        450 ");
        notice2.setControlfields(Lists.newArrayList(ctrlPpn2, ctrlType2));

        Mockito.doNothing().when(issnService).checkFormat(ISSN_IDENTIFIER_OK);
        Mockito.when(issnService.getPpnFromIdentifiant(ISSN_IDENTIFIER_OK)).thenReturn(Lists.newArrayList(PPN_OK, "123456000"));
        Mockito.when(noticeService.getNoticeByPpn(PPN_OK)).thenReturn(notice);
        Mockito.when(noticeService.getNoticeByPpn("123456000")).thenReturn(notice2);

        this.mockMvc.perform(get("/api/v1/print_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_OK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[1].ppn").value(notice2.getPpn()))
                .andExpect(jsonPath("$.ppns[1].providerPresent").value(false))
                .andExpect(jsonPath("$.ppns[1].typeSupport").value(notice2.getTypeSupport().toString()))
                .andExpect(jsonPath("$.ppns[0].ppn").value(notice.getPpn()))
                .andExpect(jsonPath("$.ppns[0].typeSupport").value(notice.getTypeSupport().toString()))
                .andExpect(jsonPath("$.ppns[0].providerPresent").value(false));
    }

    @Test
    @DisplayName("test WS print_identifier_2_ppn : serial + ISSN ok + exception erreur SQL")
    void printIdentifier2PpnCas3() throws Exception {
        Controlfield ctrlPpn = new Controlfield();
        ctrlPpn.setTag("001");
        ctrlPpn.setValue(PPN_OK);

        Controlfield ctrlType = new Controlfield();
        ctrlType.setTag("008");
        ctrlType.setValue("Oax3");

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));
        Mockito.doNothing().when(issnService).checkFormat(ISSN_IDENTIFIER_OK);
        Mockito.when(issnService.getPpnFromIdentifiant(ISSN_IDENTIFIER_OK)).thenReturn(Lists.newArrayList(PPN_OK));
        Mockito.doThrow(IOException.class).when(noticeService).getNoticeByPpn(Mockito.any());

        this.mockMvc.perform(get("/api/v1/print_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_OK))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue((result.getResolvedException() instanceof IOException)));
    }

    @Test
    @DisplayName("test WS print_identifier_2_ppn : Serial + ISSN au mauvais format")
    void printIdentifier2PpnCas4() throws Exception {
        Mockito.doThrow(IllegalArgumentException.class).when(issnService).checkFormat(ISSN_IDENTIFIER_KO);

        this.mockMvc.perform(get("/api/v1/print_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_KO))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue((result.getResolvedException() instanceof IllegalArgumentException)));
    }

    @Test
    @DisplayName("test WS print_identifier_to_ppn : Serial au mauvais format")
    void printIdentifier2PpnCas5() throws Exception {
        String type_ko = "test";

        this.mockMvc.perform(get("/api/v1/print_identifier_2_ppn/" + type_ko + "/" + ISSN_IDENTIFIER_KO))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue((result.getResolvedException() instanceof IllegalArgumentException)));
    }

    @Test
    @DisplayName("test WS print_identifer_2_ppn : erreur récupération provider")
    void printIdentifer2PpnErreurAppelWs() throws Exception {
        Controlfield ctrlPpn = new Controlfield();
        ctrlPpn.setTag("001");
        ctrlPpn.setValue(PPN_OK);

        Controlfield ctrlType = new Controlfield();
        ctrlType.setTag("008");
        ctrlType.setValue("Aax3");

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn, ctrlType));

        Controlfield ctrlPpn2 = new Controlfield();
        ctrlPpn2.setTag("001");
        ctrlPpn2.setValue("987654321");

        Controlfield ctrlType2 = new Controlfield();
        ctrlType2.setTag("008");
        ctrlType2.setValue("Oax3");

        NoticeXml notice2 = new NoticeXml();
        notice2.setLeader("     gam0 22        450 ");
        notice2.setControlfields(Lists.newArrayList(ctrlPpn2, ctrlType2));

        Mockito.when(providerService.checkProviderDansNoticeGeneral(Mockito.any(), Mockito.any())).thenThrow(new IOException());
        Mockito.doNothing().when(issnService).checkFormat(ISSN_IDENTIFIER_OK);
        Mockito.when(issnService.getPpnFromIdentifiant(ISSN_IDENTIFIER_OK)).thenReturn(Lists.newArrayList(PPN_OK));
        Mockito.when(noticeService.getEquivalentElectronique(Mockito.any())).thenReturn(Lists.newArrayList("987654321"));
        Mockito.when(noticeService.getNoticeByPpn(PPN_OK)).thenReturn(notice);
        Mockito.when(noticeService.getNoticeByPpn("987654321")).thenReturn(notice2);

        this.mockMvc.perform(get("/api/v1/print_identifier_2_ppn/" + SERIAL + "/" + ISSN_IDENTIFIER_OK + "/" + PROVIDER_OK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[0].ppn").value("987654321"))
                .andExpect(jsonPath("$.ppns[0].providerPresent").value(false))
                .andExpect(jsonPath("$.erreurs[0]").value("Impossible d'analyser le provider en raison d'un problème technique, poursuite du traitement"));
    }

    @Test
    void doiIdentifier2ppnCasOk() throws Exception {
        Controlfield ctrlPpn2 = new Controlfield();
        ctrlPpn2.setTag("001");
        ctrlPpn2.setValue(PPN_OK);

        Controlfield ctrlType2 = new Controlfield();
        ctrlType2.setTag("008");
        ctrlType2.setValue("Oax3");

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn2, ctrlType2));

        ElementDto providerDto = new ElementDto(PROVIDER_OK, "CAIRN", 81);

        Mockito.when(providerService.getProviderDisplayName(Mockito.any())).thenReturn(Optional.of(providerDto));
        Mockito.when(doiService.getPpnFromIdentifiant(DOI_OK)).thenReturn(Lists.newArrayList(PPN_OK));
        Mockito.when(noticeService.getNoticeByPpn(PPN_OK)).thenReturn(notice);
        Mockito.doNothing().when(doiService).checkFormat(Mockito.anyString());
        Mockito.when(this.providerService.checkProviderDansNoticeGeneral(Mockito.any(), Mockito.any())).thenReturn(true);

        this.mockMvc.perform(get("/api/v1/doi_identifier_2_ppn?doi=" + DOI_OK + "&provider=" + PROVIDER_OK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[0].ppn").value(PPN_OK))
                .andExpect(jsonPath("$.ppns[0].typeSupport").value("ELECTRONIQUE"))
                .andExpect(jsonPath("$.ppns[0].providerPresent").value(true));
    }

    @Test
    void doiIdentifier2ppnErreurFormat() throws Exception {
        String doi_ko = "dmljksdlmgkjdfmgkljglmf";


        ElementDto providerDto = new ElementDto(PROVIDER_OK, "CAIRN", 81);

        Mockito.when(providerService.getProviderDisplayName(Mockito.any())).thenReturn(Optional.of(providerDto));
        Mockito.doThrow(new IllegalArgumentException(DoiService.MESSAGE_ERROR_DOI_FORMAT)).when(doiService).checkFormat(Mockito.anyString());

        this.mockMvc.perform(get("/api/v1/doi_identifier_2_ppn?doi=" + doi_ko + "&provider=" + PROVIDER_OK))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue((result.getResolvedException() instanceof IllegalArgumentException)));
    }

    @Test
    void doiIdentifier2ppnNoPpnFound() throws Exception {
        ElementDto providerDto = new ElementDto(PROVIDER_OK, "CAIRN", 81);

        Mockito.when(providerService.getProviderDisplayName(Mockito.any())).thenReturn(Optional.of(providerDto));
        Mockito.doNothing().when(doiService).checkFormat(Mockito.anyString());
        Mockito.when(doiService.getPpnFromIdentifiant(DOI_OK)).thenThrow(new IllegalPpnException("Aucune notice ne correspond à la recherche"));

        this.mockMvc.perform(get("/api/v1/doi_identifier_2_ppn?doi=" + DOI_OK + "&provider=" + PROVIDER_OK))
                .andExpect(status().isOk());
    }

    @Test
    void doiIdentifier2ppnErreurProvider() throws Exception {
        Controlfield ctrlPpn2 = new Controlfield();
        ctrlPpn2.setTag("001");
        ctrlPpn2.setValue(PPN_OK);

        Controlfield ctrlType2 = new Controlfield();
        ctrlType2.setTag("008");
        ctrlType2.setValue("Oax3");

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn2, ctrlType2));

        ElementDto providerDto = new ElementDto(PROVIDER_OK, "CAIRN", 81);

        Mockito.when(providerService.getProviderDisplayName(Mockito.any())).thenReturn(Optional.of(providerDto));
        Mockito.when(doiService.getPpnFromIdentifiant(DOI_OK)).thenReturn(Lists.newArrayList(PPN_OK));
        Mockito.when(noticeService.getNoticeByPpn(PPN_OK)).thenReturn(notice);
        Mockito.doNothing().when(doiService).checkFormat(Mockito.anyString());
        Mockito.when(this.providerService.checkProviderDansNoticeGeneral(Mockito.any(), Mockito.any())).thenThrow(new IOException());

        this.mockMvc.perform(get("/api/v1/doi_identifier_2_ppn?doi=" + DOI_OK + "&provider=" + PROVIDER_OK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ppns[0].ppn").value(PPN_OK))
                .andExpect(jsonPath("$.ppns[0].typeSupport").value("ELECTRONIQUE"))
                .andExpect(jsonPath("$.ppns[0].providerPresent").value(false));
    }

    @Test
    void doiIdentifier2ppnErreurTypeSupportNotice() throws Exception {
        Controlfield ctrlPpn2 = new Controlfield();
        ctrlPpn2.setTag("001");
        ctrlPpn2.setValue(PPN_OK);

        Controlfield ctrlType2 = new Controlfield();
        ctrlType2.setTag("008");
        ctrlType2.setValue("Aax3");

        NoticeXml notice = new NoticeXml();
        notice.setLeader("     gam0 22        450 ");
        notice.setControlfields(Lists.newArrayList(ctrlPpn2, ctrlType2));

        ElementDto providerDto = new ElementDto(PROVIDER_OK, "CAIRN", 81);

        Mockito.when(providerService.getProviderDisplayName(Mockito.any())).thenReturn(Optional.of(providerDto));
        Mockito.when(doiService.getPpnFromIdentifiant(DOI_OK)).thenReturn(Lists.newArrayList(PPN_OK));
        Mockito.when(noticeService.getNoticeByPpn(PPN_OK)).thenReturn(notice);
        Mockito.doNothing().when(doiService).checkFormat(Mockito.anyString());

        this.mockMvc.perform(get("/api/v1/doi_identifier_2_ppn?doi=" + DOI_OK + "&provider=" + PROVIDER_OK))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.erreurs[0]").value("Le PPN " + notice.getPpn() + " n'est pas une ressource électronique"));
    }
}
