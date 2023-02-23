package fr.abes.convergence.kbartws.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BaseXmlFunctionsCaller {
    @Autowired
    private JdbcTemplate baseXmlJdbcTemplate;

    public List<String> issnToPpn(String issn){
        StringBuilder request = new StringBuilder("SELECT AUTORITES.ISSN2PPNJSON('");
        request.append(issn);
        request.append("') from DUAL;");
        return baseXmlJdbcTemplate.queryForList(request.toString(), String.class);
    }

    public List<String> isbnToPpn(String isbn) {
        StringBuilder request = new StringBuilder("SELECT AUTORITES.ISBN2PPNJSON('");
        request.append(isbn);
        request.append("') from DUAL;");
        return baseXmlJdbcTemplate.queryForList(request.toString(), String.class);
    }
}

