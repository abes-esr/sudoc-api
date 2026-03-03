package fr.abes.sudoc.service;

import fr.abes.sudoc.exception.IllegalPpnException;

import java.io.IOException;
import java.util.List;

public interface IIdentifiantService {
    boolean checkFormat(String identifiant) throws IllegalArgumentException;

    List<String> getPpnFromIdentifiant(String identifiant) throws IllegalPpnException, IOException;
}
