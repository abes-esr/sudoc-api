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
import reactor.core.publisher.Mono;

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

    public Mono<String> getProviderFor035(Integer provider) {
        Mono<Provider035> valeur035Opt = provider035Repository.findById(provider);
        return valeur035Opt.cache().map(Provider035::getValeur);
    }

    public boolean checkProviderDansNoticeGeneral(ElementDto provider, NoticeXml notice) {
        return this.checkProviderDansNotice(provider.getDisplayName(), notice)
                || this.checkProviderDansNotice(provider.getProvider(), notice)
                || this.checkProviderIn035(provider.getIdProvider(), notice);
    }

    private boolean checkProviderDansNotice(String provider, NoticeXml notice) {
        return notice.checkProviderInZone(provider, "210", "c")
                || notice.checkProviderInZone(provider, "214", "c");
    }

    private boolean checkProviderIn035(Integer providerIdt, NoticeXml notice) {
        String provider035 = this.getProviderFor035(providerIdt).block();
        return provider035 != null && notice.checkProviderIn035a(provider035);

    }
}
