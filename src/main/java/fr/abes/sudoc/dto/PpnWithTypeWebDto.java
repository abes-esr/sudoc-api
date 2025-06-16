package fr.abes.sudoc.dto;

import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.exception.ZoneNotFoundException;
import fr.abes.sudoc.utils.TYPE_DOCUMENT;
import fr.abes.sudoc.utils.TYPE_SUPPORT;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PpnWithTypeWebDto {
    String ppn;
    TYPE_SUPPORT typeSupport;
    TYPE_DOCUMENT typeDocument;
    Boolean providerPresent;

    public PpnWithTypeWebDto(String ppn, TYPE_SUPPORT typeSupport, TYPE_DOCUMENT typeDocument, Boolean providerPresent) {
        this.ppn = ppn;
        this.typeSupport = typeSupport;
        this.typeDocument = typeDocument;
        this.providerPresent = providerPresent;
    }
}
