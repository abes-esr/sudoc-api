package fr.abes.sudoc.service;

import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.configuration.UtilsConfig;
import fr.abes.sudoc.entity.BiblioTableFrbr4XX;
import fr.abes.sudoc.entity.NoticesBibio;
import fr.abes.sudoc.entity.notice.Datafield;
import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.entity.notice.SubField;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.exception.ZoneNotFoundException;
import fr.abes.sudoc.repository.BiblioTableFrbr4XXRepository;
import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;

import javax.sql.rowset.serial.SerialClob;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {NoticeService.class, UtilsConfig.class})
class NoticeServiceTest {
    @Autowired
    private NoticeService service;

    @MockBean
    private BaseXmlFunctionsCaller baseXmlFunctionsCaller;

    @MockBean
    private BiblioTableFrbr4XXRepository biblioTableFrbr4XXRepository;

    @Value("classpath:143519379.xml")
    private Resource xmlNoticeBiblio;

    @Value("classpath:123456789.xml")
    private Resource xmlNoticeImprimee;

    @Value("classpath:987654321.xml")
    private Resource xmlNoticeElect;

    @Value("classpath:noticeWith452.xml")
    private Resource noticeWith452;


    @Test
    void getNoticeByPpn() throws SQLException, IOException, IllegalPpnException {
        String ppn = "143519379";
        String xml = IOUtils.toString(new FileInputStream(xmlNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        NoticesBibio notice = new NoticesBibio();
        notice.setId(1);
        notice.setPpn(ppn);
        notice.setDataXml(new SerialClob(xml.toCharArray()));

        //cas ou le service renvoie une notice bibliographique
        Mockito.when(baseXmlFunctionsCaller.findByPpn("143519379")).thenReturn(Optional.of(notice));
        Assertions.assertEquals(notice.getPpn(), service.getNoticeByPpn(ppn).getPpn());

        //cas ou le ppn en entrée est null
        assertThrows(IllegalPpnException.class, () -> service.getNoticeByPpn(null));
    }

    @Test
    void getNoticeElectroniqueLiee() throws IOException, SQLException, IllegalPpnException, ZoneNotFoundException {
        List<String> ppn = new ArrayList<>();
        ppn.add("123456789");
        ppn.add("987654321");

        String xml = IOUtils.toString(new FileInputStream(xmlNoticeImprimee.getFile()), StandardCharsets.UTF_8);
        NoticesBibio noticeImprimee = new NoticesBibio();
        noticeImprimee.setId(1);
        noticeImprimee.setPpn("123456789");
        noticeImprimee.setDataXml(new SerialClob(xml.toCharArray()));

        xml = IOUtils.toString(new FileInputStream(xmlNoticeElect.getFile()), StandardCharsets.UTF_8);
        NoticesBibio noticeElect = new NoticesBibio();
        noticeElect.setId(1);
        noticeElect.setPpn("987654321");
        noticeElect.setDataXml(new SerialClob(xml.toCharArray()));

        Mockito.when(baseXmlFunctionsCaller.findByPpn("123456789")).thenReturn(Optional.of(noticeImprimee));
        Mockito.when(baseXmlFunctionsCaller.findByPpn("987654321")).thenReturn(Optional.of(noticeElect));

        List<String> result = service.getNoticeElectroniqueLiee(ppn);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("987654321", result.get(0));
    }

    @Test
    @DisplayName("test getEquivalentElectronique : première condition")
    void getEquivalentElectroniqueFirstCondition() throws IOException, SQLException, IllegalPpnException, ZoneNotFoundException {
        String xml = IOUtils.toString(new FileInputStream(noticeWith452.getFile()), StandardCharsets.UTF_8);
        NoticesBibio noticeWith452 = new NoticesBibio();
        noticeWith452.setId(1);
        noticeWith452.setPpn("111111111");
        noticeWith452.setDataXml(new SerialClob(xml.toCharArray()));

        NoticeXml noticeSource = new NoticeXml();
        Datafield datafield = new Datafield();
        datafield.setTag("452");
        SubField subField = new SubField();
        subField.setCode("0");
        subField.setValue("111111111");
        datafield.setSubFields(Lists.newArrayList(subField));
        noticeSource.setDatafields(Lists.newArrayList(datafield));

        Mockito.when(baseXmlFunctionsCaller.findByPpn("111111111")).thenReturn(Optional.of(noticeWith452));

        List<String> ppnLiees = service.getEquivalentElectronique(noticeSource);
        Assertions.assertFalse(ppnLiees.isEmpty());
        Assertions.assertEquals(1, ppnLiees.size());
        Assertions.assertEquals("111111111", ppnLiees.get(0));

    }

    @Test
    @DisplayName("test getEquivalentElectronique : seconde condition")
    void getEquivalentElectroniqueSecondeCondition() throws IOException, SQLException, IllegalPpnException, ZoneNotFoundException {
        String xml = IOUtils.toString(new FileInputStream(noticeWith452.getFile()), StandardCharsets.UTF_8);
        NoticesBibio noticeWith452 = new NoticesBibio();
        noticeWith452.setId(1);
        noticeWith452.setPpn("111111111");
        noticeWith452.setDataXml(new SerialClob(xml.toCharArray()));

        NoticeXml noticeSource = new NoticeXml();
        Datafield datafield = new Datafield();
        datafield.setTag("456");
        SubField subField = new SubField();
        subField.setCode("0");
        subField.setValue("111111111");
        datafield.setSubFields(Lists.newArrayList(subField));
        noticeSource.setDatafields(Lists.newArrayList(datafield));

        Mockito.when(baseXmlFunctionsCaller.findByPpn("111111111")).thenReturn(Optional.of(noticeWith452));

        List<String> ppnLiees = service.getEquivalentElectronique(noticeSource);
        Assertions.assertFalse(ppnLiees.isEmpty());
        Assertions.assertEquals(1, ppnLiees.size());
        Assertions.assertEquals("111111111", ppnLiees.get(0));

    }

    @Test
    @DisplayName("test getEquivalentElectronique : BiblioTableFrBr4XX first condition")
    void getEquivalentElectroniqueBiblioTableFrBr4XXFirstCondition() throws IOException, SQLException, IllegalPpnException, ZoneNotFoundException {
        String xml = IOUtils.toString(new FileInputStream(noticeWith452.getFile()), StandardCharsets.UTF_8);
        NoticesBibio noticeWith452 = new NoticesBibio();
        noticeWith452.setId(1);
        noticeWith452.setPpn("111111111");
        noticeWith452.setDataXml(new SerialClob(xml.toCharArray()));

        //notice sans rien, car le but est de chercher la notice dans la table FrBr4XX via le ppn de la notice source
        NoticeXml noticeSource = new NoticeXml();
        BiblioTableFrbr4XX biblioTableFrbr4XX = new BiblioTableFrbr4XX();
        biblioTableFrbr4XX.setPpn(noticeWith452.getPpn());

        Mockito.when(biblioTableFrbr4XXRepository.findAllByTagAndDatas("452$0", noticeSource.getPpn())).thenReturn(Collections.singletonList(biblioTableFrbr4XX));

        List<String> ppnLiees = service.getEquivalentElectronique(noticeSource);
        Assertions.assertFalse(ppnLiees.isEmpty());
        Assertions.assertEquals(1, ppnLiees.size());
        Assertions.assertEquals("111111111", ppnLiees.get(0));

    }

    @Test
    @DisplayName("test getEquivalentElectronique : BiblioTableFrBr4XX second condition")
    void getEquivalentElectroniqueBiblioTableFrBr4XXSecondCondition() throws IOException, SQLException, IllegalPpnException, ZoneNotFoundException {
        String xml = IOUtils.toString(new FileInputStream(noticeWith452.getFile()), StandardCharsets.UTF_8);
        NoticesBibio noticeWith452 = new NoticesBibio();
        noticeWith452.setId(1);
        noticeWith452.setPpn("111111111");
        noticeWith452.setDataXml(new SerialClob(xml.toCharArray()));

        NoticeXml noticeSource = new NoticeXml();
        BiblioTableFrbr4XX biblioTableFrbr4XX = new BiblioTableFrbr4XX();
        biblioTableFrbr4XX.setPpn(noticeWith452.getPpn());

        Mockito.when(biblioTableFrbr4XXRepository.findAllByTagAndDatas("452$0", noticeSource.getPpn())).thenReturn(new ArrayList<>());
        Mockito.when(biblioTableFrbr4XXRepository.findAllByTagAndDatas("455$0", noticeSource.getPpn())).thenReturn(Collections.singletonList(biblioTableFrbr4XX));

        List<String> ppnLiees = service.getEquivalentElectronique(noticeSource);
        Assertions.assertFalse(ppnLiees.isEmpty());
        Assertions.assertEquals(1, ppnLiees.size());
        Assertions.assertEquals("111111111", ppnLiees.get(0));

    }

    @Test
    @DisplayName("test getEquivalentElectronique : Nothing condition")
    void getEquivalentElectroniqueNothingCondition() throws IOException, SQLException, IllegalPpnException, ZoneNotFoundException {
        String xml = IOUtils.toString(new FileInputStream(noticeWith452.getFile()), StandardCharsets.UTF_8);
        NoticesBibio noticeWith452 = new NoticesBibio();
        noticeWith452.setId(1);
        noticeWith452.setPpn("111111111");
        noticeWith452.setDataXml(new SerialClob(xml.toCharArray()));

        NoticeXml noticeSource = new NoticeXml();

        Mockito.when(biblioTableFrbr4XXRepository.findAllByTagAndDatas("452$0", noticeSource.getPpn())).thenReturn(new ArrayList<>());
        Mockito.when(biblioTableFrbr4XXRepository.findAllByTagAndDatas("456$0", noticeSource.getPpn())).thenReturn(new ArrayList<>());

        List<String> ppnLiees = service.getEquivalentElectronique(noticeSource);
        Assertions.assertTrue(ppnLiees.isEmpty());

    }
}
