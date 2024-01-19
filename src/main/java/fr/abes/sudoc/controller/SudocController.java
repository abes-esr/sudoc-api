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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<ElementDto> providerDto = this.providerService.getProviderDisplayName(provider);
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
                            resultat.addPpn(new PpnWithTypeWebDto(notice.getPpn(), TYPE_SUPPORT.ELECTRONIQUE, notice.getTypeDocument(), this.providerService.checkProviderDansNoticeGeneral(providerDto, notice)));
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
        Optional<ElementDto> providerDto = this.providerService.getProviderDisplayName(provider);
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
                                    resultat.addPpn(new PpnWithTypeWebDto(noticeLiee.getPpn(), TYPE_SUPPORT.IMPRIME, noticeLiee.getTypeDocument(), this.providerService.checkProviderDansNoticeGeneral(providerDto, noticeLiee)));
                                }
                            }
                        } else if (notice.isNoticeElectronique()){
                            resultat.addPpn(new PpnWithTypeWebDto(notice.getPpn(), TYPE_SUPPORT.AUTRE, notice.getTypeDocument(), this.providerService.checkProviderDansNoticeGeneral(providerDto, notice)));
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
        Optional<ElementDto> providerDto = this.providerService.getProviderDisplayName(provider);
        try {
            IIdentifiantService service = factory.getDoiService();
            if (service.checkFormat(doi_identifier)) {
                log.debug("Recherche des ppn pour l'identifiant doi_identifier n° " + doi_identifier + " avec le service DOI");
                for(String ppn : service.getPpnFromIdentifiant(doi_identifier) ) {
                    log.debug("doi_identifier n° " + doi_identifier + " <-> ppn n° " + ppn);
                    NoticeXml notice = noticeService.getNoticeByPpn(ppn);
                    if (!notice.isDeleted()){
                        if (notice.isNoticeElectronique()) {
                            resultat.addPpn(new PpnWithTypeWebDto(notice.getPpn(), TYPE_SUPPORT.ELECTRONIQUE, notice.getTypeDocument(), this.providerService.checkProviderDansNoticeGeneral(providerDto, notice)));
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

    @PostMapping(value = "/dat2ppn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWebDto datToPpn(@Valid @RequestBody SearchDatWebDto request) {
        if (request.getTitre() == null) {
            throw new IllegalArgumentException("Le titre ne peut pas être null");
        }
        ResultWebDto result = new ResultWebDto();

        List<String> listPpns = new ArrayList<>();

        try {
            listPpns.addAll(service.getPpnFromDat(request.getDate(), request.getAuteur(), request.getTitre()));
            if (request.getProviderName() != null && !request.getProviderName().isEmpty()) {
                Optional<ElementDto> providerDto = this.providerService.getProviderDisplayName(Optional.of(request.getProviderName()));
                List<String> listPpnsFiltres = listPpns.stream().filter(ppn -> {
                    try {
                        NoticeXml noticeInXmlFromPpnInString = this.noticeService.getNoticeByPpn(ppn);
                        if (!noticeInXmlFromPpnInString.isDeleted()) {
                            return providerService.checkProviderDansNoticeGeneral(providerDto, noticeInXmlFromPpnInString);
                        }
                    } catch (IllegalPpnException | IOException | ZoneNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    return false;
                }).toList();
                result.addPpns(listPpnsFiltres);
            } else {
                result.addPpns(listPpns);
            }
        } catch (CBSException ex) {
            result.addErreur(ex.getMessage());
        }

        return result;
    }
}
