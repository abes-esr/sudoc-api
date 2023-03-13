package fr.abes.convergence.kbartws.controller;

import fr.abes.convergence.kbartws.dto.PpnWithTypeWebDto;
import fr.abes.convergence.kbartws.dto.ResultWsDto;
import fr.abes.convergence.kbartws.entity.notice.NoticeXml;
import fr.abes.convergence.kbartws.exception.IllegalPpnException;
import fr.abes.convergence.kbartws.service.IIdentifiantService;
import fr.abes.convergence.kbartws.service.IdentifiantFactory;
import fr.abes.convergence.kbartws.service.NoticeService;
import fr.abes.convergence.kbartws.utils.TYPE_ID;
import fr.abes.convergence.kbartws.utils.TYPE_SUPPORT;
import fr.abes.convergence.kbartws.utils.Utilitaire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
        try {
            TYPE_ID enumType = Utilitaire.getEnumFromString(type);
            ResultWsDto resultat = new ResultWsDto();
            IIdentifiantService service = factory.getService(enumType);
            if (service.checkFormat(onlineIdentifier)) {
                try {
                    for (String ppn : service.getPpnFromIdentifiant(onlineIdentifier)) {
                        NoticeXml notice = noticeService.getNoticeByPpn(ppn);
                        if (!notice.isDeleted()) {
                            if (notice.isNoticeElectronique()) {
                                resultat.addPpn(new PpnWithTypeWebDto(notice.getPpn(), notice.getTypeSupport()));
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
        } catch (IllegalStateException ex) {
            throw new IllegalArgumentException("Le type " + type + " est incorrect. Les types acceptés sont : monograph, serial");
        }
    }

    @GetMapping(value = "/print_identifier_2_ppn/{type}/{printIdentifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWsDto printIdentifier2Ppn(@PathVariable String type, @PathVariable String printIdentifier) throws IOException {
        try {
            TYPE_ID enumType = Utilitaire.getEnumFromString(type);
            ResultWsDto resultat = new ResultWsDto();
            IIdentifiantService service = factory.getService(enumType);
            if (service.checkFormat(printIdentifier)) {
                for (String ppn : service.getPpnFromIdentifiant(printIdentifier)) {
                    NoticeXml notice = noticeService.getNoticeByPpn(ppn);
                    if (!notice.isDeleted()) {
                        if (notice.isNoticeImprimee()) {
                            List<String> ppnElect = noticeService.getEquivalentElectronique(notice);
                            if (ppnElect.size() == 0) {
                                //aucun ppn électronique trouvé dans une notice liée, on renvoie le ppn imprimé
                                resultat.addPpn(new PpnWithTypeWebDto(ppn, TYPE_SUPPORT.IMPRIME));
                            } else {
                                ppnElect.forEach(ppnLie -> resultat.addPpn(new PpnWithTypeWebDto(ppnLie, TYPE_SUPPORT.ELECTRONIQUE)));
                            }
                        } else {
                            resultat.addErreur("Le PPN " + notice.getPpn() + " n'est pas une ressource imprimée");
                        }
                    }
                }
                return resultat;
            } else {
                throw new IllegalArgumentException("Le format de l'" + enumType.name() + " " + printIdentifier + " est incorrect");
            }
        } catch (IllegalStateException ex) {
            throw new IllegalArgumentException("Le type " + type + " est incorrect. Les types acceptés sont : monograph, serial");
        } catch (IOException | IllegalPpnException ex) {
            log.error("erreur dans la récupération de la notice correspondant à l'identifiant " + printIdentifier);
            throw new IOException(ex);
        }
    }
}
