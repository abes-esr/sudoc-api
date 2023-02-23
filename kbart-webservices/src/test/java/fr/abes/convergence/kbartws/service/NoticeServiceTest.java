package fr.abes.convergence.kbartws.service;

import fr.abes.convergence.kbartws.configuration.BaseXMLOracleConfig;
import fr.abes.convergence.kbartws.configuration.BaseXmlMapper;
import fr.abes.convergence.kbartws.entity.NoticesBibio;
import fr.abes.convergence.kbartws.entity.notice.NoticeXml;
import fr.abes.convergence.kbartws.exception.IllegalPpnException;
import fr.abes.convergence.kbartws.repository.NoticesBibioRepository;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {NoticeService.class, BaseXmlMapper.class})
class NoticeServiceTest {
    @Autowired
    private NoticeService service;

    @MockBean
    private NoticesBibioRepository repositoryBiblio;

    @Value("classpath:143519379.xml")
    private Resource xmlNoticeBiblio;

    @Test
    void getNoticeByPpn() throws SQLException, IOException, IllegalPpnException {
        String ppn = "143519379";
        String xml = IOUtils.toString(new FileInputStream(xmlNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        NoticesBibio notice = new NoticesBibio();
        notice.setId(1);
        notice.setPpn(ppn);
        notice.setDataXml(new SerialClob(xml.toCharArray()));

        //cas ou le service renvoie une notice bibliographique
        Mockito.when(repositoryBiblio.findByPpn("143519379")).thenReturn(Optional.of(notice));
        Assertions.assertEquals(notice.getId(), service.getNoticeByPpn(ppn).getId());
        Assertions.assertEquals(notice.getPpn(), service.getNoticeByPpn(ppn).getPpn());

        //cas ou le ppn en entrée est null
        assertThrows(IllegalPpnException.class, () -> service.getNoticeByPpn(null));
    }

    @Test
    void getNoticeXmlFromNoticeBibio() throws IOException, SQLException {
        NoticesBibio notice = new NoticesBibio();
        String xml = IOUtils.toString(new FileInputStream(xmlNoticeBiblio.getFile()), StandardCharsets.UTF_8);
        notice.setId(1);
        notice.setPpn("143519379");
        notice.setDataXml(new SerialClob(xml.toCharArray()));

        NoticeXml result = service.getNoticeXmlFromNoticeBibio(notice);
        Assertions.assertEquals(notice.getPpn(), result.getPpn());
    }
}