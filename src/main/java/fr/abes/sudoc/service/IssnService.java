package fr.abes.sudoc.service;

import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.exception.IllegalPpnException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class IssnService implements IIdentifiantService {
    private final BaseXmlFunctionsCaller caller;
    private static final Pattern patternIssn = Pattern.compile("^[0-9]{4}-?[0-9]{3}[0-9xX]$");

    public IssnService(BaseXmlFunctionsCaller caller) {
        this.caller = caller;
    }

    @Override
    public boolean checkFormat(String issn) {
        return issn != null && patternIssn.matcher(issn).matches();
    }

    @Override
    public List<String> getPpnFromIdentifiant(String issn) throws IOException, IllegalPpnException {
        try{
            return caller.issnToPpn(issn.replace("-", ""));
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
