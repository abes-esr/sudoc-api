package fr.abes.sudoc.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLRecoverableException;
import java.util.List;

@Slf4j
@Component
public class BaseXmlFunctionsCaller {
    private final JdbcTemplate baseXmlJdbcTemplate;

    public BaseXmlFunctionsCaller(JdbcTemplate baseXmlJdbcTemplate) {
        this.baseXmlJdbcTemplate = baseXmlJdbcTemplate;
    }


    public List<String> issnToPpn(String issn) throws UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT distinct ppn from AUTORITES.biblio_table_fouretout where cle1='ISSN' and cle2='");
        request.append(issn);
        request.append("'");
        return baseXmlJdbcTemplate.queryForList(request.toString(), String.class);
    }


    public List<String> isbnToPpn(String isbn) throws UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT distinct ppn from AUTORITES.biblio_table_fouretout where cle1='ISBN' and cle2='");
        request.append(isbn);
        request.append("'");
        return baseXmlJdbcTemplate.queryForList(request.toString(), String.class);
    }


    public String baconProvider035(Integer provider) throws SQLRecoverableException, UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT AUTORITES.BACON_PROVIDER_035_JSON(");
        request.append(provider);
        request.append(") as data_xml from DUAL");
        return baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
    }


    public List<String> doiToPpn(String doi) throws UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT distinct ppn from AUTORITES.biblio_table_fouretout where cle1='DOI' and cle2='");
        request.append(doi.toLowerCase());
        request.append("'");
        return baseXmlJdbcTemplate.queryForList(request.toString(), String.class);
    }
}
