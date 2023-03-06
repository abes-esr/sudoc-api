package fr.abes.convergence.kbartws.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.convergence.kbartws.entity.BiblioTableFrbr4XX;
import fr.abes.convergence.kbartws.entity.NoticesBibio;
import fr.abes.convergence.kbartws.entity.notice.NoticeXml;
import fr.abes.convergence.kbartws.exception.IllegalPpnException;
import fr.abes.convergence.kbartws.repository.BiblioTableFrbr4XXRepository;
import fr.abes.convergence.kbartws.repository.NoticesBibioRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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

    public List<String> getEquivalentElectronique(NoticeXml notice) throws IOException, IllegalPpnException {
        List<String> ppnlies;

        //on cherche une 452$0 dans la notice
        List<String> ppn452 = notice.get4XXDollar0("452");
        ppnlies = getNoticeElectroniqueLiee(ppn452);
        if (ppnlies.size() > 0) return ppnlies;

        //on cherche une 455$0 dans la notice
        List<String> ppn455 = notice.get4XXDollar0("455");
        ppnlies = getNoticeElectroniqueLiee(ppn455);
        if (ppnlies.size() > 0) return ppnlies;

        //Si pas de résultat trouvé, on interroge la table biblio_table_frbr_4XX
        ppnlies.addAll(this.biblioTableFrbr4XXRepository.findPpnByTagAndDatas("452$0", notice.getPpn()));
        //on renvoie les ppn trouvés via la requête
        if (ppnlies.size() > 0) return ppnlies;

        ppnlies.addAll(this.biblioTableFrbr4XXRepository.findPpnByTagAndDatas("456$0", notice.getPpn()));
        //on renvoie les ppn trouvés via la requête
        if (ppnlies.size() > 0) return ppnlies;

        //aucune des conditions n'a été respectée, on renvoie une liste vide
        return new ArrayList<>();
    }

    public List<String> getNoticeElectroniqueLiee(List<String> ppn4XX) throws IllegalPpnException, IOException {
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
