package fr.abes.sudoc.dto;

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
    List<PpnWithTypeWebDto> resultats = new ArrayList<>();
    @JsonProperty(value = "erreurs")
    List<String> erreurs = new ArrayList<>();

    public void addPpn(PpnWithTypeWebDto ppn) {
        this.resultats.add(ppn);
    }

    public void addPpns(List<PpnWithTypeWebDto> ppns) {
        this.resultats.addAll(ppns);
    }

    public void addErreur(String erreur) {
        this.erreurs.add(erreur);
    }

    public void addErreurs(List<String> erreurs) { this.erreurs.addAll(erreurs); }
}
