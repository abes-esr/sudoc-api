package fr.abes.sudoc.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.sudoc.entity.BiblioTableFrbr4XX;
import fr.abes.sudoc.entity.NoticesBibio;
import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.exception.ZoneNotFoundException;
import fr.abes.sudoc.repository.BiblioTableFrbr4XXRepository;
import fr.abes.sudoc.repository.NoticesBibioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Service
@Slf4j
public class NoticeService {
    private final NoticesBibioRepository noticesBibioRepository;

    private final BiblioTableFrbr4XXRepository biblioTableFrbr4XXRepository;

    private final XmlMapper xmlMapper;

    public NoticeService(NoticesBibioRepository noticesBibioRepository, BiblioTableFrbr4XXRepository biblioTableFrbr4XXRepository, XmlMapper xmlMapper) {
        this.noticesBibioRepository = noticesBibioRepository;
        this.biblioTableFrbr4XXRepository = biblioTableFrbr4XXRepository;
        this.xmlMapper = xmlMapper;
    }


    public NoticeXml getNoticeByPpn(String ppn) throws IllegalPpnException, IOException {
        if (ppn == null)
            throw new IllegalPpnException("Le PPN ne peut pas être null");
        Optional<NoticesBibio> noticeOpt = this.noticesBibioRepository.findByPpn(ppn);
        if (noticeOpt.isPresent()) {
            try {
                return xmlMapper.readValue(noticeOpt.get().getDataXml().getCharacterStream(), NoticeXml.class);
            } catch (SQLException ex) {
                throw new IOException(ex);
            }
        }
        return null;
    }

    public List<String> getEquivalentElectronique(NoticeXml notice) throws IOException, IllegalPpnException, ZoneNotFoundException {
        //on cherche une 452$0 dans la notice
        List<String> ppn452 = notice.get4XXDollar0("452");
        Set<String> ppnlies = new HashSet<>(getNoticeElectroniqueLiee(ppn452));

        //on cherche une 456$0 dans la notice
        List<String> ppn456 = notice.get4XXDollar0("456");
        ppnlies.addAll(getNoticeElectroniqueLiee(ppn456));

        //Si pas de résultat trouvé, on interroge la table biblio_table_frbr_4XX
        ppnlies.addAll(this.biblioTableFrbr4XXRepository.findAllByTagAndDatas("452$0", notice.getPpn()).stream().map(BiblioTableFrbr4XX::getPpn).toList());

        ppnlies.addAll(this.biblioTableFrbr4XXRepository.findAllByTagAndDatas("455$0", notice.getPpn()).stream().map(BiblioTableFrbr4XX::getPpn).toList());

        //aucune des conditions n'a été respectée, on renvoie une liste vide
        return ppnlies.stream().toList();
    }

    public List<String> getNoticeElectroniqueLiee(List<String> ppn4XX) throws IllegalPpnException, IOException, ZoneNotFoundException {
        List<String> ppns = new ArrayList<>();
        for (String ppn : ppn4XX) {
            NoticeXml noticeLiee = getNoticeByPpn(ppn);
            if (noticeLiee.isNoticeElectronique()) {
                ppns.add(ppn);
            }
        }
        return ppns;
    }
}
