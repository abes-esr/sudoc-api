package fr.abes.convergence.kbartws.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BaseXmlFunctionsCaller {
    @Autowired
    private JdbcTemplate baseXmlJdbcTemplate;

    public String issnToPpn(String issn){
        StringBuilder request = new StringBuilder("SELECT AUTORITES.ISSN2PPNJSON")
    }
}

