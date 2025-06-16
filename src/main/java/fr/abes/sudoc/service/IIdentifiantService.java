package fr.abes.sudoc.service;

import fr.abes.sudoc.dto.PpnWithTypeWebDto;
import fr.abes.sudoc.dto.ResultWsDto;
import fr.abes.sudoc.dto.provider.ElementDto;
import fr.abes.sudoc.exception.IllegalPpnException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IIdentifiantService {
    boolean checkFormat(String identifiant);

    List<PpnWithTypeWebDto> getPpnFromIdentifiant(String identifiant, Optional<ElementDto> provider) throws IllegalPpnException, IOException;
}
