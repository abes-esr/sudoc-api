package fr.abes.sudoc.service;

import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.exception.IllegalPpnException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DoiService implements IIdentifiantService{
    private final BaseXmlFunctionsCaller caller;

    public DoiService(BaseXmlFunctionsCaller caller) {
        this.caller = caller;
    }

    @Override
    public boolean checkFormat(String doi) {
        String doiPattern = "10.\\d{0,15}.\\d{0,15}.+";
        return doi != null && doi.matches(doiPattern);
    }

    @Override
    public List<String> getPpnFromIdentifiant(String doi) throws IOException, IllegalPpnException {
        try {
            List<String> result = caller.doiToPpn(doi);
            if (result.isEmpty())
                throw new IllegalPpnException("Aucune notice ne correspond à la recherche sur le doi " + doi);
            else
                if (result.size() != 1) {
                    throw new IllegalPpnException("Plusieurs résultats à la recherche sur doi " + doi);
                } else {
                    List<String> retour = new ArrayList<>();
                    retour.add(result.get(0));
                    return retour;
                }
        } catch (UncategorizedSQLException ex) {
            throw new IOException("Incident technique lors de l'accès à la base de données");
        }
    }
}
