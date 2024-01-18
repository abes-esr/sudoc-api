package fr.abes.sudoc.service;

import fr.abes.sudoc.exception.IllegalPpnException;

import java.io.IOException;
import java.util.List;

public interface IIdentifiantService {
    boolean checkFormat(String identifiant);

    List<String> getPpnFromIdentifiant(String identifiant) throws IllegalPpnException, IOException;
}
