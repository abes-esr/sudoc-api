package fr.abes.convergence.kbartws.service;

import fr.abes.convergence.kbartws.dto.provider.ResultProviderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderService {
    private final WsService wsService;

    public Optional<String> getProviderDisplayName(String shortName) throws IOException {
        ResultProviderDto result = wsService.callProviderList();
        return Arrays.stream(result.getBacon().getQuery().getResults()).toList().stream().filter(el -> el.getElements().getProvider().equalsIgnoreCase(shortName)).map(res -> res.getElements().getDisplayName()).findFirst();
    }
}
