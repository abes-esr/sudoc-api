package fr.abes.sudoc.controller;

import fr.abes.sudoc.dto.PpnWithTypeWebDto;
import fr.abes.sudoc.dto.ResultWsDto;
import fr.abes.sudoc.dto.SearchDatWebDto;
import fr.abes.sudoc.dto.provider.ElementDto;
import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.exception.ZoneNotFoundException;
import fr.abes.sudoc.service.*;
import fr.abes.sudoc.utils.TYPE_ID;
import fr.abes.sudoc.utils.Utilitaire;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class SudocController {
    private final IdentifiantFactory factory;

    private final DatService service;

    private final NoticeService noticeService;

    private  final ProviderService providerService;

    public SudocController(IdentifiantFactory factory, DatService service, NoticeService noticeService, ProviderService providerService) {
        this.factory = factory;
        this.service = service;
        this.noticeService = noticeService;
        this.providerService = providerService;
    }


    @GetMapping(value = {"/online_identifier_2_ppn/{type}/{onlineIdentifier}", "/online_identifier_2_ppn/{type}/{onlineIdentifier}/{provider}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWsDto onlineIdentifier2Ppn(@PathVariable String type, @PathVariable String onlineIdentifier, @PathVariable(required = false) Optional<String> provider) throws IOException, ZoneNotFoundException, IllegalPpnException {
        log.debug("ONLINE IDENTIFIER 2 PPN");
        ResultWsDto resultat = new ResultWsDto();
        Optional<ElementDto> providerDto = this.providerService.getProviderDisplayName(provider);
        try {
            TYPE_ID enumType = Utilitaire.getEnumFromString(type);
            IIdentifiantService service = factory.getService(enumType);
            if (service.checkFormat(onlineIdentifier)) {
                log.debug("Recherche des ppn pour l'identifiant onlineIdentifier n° {} avec le service {}", onlineIdentifier, enumType);
                List<String> listPpn = service.getPpnFromIdentifiant(onlineIdentifier);
                for (String ppn : listPpn) {
                    log.debug("onlineIdentifier n° {} <-> ppn n° {}", onlineIdentifier, ppn);
                    feedResultatWithNotice(resultat, providerDto, ppn);
                }
            }
            else {
                throw new IllegalArgumentException("Le format de l'" + enumType.name() + " " + onlineIdentifier + " est incorrect");
            }
        } catch (IllegalStateException ex) {
            throw new IllegalArgumentException("Le type " + type + " est incorrect. Les types acceptés sont : monograph, serial");
        } catch (IOException | IllegalPpnException ex) {
            log.error("erreur dans la récupération de la notice correspondant à l'online identifier {}", onlineIdentifier);
            throw new IOException(ex);
        }
        return resultat;
    }


    @GetMapping(value = {"/print_identifier_2_ppn/{type}/{printIdentifier}","/print_identifier_2_ppn/{type}/{printIdentifier}/{provider}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWsDto printIdentifier2Ppn(@PathVariable String type, @PathVariable String printIdentifier, @PathVariable Optional<String> provider) throws IOException, ZoneNotFoundException, IllegalPpnException {
        log.debug("PRINT IDENTIFIER 2 PPN");
        ResultWsDto resultat = new ResultWsDto();
        Optional<ElementDto> providerDto = this.providerService.getProviderDisplayName(provider);
        try {
            TYPE_ID enumType = Utilitaire.getEnumFromString(type);
            IIdentifiantService service = factory.getService(enumType);
            if (service.checkFormat(printIdentifier)) {
                log.debug("Recherche des ppn pour l'identifiant printIdentifier n° {} avec le service {}", printIdentifier, enumType);
                for (String ppn : service.getPpnFromIdentifiant(printIdentifier)) {
                    log.debug("printIdentifier n° {} <-> ppn n° {}", printIdentifier, ppn);
                    NoticeXml notice = noticeService.getNoticeByPpn(ppn);
                    if (!notice.isDeleted()) {
                        if (notice.isNoticeImprimee()) {
                            List<String> ppnElect = new ArrayList<>();
                            try {
                                ppnElect = noticeService.getEquivalentElectronique(notice);
                            } catch (IllegalPpnException ex){
                                resultat.addErreur(ex.getMessage());
                            }
                            if (ppnElect.isEmpty()) {
                                //aucun ppn électronique trouvé dans une notice liée, on renvoie le ppn imprimé
                                resultat.addPpn(new PpnWithTypeWebDto(notice, false));
                            } else {
                                for (String ppnLie : ppnElect) {
                                    NoticeXml noticeLiee = noticeService.getNoticeByPpn(ppnLie);
                                    if(!noticeLiee.isDeleted()) {
                                        try {
                                            resultat.addPpn(new PpnWithTypeWebDto(noticeLiee, this.providerService.checkProviderDansNoticeGeneral(providerDto, noticeLiee)));
                                        } catch (IOException ex) {
                                            resultat.addPpn(new PpnWithTypeWebDto(noticeLiee, false));
                                            resultat.addErreur("Impossible d'analyser le provider en raison d'un problème technique, poursuite du traitement");
                                        }
                                    }
                                }
                            }
                        } else if (notice.isNoticeElectronique()){
                            try {
                                resultat.addPpn(new PpnWithTypeWebDto(notice, this.providerService.checkProviderDansNoticeGeneral(providerDto, notice)));
                            } catch (IOException ex) {
                                resultat.addPpn(new PpnWithTypeWebDto(notice, false));
                                resultat.addErreur("Impossible d'analyser le provider en raison d'un problème technique, poursuite du traitement");
                            }
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
            log.error("erreur dans la récupération de la notice correspondant à au print identifier {}", printIdentifier);
            throw new IOException(ex);
        }
    }


    @GetMapping(value = {"/doi_identifier_2_ppn"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWsDto doiIdentifier2Ppn(@RequestParam(name = "doi") String doi_identifier, @RequestParam(name = "provider") Optional<String> provider) throws IOException, ZoneNotFoundException, IllegalPpnException {
        log.debug("DOI IDENTIFIER 2 PPN");
        log.debug("RequestParam doi : {}, provider : {}", doi_identifier, provider);
        ResultWsDto resultat = new ResultWsDto();
        Optional<ElementDto> providerDto = this.providerService.getProviderDisplayName(provider);
        log.debug("providerDto : {}", providerDto);
        try {
            IIdentifiantService service = factory.getService(TYPE_ID.DOI);
            if (service.checkFormat(doi_identifier)) {
                log.debug("Recherche des ppn pour l'identifiant doi_identifier n° {} avec le service DOI", doi_identifier);
                for(String ppn : service.getPpnFromIdentifiant(doi_identifier) ) {
                    log.debug("doi_identifier n° {} <-> ppn n° {}", doi_identifier, ppn);
                    feedResultatWithNotice(resultat, providerDto, ppn);
                }
            } else {
                log.debug("DOI mauvais format {}", doi_identifier);
                throw new IllegalArgumentException("Le DOI n'est pas au bon format");
            }
        } catch (IOException ex) {
            log.error("Erreur dans la récupération de la notice correspondant au doi {}", doi_identifier);
            throw new IOException(ex);
        } catch (ZoneNotFoundException e) {
            log.debug("ZoneNotFoundException : {}", e.getMessage());
            throw new IOException(e.getMessage());
        } catch (IllegalPpnException e) {
            log.info("Aucune notice ne correspond au doi {}", doi_identifier);
            //res.addErreur("Aucune notice ne correspond à l'identifiant " + doi_identifier);
        }
        return resultat;
    }

    private void feedResultatWithNotice(ResultWsDto resultat, Optional<ElementDto> providerDto, String ppn) throws IllegalPpnException, IOException, ZoneNotFoundException {
        NoticeXml notice = noticeService.getNoticeByPpn(ppn);
        if (notice != null && !notice.isDeleted()){
            if (notice.isNoticeElectronique()) {
                try {
                    resultat.addPpn(new PpnWithTypeWebDto(notice, this.providerService.checkProviderDansNoticeGeneral(providerDto, notice)));
                } catch (IOException ex) {
                    resultat.addPpn(new PpnWithTypeWebDto(notice, false));
                    resultat.addErreur("Impossible d'analyser le provider en raison d'un problème technique, poursuite du traitement");
                }
            } else {
                resultat.addErreur("Le PPN " + notice.getPpn() + " n'est pas une ressource électronique");
            }
        } else {
            log.debug("La notice est supprimée ou inexistante : {}", notice);
        }
    }

    @PostMapping(value = "/dat2ppn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWsDto datToPpn(@Valid @RequestBody SearchDatWebDto request) throws IOException {
        if (request.getTitre() == null) {
            throw new IllegalArgumentException("Le titre ne peut pas être null");
        }
        ResultWsDto resultat = new ResultWsDto();
        Optional<ElementDto> providerDto = Optional.empty();
        if (request.getProviderName() != null && !request.getProviderName().isEmpty()) {
            providerDto = this.providerService.getProviderDisplayName(Optional.of(request.getProviderName()));
        }
        try {
            for (String ppn : service.getPpnFromDat(request.getDate(), request.getAuteur(), request.getTitre())) {
                log.debug("dat2ppn : date : {} / auteur : {} / titre : {} <-> ppn n° {}", request.getDate(), request.getAuteur(), request.getTitre(), ppn);
                feedResultatWithNotice(resultat, providerDto, ppn);
            }
        } catch (IllegalPpnException | ZoneNotFoundException ex) {
            resultat.addErreur(ex.getMessage());
        } catch (IOException ex) {
            log.error("Erreur dans la récupération de la notice correspondant à l'identifiant");
            throw new IOException(ex);
        }
        return resultat;
    }
}
