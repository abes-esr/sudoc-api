package fr.abes.convergence.kbartws.dto;

import fr.abes.convergence.kbartws.utils.TYPE_SUPPORT;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PpnWithTypeWebDto {
    String ppn;
    TYPE_SUPPORT type;
}
