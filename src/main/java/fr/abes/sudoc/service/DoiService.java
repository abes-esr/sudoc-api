package fr.abes.sudoc.service;

import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.exception.IllegalPpnException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class DoiService implements IIdentifiantService{
    private final BaseXmlFunctionsCaller caller;
    private static final Pattern patternDoi = Pattern.compile("10.\\d{0,15}.\\d{0,15}.+");

    public static final String MESSAGE_ERROR_DOI_FORMAT = "Le DOI n'est pas au bon format";

    public DoiService(BaseXmlFunctionsCaller caller) {
        this.caller = caller;
    }

    @Override
    public void checkFormat(String doi) throws IllegalArgumentException {
        if (doi == null || !patternDoi.matcher(doi).find()){
            log.debug("DOI mauvais format {}", doi);
            throw new IllegalArgumentException(MESSAGE_ERROR_DOI_FORMAT);
        }
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
        } catch (SQLException ex) {
            if (ex.getMessage().contains("no ppn matched")) {
                throw new IllegalPpnException("Aucune notice ne correspond à la recherche");
            }
            throw new IOException(ex);
        }
    }
}
