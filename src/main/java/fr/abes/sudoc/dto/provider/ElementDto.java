package fr.abes.sudoc.dto.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ElementDto {
    @JsonProperty("provider")
    private String provider;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("idprovider")
    private Integer idProvider;
}
