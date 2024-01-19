package fr.abes.sudoc.controller;

import fr.abes.cbs.exception.CBSException;
import fr.abes.sudoc.dto.PpnWithTypeWebDto;
import fr.abes.sudoc.dto.ResultWebDto;
import fr.abes.sudoc.dto.ResultWsDto;
import fr.abes.sudoc.dto.SearchDatWebDto;
import fr.abes.sudoc.dto.provider.ElementDto;
import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.exception.ZoneNotFoundException;
import fr.abes.sudoc.service.*;
import fr.abes.sudoc.utils.ExecutionTime;
import fr.abes.sudoc.utils.TYPE_ID;
import fr.abes.sudoc.utils.TYPE_SUPPORT;
import fr.abes.sudoc.utils.Utilitaire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class SudocController {

    @Autowired
    private IdentifiantFactory factory;
    @Autowired
    private SudocService service;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private ProviderService providerService;

    @ExecutionTime
    @GetMapping(value = {"/online_identifier_2_ppn/{type}/{onlineIdentifier}", "/online_identifier_2_ppn/{type}/{onlineIdentifier}/{provider}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWsDto onlineIdentifier2Ppn(@PathVariable String type, @PathVariable String onlineIdentifier, @PathVariable(required = false) Optional<String> provider) throws IOException, ZoneNotFoundException {
        log.debug("-----------------------------------------------------------");
        log.debug("-----------------------------------------------------------");
        log.debug("ONLINE IDENTIFIER 2 PPN");
        ResultWsDto resultat = new ResultWsDto();
        Optional<ElementDto> providerDto = this.providerService.getProviderDisplayName(provider, resultat);
        try {
            TYPE_ID enumType = Utilitaire.getEnumFromString(type);
            IIdentifiantService service = factory.getService(enumType);
            if (service.checkFormat(onlineIdentifier)) {
                log.debug("Recherche des ppn pour l'identifiant onlineIdentifier n° " + onlineIdentifier + " avec le service " + enumType);
                for (String ppn : service.getPpnFromIdentifiant(onlineIdentifier)) {
                    log.debug("onlineIdentifier n° " + onlineIdentifier + " <-> ppn n° " + ppn);
                    NoticeXml notice = noticeService.getNoticeByPpn(ppn);
                    if (!notice.isDeleted()) {
                        if (notice.isNoticeElectronique()) {
                            this.providerService.checkProviderDansNoticeGeneral(resultat, providerDto, notice);
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
            throw new IOException(ex);
        }
        return resultat;
    }

    @ExecutionTime
    @GetMapping(value = {"/print_identifier_2_ppn/{type}/{printIdentifier}","/print_identifier_2_ppn/{type}/{printIdentifier}/{provider}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWsDto printIdentifier2Ppn(@PathVariable String type, @PathVariable String printIdentifier, @PathVariable Optional<String> provider) throws IOException, ZoneNotFoundException {
        log.debug("-----------------------------------------------------------");
        log.debug("-----------------------------------------------------------");
        log.debug("PRINT IDENTIFIER 2 PPN");
        ResultWsDto resultat = new ResultWsDto();
        Optional<ElementDto> providerDto = getProviderDisplayName(provider, resultat);
        try {
            TYPE_ID enumType = Utilitaire.getEnumFromString(type);
            IIdentifiantService service = factory.getService(enumType);
            if (service.checkFormat(printIdentifier)) {
                log.debug("Recherche des ppn pour l'identifiant printIdentifier n° " + printIdentifier + " avec le service " + enumType);
                for (String ppn : service.getPpnFromIdentifiant(printIdentifier)) {
                    log.debug("printIdentifier n° " + printIdentifier + " <-> ppn n° " + ppn);
                    NoticeXml notice = noticeService.getNoticeByPpn(ppn);
                    if (!notice.isDeleted()) {
                        if (notice.isNoticeImprimee()) {
                            List<String> ppnElect = noticeService.getEquivalentElectronique(notice);
                            if (ppnElect.isEmpty()) {
                                //aucun ppn électronique trouvé dans une notice liée, on renvoie le ppn imprimé
                                resultat.addPpn(new PpnWithTypeWebDto(ppn, TYPE_SUPPORT.IMPRIME, notice.getTypeDocument(), false));
                            } else {
                                for (String ppnLie : ppnElect) {
                                    NoticeXml noticeLiee = noticeService.getNoticeByPpn(ppnLie);
                                    this.checkProviderDansNoticeGeneral(resultat, providerDto, noticeLiee);
                                }
                            }
                        } else if (notice.isNoticeElectronique()){
                            boolean providerPresent = false;
                            if (providerDto.isPresent()) {
                                providerPresent = (checkProviderDansNotice(providerDto.get().getDisplayName(), notice) || checkProviderDansNotice(providerDto.get().getProvider(), notice) || checkProviderIn035(providerDto.get().getIdProvider(), notice));
                            }
                            resultat.addPpn(new PpnWithTypeWebDto(ppn, TYPE_SUPPORT.AUTRE, notice.getTypeDocument(), providerPresent));
                        }
                    }
                }
                if(resultat.getResultats().isEmpty() && resultat.getErreurs().isEmpty()){
                    resultat.addErreur("Aucun PPN ne correspond au " + printIdentifier);
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

    @ExecutionTime
    @GetMapping(value = {"/doi_identifier_2_ppn"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWsDto doiIdentifier2Ppn(@RequestParam(name = "doi") String doi_identifier, @RequestParam(name = "provider") Optional<String> provider) throws IOException, ZoneNotFoundException {
        log.debug("-----------------------------------------------------------");
        log.debug("-----------------------------------------------------------");
        log.debug("DOI IDENTIFIER 2 PPN");
        ResultWsDto resultat = new ResultWsDto();
        Optional<ElementDto> providerDto = getProviderDisplayName(provider, resultat);
        try {
            IIdentifiantService service = factory.getDoiService();
            if (service.checkFormat(doi_identifier)) {
                log.debug("Recherche des ppn pour l'identifiant doi_identifier n° " + doi_identifier + " avec le service DOI");
                for(String ppn : service.getPpnFromIdentifiant(doi_identifier) ) {
                    log.debug("doi_identifier n° " + doi_identifier + " <-> ppn n° " + ppn);
                    NoticeXml notice = noticeService.getNoticeByPpn(ppn);
                    if (!notice.isDeleted()){
                        if (notice.isNoticeElectronique()) {
                            this.checkProviderDansNoticeGeneral(resultat, providerDto, notice);
                        } else {
                            resultat.addErreur("Le PPN " + notice.getPpn() + " n'est pas une ressource électronique");
                        }
                    }
                }
            }
        } catch (IllegalStateException ex) {
            throw new IllegalArgumentException("Le DOI n'est pas au bon format");
        } catch (IOException ex) {
            log.error("Erreur dans la récupération de la notice correspondant à l'identifiant");
            throw new IOException(ex);
        } catch (IllegalPpnException ex) {
            log.debug("Impossible de retrouver une notice correspondant à cet identifiant");
        }
        return resultat;
    }

//    private Optional<ElementDto> getProviderDisplayName(Optional<String> provider, ResultWsDto resultat) {
//        Optional<ElementDto> providerDisplayName = Optional.empty();
//        try {
//            providerDisplayName = (provider.isPresent()) ? providerService.getProviderDisplayName(provider.get()) : Optional.empty();
//        } catch (IOException | RestClientResponseException ex) {
//            log.error(ex.getMessage());
//            resultat.addErreur("Impossible d'analyser le provider en raison d'un problème technique, poursuite du traitement");
//        }
//        return providerDisplayName;
//    }
//
//    private void checkProviderDansNoticeGeneral(ResultWsDto resultat, Optional<ElementDto> providerDisplayName, NoticeXml notice) throws IOException, ZoneNotFoundException {
//        if (providerDisplayName.isPresent()) {
//            if (this.checkProviderDansNotice(providerDisplayName.get().getDisplayName(), notice) || this.checkProviderDansNotice(providerDisplayName.get().getProvider(), notice) || this.checkProviderIn035(providerDisplayName.get().getIdProvider(), notice)) {
//                resultat.addPpn(new PpnWithTypeWebDto(notice.getPpn(), notice.getTypeSupport(), notice.getTypeDocument(), true));
//            }
//            else {
//                resultat.addPpn(new PpnWithTypeWebDto(notice.getPpn(), notice.getTypeSupport(), notice.getTypeDocument(), false));
//            }
//        }
//        else {
//            resultat.addPpn(new PpnWithTypeWebDto(notice.getPpn(), notice.getTypeSupport(), notice.getTypeDocument(), false));
//        }
//    }
//
//    private boolean checkProviderDansNotice(String provider, NoticeXml notice) {
//        String providerWithoutDiacritics = Utilitaire.replaceDiacritics(provider);
//        return notice.checkProviderInZone(providerWithoutDiacritics, "210", "c") || notice.checkProviderInZone(providerWithoutDiacritics, "214", "c");
//    }
//
//    private boolean checkProviderIn035(Integer providerIdt, NoticeXml notice) throws IOException {
//        List<String> providers035 = providerService.getProviderFor035(providerIdt);
//        for (String provider035 : providers035) {
//            if (notice.checkProviderIn035a(provider035)) {
//                return true;
//            }
//        }
//        return false;
//    }

    @ExecutionTime
    @PostMapping(value = "/dat2ppn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWebDto datToPpn(@Valid @RequestBody SearchDatWebDto request) throws IOException, IllegalPpnException {
        if (request.getTitre() == null) {
            throw new IllegalArgumentException("Le titre ne peut pas être null");
        }
        ResultWebDto result = new ResultWebDto();

        try {
            result.addPpns(service.getPpnFromDat(request.getDate(), request.getAuteur(), request.getTitre()));
        } catch (CBSException ex) {
            result.addErreur(ex.getMessage());
        }

        Optional<ElementDto> providerDto = this.providerService.getProviderDisplayName(request.getProviderName());

        for (String ppnInString : result.getPpns()) {
            NoticeXml noticeInXmlFromPpnInString = this.noticeService.getNoticeByPpn(ppnInString);
            if (!noticeInXmlFromPpnInString.isDeleted()) {
                this.providerService.checkProviderDansNoticeGeneralDat2Ppn(result, providerDto, noticeInXmlFromPpnInString);
            }
        }

        return result;
    }
}
