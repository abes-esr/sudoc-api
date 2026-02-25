package fr.abes.sudoc.service;

import fr.abes.sudoc.dto.provider.ElementDto;
import fr.abes.sudoc.entity.Provider;
import fr.abes.sudoc.entity.Provider035;
import fr.abes.sudoc.entity.notice.NoticeXml;
import fr.abes.sudoc.repository.Provider035Repository;
import fr.abes.sudoc.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderService {
    private final ProviderRepository providerRepository;

    private final Provider035Repository provider035Repository;


    @Cacheable("providerCache")
    public Optional<ElementDto> getProviderDisplayName(Optional<String> shortname) {
        Optional<ElementDto> providerDisplayName = Optional.empty();
        if (shortname.isPresent()) {
            Optional<Provider> provider = this.providerRepository.findByProvider(shortname.get());
            if (provider.isPresent()) {
                ElementDto elementDto = new ElementDto();
                elementDto.setProvider(provider.get().getProvider());
                elementDto.setDisplayName(provider.get().getDisplayName());
                elementDto.setIdProvider(provider.get().getIdtProvider());
                providerDisplayName = Optional.of(elementDto);
            }

        }
        return providerDisplayName;
    }

    public String getProviderFor035(Integer provider) {
        Optional<Provider035> valeur035Opt = provider035Repository.findById(provider);
        return valeur035Opt.map(Provider035::getValeur).orElse(null);
    }

    public boolean checkProviderDansNoticeGeneral(Optional<ElementDto> providerDisplayName, NoticeXml notice) throws IOException {
        return providerDisplayName.map(
                elementDto ->
                        this.checkProviderDansNotice(elementDto.getDisplayName(), notice) ||
                        this.checkProviderDansNotice(elementDto.getProvider(), notice) ||
                        this.checkProviderIn035(elementDto.getIdProvider(), notice)
        ).orElse(true);
    }

    private boolean checkProviderDansNotice(String provider, NoticeXml notice) {
        return notice.checkProviderInZone(provider, "210", "c")
                || notice.checkProviderInZone214(provider);
    }

    private boolean checkProviderIn035(Integer providerIdt, NoticeXml notice) {
        String provider035 = this.getProviderFor035(providerIdt);
        return provider035 != null && notice.checkProviderIn035a(provider035);

    }
}
