package fr.abes.convergence.kbartws.dto.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ElementDto {
    @JsonProperty("provider")
    private String provider;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("idprovider")
    private String idProvider;
}
