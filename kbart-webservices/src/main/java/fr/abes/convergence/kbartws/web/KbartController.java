package fr.abes.convergence.kbartws.web;

import fr.abes.convergence.kbartws.entity.NoticeBibio;
import fr.abes.convergence.kbartws.entity.notice.NoticeXml;
import fr.abes.convergence.kbartws.service.*;
import fr.abes.convergence.kbartws.utils.TYPE_ID;
import fr.abes.convergence.kbartws.utils.Utilitaire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class KbartController {
    private IdentifiantFactory factory;

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/v1/online_identifier_2_ppn/{type}/{onlineIdentifier}")
    public List<String> onlineIdentifier2Ppn(@PathVariable String type, @PathVariable String onlineIdentifier) throws IOException {
        TYPE_ID enumType = Utilitaire.getEnumFromString(type);
        List<String> resultats = new ArrayList<>();
        List<String> erreurs = new ArrayList<>();
        IIdentifiantService service = factory.getService(enumType);
        if (service.checkFormat(onlineIdentifier)) {
            for (String ppn : service.getPpnFromIdentifiant(onlineIdentifier)) {
                NoticeXml notice;
                try {
                    notice = noticeService.getNoticeXmlFromNoticeBibio(noticeService.getNoticeByPpn(ppn));
                    if (!notice.isDeleted()) {
                        if (notice.isNoticeElectronique()) {
                            resultats.add(notice.getPpn());
                        } else {
                            erreurs.add("Le PPN " + notice.getPpn() + " n'est pas une ressource électronique");
                        }
                    }
                } catch (SQLException | IOException ex) {
                    log.error("erreur dans la récupération de la notice XML");
                    throw new IOException(ex);
                }
            }
        } else {
            throw new IllegalArgumentException("Le format de l'" + enumType.name() + " " + onlineIdentifier + " est incorrect");
        }
        return resultats;
    }

    /**
     * Supprimera tous les caractères différents de 0-9, X, x. Supprimera le - d'un issn.
     *
     * @param onlineIdentifier identifiant reçu en entrée, isbn ou issn
     * @return un identifiant ne pouvant comporter que des chiffres, X, x
     */
    private String normalizeOnlineIdentifier(String onlineIdentifier) {
        return onlineIdentifier.replaceAll("[^\\d|X|x]", "");
    }
}
