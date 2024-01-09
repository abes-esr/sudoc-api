package fr.abes.sudoc.controller;

import fr.abes.cbs.exception.CBSException;
import fr.abes.sudoc.dto.ResultWebDto;
import fr.abes.sudoc.dto.SearchDatWebDto;
import fr.abes.sudoc.dto.provider.ElementDto;
import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.service.NoticeService;
import fr.abes.sudoc.service.ProviderService;
import fr.abes.sudoc.service.SudocService;
import fr.abes.sudoc.utils.ExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class SudocController {
    @Autowired
    private SudocService service;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private ProviderService providerService;

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

        if(request.isCheckProviderInNotices()){
            Optional<ElementDto> providerDto = this.providerService.getProviderDisplayName(request.getProviderName());

            for(String ppnInString : result.getPpns()){
                NoticeXml noticeInXmlFromPpnInString = this.noticeService.getNoticeByPpn(ppnInString);
                if(!noticeInXmlFromPpnInString.isDeleted()){
                    this.providerService.checkProviderDansNoticeGeneralDat2Ppn(result, providerDto, noticeInXmlFromPpnInString);
                }
            }
        }

        return result;
    }
}
