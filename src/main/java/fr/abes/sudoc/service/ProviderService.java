package fr.abes.sudoc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import fr.abes.sudoc.dto.provider.ElementDto;
import fr.abes.sudoc.dto.provider035.ResultProvider035Dto;
import fr.abes.sudoc.entity.Provider;
import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.repository.ProviderRepository;
import fr.abes.sudoc.utils.ExecutionTime;
import fr.abes.sudoc.utils.Utilitaire;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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


    @Cacheable("providerCache")
    public Optional<ElementDto> getProviderDisplayName(Optional<String> shortname) {
        Optional<ElementDto> providerDisplayName = Optional.empty();
        if (shortname.isPresent()) {
            Optional<Provider> provider = this.providerRepository.findByProvider(shortname.get());
            Optional<ElementDto> elementDto = Optional.of(new ElementDto());
            if (provider.isPresent()) {
                elementDto.get().setProvider(provider.get().getProvider());
                elementDto.get().setDisplayName(provider.get().getDisplayName());
                elementDto.get().setIdProvider(provider.get().getIdtProvider());
            }
            providerDisplayName = elementDto;
        }
        return providerDisplayName;
    }


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

    public boolean checkProviderDansNoticeGeneral(Optional<ElementDto> providerDisplayName, NoticeXml notice) throws IOException {
        if (providerDisplayName.isPresent()) {
            if (this.checkProviderDansNotice(providerDisplayName.get().getDisplayName(), notice) || this.checkProviderDansNotice(providerDisplayName.get().getProvider(), notice) || this.checkProviderIn035(providerDisplayName.get().getIdProvider(), notice)) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    private boolean checkProviderDansNotice(String provider, NoticeXml notice) {
        String providerWithoutDiacritics = Utilitaire.replaceDiacritics(provider);
        return notice.checkProviderInZone(providerWithoutDiacritics, "210", "c") || notice.checkProviderInZone(providerWithoutDiacritics, "214", "c");
    }

    private boolean checkProviderIn035(Integer providerIdt, NoticeXml notice) throws IOException {
        List<String> providers035 = this.getProviderFor035(providerIdt);
        for (String provider035 : providers035) {
            if (notice.checkProviderIn035a(provider035)) {
                return true;
            }
        }
        return false;
    }
}
