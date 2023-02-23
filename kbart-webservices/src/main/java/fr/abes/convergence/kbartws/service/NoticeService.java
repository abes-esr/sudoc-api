package fr.abes.convergence.kbartws.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.convergence.kbartws.entity.NoticesBibio;
import fr.abes.convergence.kbartws.entity.notice.NoticeXml;
import fr.abes.convergence.kbartws.exception.IllegalPpnException;
import fr.abes.convergence.kbartws.repository.NoticesBibioRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class NoticeService {
    private final NoticesBibioRepository repository;

    private final XmlMapper xmlMapper;

    public NoticeService(NoticesBibioRepository repository, XmlMapper xmlMapper) {
        this.repository = repository;
        this.xmlMapper = xmlMapper;
    }

    public NoticesBibio getNoticeByPpn(String ppn) throws IllegalPpnException {
        if (ppn == null)
            throw new IllegalPpnException("Le PPN ne peut pas être null");
        Optional<NoticesBibio> noticeOpt = this.repository.findByPpn(ppn);
        return noticeOpt.orElse(null);
    }

    public NoticeXml getNoticeXmlFromNoticeBibio(NoticesBibio noticesBibio) throws SQLException, IOException {
        return xmlMapper.readValue(noticesBibio.getDataXml().getCharacterStream(), NoticeXml.class);
    }
}
