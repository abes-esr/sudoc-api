package fr.abes.convergence.kbartws.service;

import fr.abes.convergence.kbartws.component.BaseXmlFunctionsCaller;
import fr.abes.convergence.kbartws.exception.IllegalPpnException;
import fr.abes.convergence.kbartws.utils.TYPE_ID;
import oracle.jdbc.OracleDatabaseException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class IsbnService implements IIdentifiantService {
    private final BaseXmlFunctionsCaller caller;

    public IsbnService(BaseXmlFunctionsCaller caller) {
        this.caller = caller;
    }

    @Override
    public boolean checkFormat(String isbn) {
        return isbn != null && isbn.matches("^[0-9]((?:-?[0-9]){9}|(?:-?[0-9]){12})$");
    }

    @Override
    public List<String> getPpnFromIdentifiant(String isbn) throws IllegalPpnException {
        try{
            return caller.isbnToPpn(isbn);
        }catch (UncategorizedSQLException ex){
            throw new IllegalPpnException("Aucune notice ne correspond à la recherche");
        }
    }
}
