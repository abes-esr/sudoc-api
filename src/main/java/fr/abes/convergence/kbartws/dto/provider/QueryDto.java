package fr.abes.convergence.kbartws.dto.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QueryDto {
    @JsonProperty("id")
    private String id;
    @JsonProperty("results")
    private ResultDto[] results;
}
