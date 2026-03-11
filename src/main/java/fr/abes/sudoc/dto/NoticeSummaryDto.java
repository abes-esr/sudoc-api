package fr.abes.sudoc.dto;

import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.exception.ZoneNotFoundException;
import fr.abes.sudoc.utils.TYPE_DOCUMENT;
import fr.abes.sudoc.utils.TYPE_SUPPORT;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoticeSummaryDto {
    String ppn;
    TYPE_SUPPORT typeSupport;
    TYPE_DOCUMENT typeDocument;
    boolean providerPresent;
    boolean isFoundByRebound = false;

    public NoticeSummaryDto(NoticeXml noticeXml, Boolean providerPresent) throws ZoneNotFoundException {
        this.ppn = noticeXml.getPpn();
        this.typeSupport = noticeXml.getTypeSupport();
        this.typeDocument = noticeXml.getTypeDocument();
        this.providerPresent = providerPresent;
    }
}
