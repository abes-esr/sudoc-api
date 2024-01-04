package fr.abes.convergence.kbartws.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.convergence.kbartws.component.BaseXmlFunctionsCaller;
import fr.abes.convergence.kbartws.configuration.UtilsConfig;
import fr.abes.convergence.kbartws.repository.ProviderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.sql.SQLRecoverableException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ProviderService.class, BaseXmlFunctionsCaller.class, UtilsConfig.class})
public class ProviderServiceTest {
    @Autowired
    ProviderService service;

    @MockBean
    BaseXmlFunctionsCaller caller;

    @MockBean
    ProviderRepository providerRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getProviderFor035Test() throws SQLRecoverableException, IOException {
        String resultWs = "{\"bacon\":{\"query\":{\"idt_provider\":\"81\",\"results\":{\"element\":{\"valeur_035\":\"FRCAIRN\"}}}}}";
        Mockito.when(caller.baconProvider035(Mockito.anyInt())).thenReturn(resultWs);

        List<String> result = service.getProviderFor035(81);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("FRCAIRN", result.get(0));
    }

    @Test
    void getProviderFor035TestWithPipe() throws SQLRecoverableException, IOException {
        String resultWs = "{\"bacon\":{\"query\":{\"idt_provider\":\"81\",\"results\":{\"element\":{\"valeur_035\":\"PROQUEST_|eeboln\"}}}}}";
        Mockito.when(caller.baconProvider035(Mockito.anyInt())).thenReturn(resultWs);

        List<String> result = service.getProviderFor035(81);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("PROQUEST_", result.get(0));
        Assertions.assertEquals("eeboln", result.get(1));
    }

    @Test
    void getProviderFor035TestWithNoResult() throws SQLRecoverableException, IOException {
        String resultWs = "{\"bacon\":{\"query\":{\"idt_provider\":\"81\",\"results\":null}}}";
        Mockito.when(caller.baconProvider035(Mockito.anyInt())).thenReturn(resultWs);

        List<String> result = service.getProviderFor035(81);
        Assertions.assertEquals(0, result.size());
    }
}
