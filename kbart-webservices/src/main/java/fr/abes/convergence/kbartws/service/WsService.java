package fr.abes.convergence.kbartws.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.convergence.kbartws.dto.provider.ResultDto;
import fr.abes.convergence.kbartws.dto.provider.ResultProviderDto;
import fr.abes.convergence.kbartws.utils.ExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class WsService {
    @Value("${url.provider_list}")
    private String providerList;

    private final RestTemplate restTemplate;
    private final HttpHeaders headers;

    private final ObjectMapper mapper;

    public WsService(ObjectMapper mapper, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        this.mapper = mapper;
    }

    public String postCall(String url, String requestJson) {
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate.postForObject(url, entity, String.class);
    }

    public String getCall(String url, String... params) {
        StringBuilder formedUrl = new StringBuilder(url);
        for (String param : params) {
            formedUrl.append("/");
            formedUrl.append(param);
        }
        log.debug(formedUrl.toString());
        return restTemplate.getForObject(formedUrl.toString(), String.class);
    }

    @ExecutionTime
    public ResultProviderDto callProviderList() throws RestClientResponseException, JsonProcessingException {
        String result = getCall(providerList);
        return mapper.readValue(result, ResultProviderDto.class);
    }

}
