package fr.abes.sudoc.service;

import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DoiService.class)
class DoiServiceTest {
    @Autowired
    DoiService doiService;

    @MockitoBean
    BaseXmlFunctionsCaller caller;

    @Test
    void checkFormat() {
        doiService.checkFormat("10.1006/jmbi.1998.2354");

        Assertions.assertThrows(IllegalArgumentException.class,() -> doiService.checkFormat(null));

        Assertions.assertThrows(IllegalArgumentException.class,() -> doiService.checkFormat("tekjlfhsdlkjf"));
    }
}