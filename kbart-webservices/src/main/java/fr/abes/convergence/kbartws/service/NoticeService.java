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

    public NoticeXml getNoticeByPpn(String ppn) throws IllegalPpnException, IOException {
        if (ppn == null)
            throw new IllegalPpnException("Le PPN ne peut pas être null");
        Optional<NoticesBibio> noticeOpt = this.repository.findByPpn(ppn);
        if(noticeOpt.isPresent()){
            try{
                return xmlMapper.readValue(noticeOpt.get().getDataXml().getCharacterStream(), NoticeXml.class);
            }catch (SQLException ex){
                throw new IOException(ex);
            }
        }
        return null;
    }
}
