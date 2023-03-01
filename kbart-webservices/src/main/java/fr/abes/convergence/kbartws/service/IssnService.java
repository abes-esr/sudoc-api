package fr.abes.convergence.kbartws.service;

import fr.abes.convergence.kbartws.component.BaseXmlFunctionsCaller;
import fr.abes.convergence.kbartws.exception.IllegalPpnException;
import oracle.jdbc.OracleDatabaseException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
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
    public List<String> getPpnFromIdentifiant(String issn) throws IllegalPpnException {
        try{
            return caller.issnToPpn(issn);
        }catch (UncategorizedSQLException ex){
            throw new IllegalPpnException("Aucune notice ne correspond à la recherche");
        }
    }

}
