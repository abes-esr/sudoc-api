package fr.abes.sudoc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.utils.Utilitaire;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLRecoverableException;
import java.util.List;

@Service
public class IsbnService implements IIdentifiantService {
    private final BaseXmlFunctionsCaller caller;

    public IsbnService(BaseXmlFunctionsCaller caller) {
        this.caller = caller;
    }

    @Override
    public boolean checkFormat(String isbn) {
        return isbn != null && isbn.matches("^[0-9]((?:-?[0-9]){9}|(?:-?[0-9]){8}X|(?:-?[0-9]){12})$");
    }

    @Override
    public List<String> getPpnFromIdentifiant(String isbn) throws IllegalPpnException, IOException {
        try{
            return Utilitaire.parseJson(caller.isbnToPpn(isbn));
        } catch (UncategorizedSQLException ex){
            throw new IllegalPpnException("Aucune notice ne correspond à la recherche");
        } catch (JsonProcessingException ex) {
            throw new IOException("Impossible de récupérer les ppns correspondant à cet identifiant");
        } catch (SQLRecoverableException ex) {
            throw new IOException("Incident technique lors de l'accès à la base de données");
        }
    }
}
