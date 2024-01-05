package fr.abes.sudoc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.dto.provider.ElementDto;
import fr.abes.sudoc.dto.provider035.ResultProvider035Dto;
import fr.abes.sudoc.entity.Provider;
import fr.abes.sudoc.repository.ProviderRepository;
import fr.abes.sudoc.utils.ExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLRecoverableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final BaseXmlFunctionsCaller caller;

    private final ObjectMapper objectMapper;

    private final ProviderRepository providerRepository;

    @ExecutionTime
    public Optional<ElementDto> getProviderDisplayName(String shortName) throws IOException {
        Optional<Provider> provider = this.providerRepository.findByProvider(shortName);
        Optional<ElementDto> elementDto = Optional.of(new ElementDto());
        if (provider.isPresent()) {
            elementDto.get().setProvider(provider.get().getProvider());
            elementDto.get().setDisplayName(provider.get().getDisplayName());
            elementDto.get().setIdProvider(provider.get().getIdtProvider());
        }
        return elementDto;
    }

    @ExecutionTime
    public List<String> getProviderFor035(Integer provider) throws IOException {
        List<String> listValeurs = new ArrayList<>();
        try {
            ResultProvider035Dto result = objectMapper.readValue(caller.baconProvider035(provider), ResultProvider035Dto.class);
            if (result.getBacon().getQuery().getResult() != null) {
                String valeur035 = result.getBacon().getQuery().getResult().getElements().getValeur035();
                if (valeur035 != null) {
                    listValeurs.addAll(Arrays.stream(valeur035.split("\\|")).toList());
                }
            }
        } catch (JsonProcessingException ex) {
            throw new IOException("Impossible de récupérer les ppns correspondant à cet identifiant");
        } catch (SQLRecoverableException ex) {
            throw new IOException("Incident technique lors de l'accès à la base de données");
        }
        return listValeurs;
    }
}
