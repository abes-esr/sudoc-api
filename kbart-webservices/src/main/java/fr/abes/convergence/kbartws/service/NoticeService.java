package fr.abes.convergence.kbartws.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.convergence.kbartws.entity.NoticeBibio;
import fr.abes.convergence.kbartws.entity.notice.NoticeXml;
import fr.abes.convergence.kbartws.repository.NoticeBibioRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class NoticeService {
    private final NoticeBibioRepository repository;

    private final XmlMapper xmlMapper;

    public NoticeService(NoticeBibioRepository repository, XmlMapper xmlMapper) {
        this.repository = repository;
        this.xmlMapper = xmlMapper;
    }

    public NoticeBibio getNoticeByPpn(String ppn) {
        Optional<NoticeBibio> noticeOpt = this.repository.findByPpn(ppn);
        return noticeOpt.orElse(null);
    }

    public NoticeXml getNoticeXmlFromNoticeBibio(NoticeBibio noticeBibio) throws SQLException, IOException {
        return xmlMapper.readValue(noticeBibio.getDataXml().getCharacterStream(), NoticeXml.class);
    }
}
