package fr.abes.sudoc.service;

import fr.abes.sudoc.dto.PpnWithTypeWebDto;
import fr.abes.sudoc.dto.provider.ElementDto;
import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.exception.ZoneNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class AbstractService {
    private final NoticeService noticeService;
    private  final ProviderService providerService;

    protected AbstractService(NoticeService noticeService, ProviderService providerService) {
        this.noticeService = noticeService;
        this.providerService = providerService;
    }

    protected PpnWithTypeWebDto feedResultatWithNotice(ElementDto providerDto, String ppn) throws IllegalPpnException, IOException, ZoneNotFoundException {
        NoticeXml notice = noticeService.getNoticeByPpn(ppn);
        if (!notice.isDeleted()){
            if (notice.isNoticeElectronique()) {
                return new PpnWithTypeWebDto(notice.getPpn(), notice.getTypeSupport(), notice.getTypeDocument(), this.providerService.checkProviderDansNoticeGeneral(providerDto, notice));
            } else {
                throw new IllegalPpnException("Le PPN " + notice.getPpn() + " n'est pas une ressource électronique");
            }
        } else {
            log.debug("La notice est supprimée : {}", notice);
        }
        return null;
    }
}
