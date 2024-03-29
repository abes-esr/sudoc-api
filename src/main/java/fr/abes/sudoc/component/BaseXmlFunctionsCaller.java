package fr.abes.sudoc.component;

import fr.abes.sudoc.utils.ExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLRecoverableException;

@Slf4j
@Component
public class BaseXmlFunctionsCaller {
    private final JdbcTemplate baseXmlJdbcTemplate;

    public BaseXmlFunctionsCaller(JdbcTemplate baseXmlJdbcTemplate) {
        this.baseXmlJdbcTemplate = baseXmlJdbcTemplate;
    }

    @ExecutionTime
    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String issnToPpn(String issn) throws SQLRecoverableException, UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT AUTORITES.ISSN2PPNJSON('");
        request.append(issn);
        request.append("') as data_xml from DUAL");
        return baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
    }

    @ExecutionTime
    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String isbnToPpn(String isbn) throws SQLRecoverableException, UncategorizedSQLException {
            StringBuilder request = new StringBuilder("SELECT AUTORITES.ISBN2PPNJSON('");
            request.append(isbn);
            request.append("') as data_xml from DUAL");
        return baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
    }

    @ExecutionTime
    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String baconProvider035(Integer provider) throws SQLRecoverableException, UncategorizedSQLException {
        StringBuilder request = new StringBuilder("SELECT AUTORITES.BACON_PROVIDER_035_JSON(");
        request.append(provider);
        request.append(") as data_xml from DUAL");
        return baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
    }

    @ExecutionTime
    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String doiToPpn(String doi) throws SQLRecoverableException, UncategorizedSQLException {
        StringBuilder request = new StringBuilder("select XMLTRANSFORM(XMLROOT(XMLElement(\"sudoc\",AUTORITES.DOI2PNN('");
        request.append(doi);
        request.append("')),version '1.0\" encoding=\"UTF-8'),lexsl) from xsl_html  where idxsl='JSON'");
        return baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
    }
}
