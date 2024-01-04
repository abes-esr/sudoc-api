package fr.abes.convergence.kbartws.service;

import fr.abes.convergence.kbartws.exception.IllegalPpnException;

import java.io.IOException;
import java.util.List;

public interface IIdentifiantService {
    boolean checkFormat(String identifiant);

    List<String> getPpnFromIdentifiant(String identifiant) throws IllegalPpnException, IOException;
}
