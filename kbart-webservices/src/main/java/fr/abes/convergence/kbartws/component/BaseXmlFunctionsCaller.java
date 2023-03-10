package fr.abes.convergence.kbartws.component;

import org.hibernate.annotations.ColumnTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BaseXmlFunctionsCaller {
    @Autowired
    private JdbcTemplate baseXmlJdbcTemplate;

    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String issnToPpn(String issn) throws UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT AUTORITES.ISSN2PPNJSON('");
        request.append(issn);
        request.append("') as data_xml from DUAL");
        return baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
    }

    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String isbnToPpn(String isbn) throws UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT AUTORITES.ISBN2PPNJSON('");
        request.append(isbn);
        request.append("') as data_xml from DUAL");
        return baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
    }
}

