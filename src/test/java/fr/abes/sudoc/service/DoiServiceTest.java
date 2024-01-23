package fr.abes.sudoc.service;

import fr.abes.sudoc.component.BaseXmlFunctionsCaller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = DoiService.class)
class DoiServiceTest {
    @Autowired
    DoiService doiService;

    @MockBean
    BaseXmlFunctionsCaller caller;

    @Test
    void checkFormat() {
        String doi = "10.1006/jmbi.1998.2354";
        Assertions.assertTrue(doiService.checkFormat(doi));

        Assertions.assertFalse(doiService.checkFormat(null));

        doi = "tekjlfhsdlkjf";
        Assertions.assertFalse(doiService.checkFormat(doi));
    }
}