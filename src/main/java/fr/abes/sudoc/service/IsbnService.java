package fr.abes.sudoc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.utils.Utilitaire;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class IsbnService implements IIdentifiantService {
    private final BaseXmlFunctionsCaller caller;
    //regexp permettant de vérifier
    //ISBN 10 et 13
    //avec ou sans tiret
    //avec prise en compte du caractère de controle Xx en fin
    private static final Pattern patternIsbn = Pattern.compile("^(?:ISBN(?:-1[03])?:?\\s*)?(?=[-0-9xX\\s]{10,17}$)(?:97[89][- ]?)?([0-9]{1,5})[- ]?([0-9]+)[- ]?([0-9]+)[- ]?([0-9xX])$");

    public IsbnService(BaseXmlFunctionsCaller caller) {
        this.caller = caller;
    }

    @Override
    public boolean checkFormat(String isbn) {

        return isbn != null && patternIsbn.matcher(isbn).matches();
    }

    @Override
    public List<String> getPpnFromIdentifiant(String isbn) throws IOException, IllegalPpnException {
        try{
            String result = caller.isbnToPpn(isbn);
            return Utilitaire.parseJson(result);
        } catch (SQLException ex) {
            if (ex.getMessage().contains("no ppn matched")) {
                throw new IllegalPpnException("Aucune notice ne correspond à la recherche");
            }
            throw new IOException(ex);
        } catch (JsonProcessingException ex) {
            throw new IOException("Impossible de récupérer les ppn correspondant à cet identifiant");
        } catch (UncategorizedSQLException e) {
            throw new IOException("Incident technique lors de l'accès à la base de données");
        }
    }
}
