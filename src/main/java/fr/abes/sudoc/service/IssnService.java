package fr.abes.sudoc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.utils.Utilitaire;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLRecoverableException;
import java.util.Collections;
import java.util.List;

@Service
public class IssnService implements IIdentifiantService {
    private final BaseXmlFunctionsCaller caller;

    public IssnService(BaseXmlFunctionsCaller caller) {
        this.caller = caller;
    }

    @Override
    public boolean checkFormat(String issn) {
        return issn != null && issn.matches("^[0-9]{4}-?[0-9]{3}[0-9xX]$");
    }

    @Override
    public List<String> getPpnFromIdentifiant(String issn) throws IOException {
        try{
            return caller.issnToPpn(issn.replace("-", ""));
        } catch (UncategorizedSQLException ex) {
            throw new IOException("Incident technique lors de l'accès à la base de données");
        }
    }

}
