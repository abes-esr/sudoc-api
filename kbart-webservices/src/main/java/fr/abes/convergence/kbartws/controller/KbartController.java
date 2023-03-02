package fr.abes.convergence.kbartws.controller;

import fr.abes.convergence.kbartws.dto.ResultWsDto;
import fr.abes.convergence.kbartws.entity.notice.NoticeXml;
import fr.abes.convergence.kbartws.exception.IllegalPpnException;
import fr.abes.convergence.kbartws.service.IIdentifiantService;
import fr.abes.convergence.kbartws.service.IdentifiantFactory;
import fr.abes.convergence.kbartws.service.NoticeService;
import fr.abes.convergence.kbartws.utils.TYPE_ID;
import fr.abes.convergence.kbartws.utils.Utilitaire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
@RequestMapping("/v1")
public class KbartController {
    @Autowired
    private IdentifiantFactory factory;

    @Autowired
    private NoticeService noticeService;

    @GetMapping(value = "/online_identifier_2_ppn/{type}/{onlineIdentifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWsDto onlineIdentifier2Ppn(@PathVariable String type, @PathVariable String onlineIdentifier) throws IOException {
        TYPE_ID enumType = Utilitaire.getEnumFromString(type);
        ResultWsDto resultat = new ResultWsDto();
        IIdentifiantService service = factory.getService(enumType);
        if (service.checkFormat(onlineIdentifier)) {
            try {
                for (String ppn : service.getPpnFromIdentifiant(onlineIdentifier)) {
                    NoticeXml notice = noticeService.getNoticeByPpn(ppn);
                    if (!notice.isDeleted()) {
                        if (notice.isNoticeElectronique()) {
                            resultat.addPpn(notice.getPpn());
                        } else {
                            resultat.addErreur("Le PPN " + notice.getPpn() + " n'est pas une ressource électronique");
                        }
                    }
                }
            } catch (IOException | IllegalPpnException ex) {
                log.error("erreur dans la récupération de la notice correspondant à l'identifiant " + onlineIdentifier);
                throw new IOException(ex);
            }
        } else {
            throw new IllegalArgumentException("Le format de l'" + enumType.name() + " " + onlineIdentifier + " est incorrect");
        }
        return resultat;
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
