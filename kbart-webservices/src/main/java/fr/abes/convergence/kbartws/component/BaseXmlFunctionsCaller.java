package fr.abes.convergence.kbartws.component;

import org.hibernate.annotations.ColumnTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLRecoverableException;

@Component
public class BaseXmlFunctionsCaller {
    @Autowired
    private JdbcTemplate baseXmlJdbcTemplate;

    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String issnToPpn(String issn) throws SQLRecoverableException, UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT AUTORITES.ISSN2PPNJSON('");
        request.append(issn);
        request.append("') as data_xml from DUAL");
        return baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
    }

    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String isbnToPpn(String isbn) throws SQLRecoverableException, UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT AUTORITES.ISBN2PPNJSON('");
        request.append(isbn);
        request.append("') as data_xml from DUAL");
        return baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
    }

    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String baconProvider035(String provider) throws SQLRecoverableException, UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT AUTORITES.BACON_PROVIDER_035_JSON('");
        request.append(provider);
        request.append("') as data_xml from DUAL");
        return baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
    }

    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String doiToPpn(String doi) throws SQLRecoverableException, UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT AUTORITES.DOI2PPN('");
        request.append(doi);
        request.append("') as data_xml from DUAL");
        return baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
    }
}

