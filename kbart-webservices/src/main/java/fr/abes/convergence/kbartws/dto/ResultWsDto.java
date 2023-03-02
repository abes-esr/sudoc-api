package fr.abes.convergence.kbartws.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResultWsDto {
    @JsonProperty(value = "ppns")
    List<String> resultats = new ArrayList<>();
    @JsonProperty(value = "erreurs")
    List<String> erreurs = new ArrayList<>();

    public void addPpn(String ppn) {
        this.resultats.add(ppn);
    }

    public void addErreur(String erreur) {
        this.erreurs.add(erreur);
    }
}
