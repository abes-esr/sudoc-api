package fr.abes.convergence.sudocws.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchDatWebDto {
    @JsonProperty("date")
    private Integer date;
    @JsonProperty("auteur")
    private String auteur;
    @JsonProperty("titre")
    private String titre;
}
