package fr.abes.sudoc.service;

import fr.abes.sudoc.entity.Provider035;
import fr.abes.sudoc.repository.Provider035Repository;
import fr.abes.sudoc.repository.ProviderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ProviderService.class})
public class ProviderServiceTest {
    @Autowired
    ProviderService service;

    @MockBean
    Provider035Repository provider035Repository;

    @MockBean
    ProviderRepository providerRepository;


    @Test
    void getProviderFor035Test() {
        Provider035 provider = new Provider035();
        provider.setId(81);
        provider.setValeur("FRCAIRN");
        Mockito.when(provider035Repository.findById(81)).thenReturn(Optional.of(provider));

        String result = service.getProviderFor035(81);
        Assertions.assertEquals("FRCAIRN", result);
    }


    @Test
    void getProviderFor035TestWithNoResult() {
        Mockito.when(provider035Repository.findById(81)).thenReturn(Optional.empty());

        String result = service.getProviderFor035(81);
        Assertions.assertNull(result);
    }
}
