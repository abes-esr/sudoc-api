package fr.abes.sudoc.dto.provider035;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QueryDto {
    @JsonProperty("idt_provider")
    private String id;
    @JsonProperty("results")
    private ResultDto result;
}
