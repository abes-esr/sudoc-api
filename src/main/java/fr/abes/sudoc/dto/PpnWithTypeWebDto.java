package fr.abes.sudoc.dto;

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
}
