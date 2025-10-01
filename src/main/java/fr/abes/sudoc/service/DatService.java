package fr.abes.sudoc.service;

import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.utils.Utilitaire;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DatService {
    private final BaseXmlFunctionsCaller caller;

    public DatService(BaseXmlFunctionsCaller caller) {
        this.caller = caller;
    }

    public List<String> getPpnFromDat(Integer date, String auteur, String titre) throws IllegalPpnException, IOException {
        try {
            List<String> result = caller.datToPpn(date, Utilitaire.formatString(auteur), Utilitaire.formatString(titre));
            if (result.isEmpty())
                throw new IllegalPpnException("Aucune notice ne correspond à la recherche sur " + date + " , " + auteur + " , " + titre);
            return result;
        } catch (UncategorizedSQLException ex) {
            throw new IOException("Incident technique lors de l'accès à la base de données");
        }
    }
}
