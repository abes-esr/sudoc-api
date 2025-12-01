package fr.abes.sudoc.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.entity.BiblioTableFrbr4XX;
import fr.abes.sudoc.entity.NoticesBibio;
import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.exception.ZoneNotFoundException;
import fr.abes.sudoc.repository.BiblioTableFrbr4XXRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NoticeService {

    private final BiblioTableFrbr4XXRepository biblioTableFrbr4XXRepository;

    private final XmlMapper xmlMapper;
    private final BaseXmlFunctionsCaller baseXmlFunctionsCaller;

    public NoticeService(BiblioTableFrbr4XXRepository biblioTableFrbr4XXRepository, XmlMapper xmlMapper, BaseXmlFunctionsCaller baseXmlFunctionsCaller) {
        this.biblioTableFrbr4XXRepository = biblioTableFrbr4XXRepository;
        this.xmlMapper = xmlMapper;
        this.baseXmlFunctionsCaller = baseXmlFunctionsCaller;
    }


    public NoticeXml getNoticeByPpn(String ppn) throws IllegalPpnException, IOException {
        if (ppn == null)
            throw new IllegalPpnException("Le PPN ne peut pas être null");
        //Optional<NoticesBibio> noticeOpt = this.noticesBibioRepository.findByPpn(ppn);
        Optional<NoticesBibio> noticeOpt = baseXmlFunctionsCaller.findByPpn(ppn);
        if (noticeOpt.isEmpty()) {
            return null;
        }
        Clob clob = noticeOpt.get().getDataXml();
        String xmlString = null;
        
        try (Reader reader = clob.getCharacterStream()){
            xmlString = new BufferedReader(reader)
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (SQLException e) {
            log.error(e.getMessage());
        } finally {
            try {
                clob.free();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        
        return (xmlString != null) ? xmlMapper.readValue(xmlString, NoticeXml.class) : null;
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
            if (noticeLiee == null) {
                throw new IllegalPpnException("Le ppn lié " + ppn + " ne retourne pas de notice");
            } else if (noticeLiee.isNoticeElectronique()) {
                ppns.add(ppn);
            }
        }
        return ppns;
    }
}
