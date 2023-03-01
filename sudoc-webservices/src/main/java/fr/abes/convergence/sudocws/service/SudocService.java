package fr.abes.convergence.sudocws.service;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.process.ProcessCBS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    private ProcessCBS cbs;

    public List<String> getPpnFromDat(Integer annee, String auteur, String titre) throws CBSException {
        this.cbs.authenticate(this.serveurCbs, this.port, this.loginCbs, this.passCbs);

        if (annee != null && auteur != null) {
            this.cbs.search("tno t ; tdo b ; apu " + annee + " ; che aut " + auteur + " et mti " + titre);
            log.info("tno t ; tdo b ; apu " + annee + " ; che aut " + auteur + " et mti " + titre);
        }
        else
            if (annee != null)
                this.cbs.search("tno t ; tdo b ; apu " + annee + " ; che mti " + titre);
            else
                this.cbs.search("tno t ; tdo b ; che aut " + auteur + " et mti " + titre);
        if (this.cbs.getNbNotices() != 0) {
            return Arrays.asList(this.cbs.getListePpn().toString().split(";"));
        }
        return new ArrayList<>();
    }
}
