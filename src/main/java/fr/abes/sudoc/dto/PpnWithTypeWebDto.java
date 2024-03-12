package fr.abes.sudoc.dto;

import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.exception.ZoneNotFoundException;
import fr.abes.sudoc.utils.TYPE_DOCUMENT;
import fr.abes.sudoc.utils.TYPE_SUPPORT;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PpnWithTypeWebDto {
    String ppn;
    TYPE_SUPPORT typeSupport;
    TYPE_DOCUMENT typeDocument;
    Boolean providerPresent;

    public PpnWithTypeWebDto(NoticeXml noticeXml, Boolean providerPresent) throws ZoneNotFoundException {
        this.ppn = noticeXml.getPpn();
        this.typeSupport = noticeXml.getTypeSupport();
        this.typeDocument = noticeXml.getTypeDocument();
        this.providerPresent = providerPresent;
    }
}
