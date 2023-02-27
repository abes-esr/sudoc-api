package fr.abes.convergence.kbartws.service;

import fr.abes.convergence.kbartws.component.BaseXmlFunctionsCaller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {IsbnService.class, BaseXmlFunctionsCaller.class})
class IsbnServiceTest {

    @Autowired
    IsbnService isbnService;

    @MockBean
    BaseXmlFunctionsCaller caller;

    @Test
    void checkFormat() {
        String isbn = "";

        Assertions.assertFalse(isbnService.checkFormat(isbn));
    }

}
