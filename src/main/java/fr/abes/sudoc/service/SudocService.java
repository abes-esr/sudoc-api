package fr.abes.sudoc.service;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.process.ProcessCBS;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SudocService {
    @Value("${sudoc.serveur}")
    private String serveurCbs;
    @Value("${sudoc.port}")
    private String port;
    @Value("${sudoc.login}")
    private String loginCbs;
    @Value("${sudoc.passwd}")
    private String passCbs;

    @Getter
    private String query;

    public List<String> getPpnFromDat(Integer annee, String auteur, String titre) throws CBSException {
        ProcessCBS cbs = new ProcessCBS();
        try {
            log.debug("serveur : " + this.serveurCbs + " / port : " + this.port);
            cbs.authenticate(this.serveurCbs, this.port, this.loginCbs, this.passCbs);
            if (annee != null && auteur != null) {
                this.query = "tno t ; apu " + annee + " ; che aut " + auteur + " et mti " + titre;
            } else if (annee != null) {
                this.query = "tno t ; apu " + annee + " ; che mti " + titre;
            } else {
                if (auteur != null) {
                    this.query = "tno t ; che aut " + auteur + " et mti " + titre;
                } else {
                    this.query = "tno t ; che mti " + titre;
                }
            }

            log.debug("requête : " + this.query);
            cbs.search(this.query);
            return switch (cbs.getNbNotices()) {
                case 0 -> new ArrayList<>();
                case 1 -> Collections.singletonList(cbs.getPpnEncours());
                default -> Arrays.asList(cbs.getListePpn().toString().split(";"));
            };
        } finally {
            cbs.disconnect();
        }
    }
}
