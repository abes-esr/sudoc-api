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
        //regexp permettant de vérifier
        //ISBN 10 et 13
        //avec ou sans tiret
        //avec prise en compte du caractère de controle X en fin
        return isbn != null && isbn.matches("^(?:ISBN(?:-1[03])?:?\\\\s*)?(?=[-0-9X\\\\s]{10,17}$)(?:97[89][- ]?)?([0-9]{1,5})[- ]?([0-9]+)[- ]?([0-9]+)[- ]?([0-9X])$");
    }

    @Override
    public List<String> getPpnFromIdentifiant(String isbn) throws IOException {
        try{
            return caller.isbnToPpn(isbn.replace("-", ""));
        }  catch (UncategorizedSQLException ex) {
            throw new IOException("Incident technique lors de l'accès à la base de données");
        }
    }
}
