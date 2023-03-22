package fr.abes.convergence.kbartws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.convergence.kbartws.dto.provider.BaconDto;
import fr.abes.convergence.kbartws.dto.provider.QueryDto;
import fr.abes.convergence.kbartws.dto.provider.ResultDto;
import fr.abes.convergence.kbartws.dto.provider.ResultProviderDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootTest(classes = {WsService.class, ObjectMapper.class, RestTemplate.class})
public class WsServiceTest {
    @Value("${url.provider_list}")
    String urlProvider;

    @Autowired
    WsService wsService;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RestTemplate restTemplate;

    @Test
    void getCallTest() {
        String url = "http://www.serviceTest.com/service/type/id";
        Mockito.when(restTemplate.getForObject(url, String.class)).thenReturn("test");

        Assertions.assertEquals("test", wsService.getCall("http://www.serviceTest.com/service", "type", "id"));
    }

    @Test
    void postCallTest() {
        String url = "http://www.serviceTest.com/service/";
        StringBuilder json = new StringBuilder("{\n");
        json.append("\"test\":\"test\"\n");
        json.append("}");

        Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn("test");

        Assertions.assertEquals("test", wsService.postCall(url, json.toString()));
    }

    @Test
    void callProviderList() throws JsonProcessingException {
        StringBuilder json = new StringBuilder("{\n");
        json.append("\"bacon\": {");
        json.append("   \"query\": {");
        json.append("       \"id\": \"list\",");
        json.append("       \"results\": [");
        json.append("           {");
        json.append("               \"element\": {");
        json.append("                   \"provider\": \"AAAS\",");
        json.append("                   \"display_name\": \"American Association for the Advancement of Science\",");
        json.append("                   \"idprovider\": 581");
        json.append("               }");
        json.append("           },");
        json.append("           {");
        json.append("               \"element\": {");
        json.append("                   \"provider\": \"AACR\",");
        json.append("                   \"display_name\": \"American Association for Cancer Research\",");
        json.append("                   \"idprovider\": 523");
        json.append("               }");
        json.append("           }");
        json.append("       ]");
        json.append("   }");
        json.append("}");
        json.append("}");

        Mockito.when(restTemplate.getForObject(urlProvider, String.class)).thenReturn(json.toString());

        ResultProviderDto result = wsService.callProviderList();

        Assertions.assertEquals(2, result.getBacon().getQuery().getResults().length);
        Assertions.assertEquals("American Association for the Advancement of Science", result.getBacon().getQuery().getResults()[0].getElements().getDisplayName());
        Assertions.assertEquals("American Association for Cancer Research", result.getBacon().getQuery().getResults()[1].getElements().getDisplayName());

    }
}
