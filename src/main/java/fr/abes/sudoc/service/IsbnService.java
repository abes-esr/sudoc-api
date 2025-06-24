package fr.abes.sudoc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.exception.IllegalPpnException;
import fr.abes.sudoc.utils.Utilitaire;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class IsbnService implements IIdentifiantService {
    private final BaseXmlFunctionsCaller caller;

    public IsbnService(BaseXmlFunctionsCaller caller) {
        this.caller = caller;
    }

    @Override
    public boolean checkFormat(String isbn) {
        //regexp permettant de vérifier
        //ISBN 10 et 13
        //avec ou sans tiret
        //avec prise en compte du caractère de controle Xx en fin
        return isbn != null && isbn.matches("^(?:ISBN(?:-1[03])?:?\\s*)?(?=[-0-9xX\\s]{10,17}$)(?:97[89][- ]?)?([0-9]{1,5})[- ]?([0-9]+)[- ]?([0-9]+)[- ]?([0-9xX])$");
    }

    @Override
    public List<String> getPpnFromIdentifiant(String isbn) throws IOException, IllegalPpnException {
        try{
            String result = caller.isbnToPpn(isbn);
            return Utilitaire.parseJson(result);
        } catch (UncategorizedSQLException ex) {
            if (ex.getMessage().contains("no ppn matched")) {
                throw new IllegalPpnException("Aucune notice ne correspond à la recherche");
            }
            throw new IOException(ex);
        } catch (JsonProcessingException ex) {
            throw new IOException("Impossible de récupérer les ppn correspondant à cet identifiant");
        }
    }
}
