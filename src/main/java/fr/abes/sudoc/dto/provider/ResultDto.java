package fr.abes.sudoc.dto.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResultDto {
    @JsonProperty("element")
    private ElementDto elements;
}
