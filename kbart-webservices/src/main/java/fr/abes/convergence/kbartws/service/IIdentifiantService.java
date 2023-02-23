package fr.abes.convergence.kbartws.service;

import java.util.List;

public interface IIdentifiantService {
    boolean checkFormat(String identifiant);

    List<String> getPpnFromIdentifiant(String identifiant);
}
