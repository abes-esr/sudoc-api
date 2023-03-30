package fr.abes.convergence.kbartws.controller;

import fr.abes.convergence.kbartws.dto.PpnWithTypeWebDto;
import fr.abes.convergence.kbartws.dto.ResultWsDto;
import fr.abes.convergence.kbartws.entity.notice.NoticeXml;
import fr.abes.convergence.kbartws.exception.IllegalPpnException;
import fr.abes.convergence.kbartws.service.IIdentifiantService;
import fr.abes.convergence.kbartws.service.IdentifiantFactory;
import fr.abes.convergence.kbartws.service.NoticeService;
import fr.abes.convergence.kbartws.service.ProviderService;
import fr.abes.convergence.kbartws.utils.TYPE_ID;
import fr.abes.convergence.kbartws.utils.TYPE_SUPPORT;
import fr.abes.convergence.kbartws.utils.Utilitaire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
@RequestMapping("/v1")
public class KbartController {
    @Autowired
    private IdentifiantFactory factory;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private NoticeService noticeService;

    @GetMapping(value = {"/online_identifier_2_ppn/{type}/{onlineIdentifier}", "/online_identifier_2_ppn/{type}/{onlineIdentifier}/{provider}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWsDto onlineIdentifier2Ppn(@PathVariable String type, @PathVariable String onlineIdentifier, @PathVariable(required = false) Optional<String> provider) throws IOException {
        ResultWsDto resultat = new ResultWsDto();
        Optional<String> providerDisplayName = getProviderDisplayName(provider, resultat);
        try {
            TYPE_ID enumType = Utilitaire.getEnumFromString(type);
            IIdentifiantService service = factory.getService(enumType);
            if (service.checkFormat(onlineIdentifier)) {
                    for (String ppn : service.getPpnFromIdentifiant(onlineIdentifier)) {
                        NoticeXml notice = noticeService.getNoticeByPpn(ppn);
                        if (!notice.isDeleted()) {
                            if (notice.isNoticeElectronique()) {
                                checkProviderDansNoticeGeneral(provider, resultat, providerDisplayName, notice);
                            } else {
                                resultat.addErreur("Le PPN " + notice.getPpn() + " n'est pas une ressource électronique");
                            }
                        }
                    }
                }
            else {
                throw new IllegalArgumentException("Le format de l'" + enumType.name() + " " + onlineIdentifier + " est incorrect");
            }
        } catch (IllegalStateException ex) {
            throw new IllegalArgumentException("Le type " + type + " est incorrect. Les types acceptés sont : monograph, serial");
        } catch (IOException ex) {
            log.error("erreur dans la récupération de la notice correspondant à l'identifiant " + onlineIdentifier);
            throw new IOException(ex);
        } catch (IllegalPpnException ex) {
            log.debug("Impossible de retrouver une notice correspondant à cet identifiant");
        }
        return resultat;
    }


    @GetMapping(value = {"/print_identifier_2_ppn/{type}/{printIdentifier}","/print_identifier_2_ppn/{type}/{printIdentifier}/{provider}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWsDto printIdentifier2Ppn(@PathVariable String type, @PathVariable String printIdentifier, @PathVariable Optional<String> provider) throws IOException {
        ResultWsDto resultat = new ResultWsDto();
        Optional<String> providerDisplayName = getProviderDisplayName(provider, resultat);
        try {
            TYPE_ID enumType = Utilitaire.getEnumFromString(type);
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
                                Optional<String> finalProviderDisplayName = providerDisplayName;
                                for (String ppnLie : ppnElect) {
                                    NoticeXml noticeLiee = noticeService.getNoticeByPpn(ppnLie);
                                    checkProviderDansNoticeGeneral(provider, resultat, finalProviderDisplayName, noticeLiee);
                                }
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

    private Optional<String> getProviderDisplayName(Optional<String> provider, ResultWsDto resultat) {
        Optional<String> providerDisplayName = Optional.empty();
        try {
            providerDisplayName = (provider.isPresent()) ? providerService.getProviderDisplayName(provider.get()) : Optional.empty();
        } catch (IOException | RestClientResponseException ex) {
            log.error(ex.getMessage());
            resultat.addErreur("Impossible d'analyser le provider en raison d'un problème technique, poursuite du traitement");
        }
        return providerDisplayName;
    }
    private void checkProviderDansNoticeGeneral(Optional<String> provider, ResultWsDto resultat, Optional<String> providerDisplayName, NoticeXml notice) {
        if (providerDisplayName.isPresent() && provider.isPresent()) {
            if (checkProviderDansNotice(providerDisplayName.get(), notice) || checkProviderDansNotice(provider.get(), notice))
                resultat.addPpn(new PpnWithTypeWebDto(notice.getPpn(), notice.getTypeSupport()));
            else
                resultat.addErreur("PPN : " + notice.getPpn() + " ne contient pas le provider " + provider.get() + " en 035$a, 210$c ou 214$c");
        }
        else {
            resultat.addPpn(new PpnWithTypeWebDto(notice.getPpn(), notice.getTypeSupport()));
        }
    }

    private boolean checkProviderDansNotice(String provider, NoticeXml notice) {
        String providerWithoutDiacritics = Utilitaire.replaceDiacritics(provider);
        //TODO : le test sur la 035 sera finalement fait dans une US ultérieure, manque une information pour la comparaison
        //if (!notice.checkProviderIn035a(providerWithoutDiacritics)) {
        return notice.checkProviderInZone(providerWithoutDiacritics, "210", "c") || notice.checkProviderInZone(providerWithoutDiacritics, "214", "c");

        //} else {
        //    resultat.addPpn(new PpnWithTypeWebDto(notice.getPpn(), notice.getTypeSupport()));
        //}
    }
}
