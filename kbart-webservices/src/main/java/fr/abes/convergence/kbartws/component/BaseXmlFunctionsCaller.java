package fr.abes.convergence.kbartws.component;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLRecoverableException;
import java.text.DecimalFormat;

@Slf4j
@Component
public class BaseXmlFunctionsCaller {
    @Autowired
    private JdbcTemplate baseXmlJdbcTemplate;

    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String issnToPpn(String issn) throws SQLRecoverableException, UncategorizedSQLException {
            long startTime = System.nanoTime();
        StringBuilder request = new StringBuilder("SELECT AUTORITES.ISSN2PPNJSON('");
        request.append(issn);
        request.append("') as data_xml from DUAL");
        String result = baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
            long endTime = System.nanoTime();
            // Temps d'exécution en secondes
            double duration = (endTime - startTime) / 1_000_000_000.0;
            DecimalFormat df = new DecimalFormat("#.##");
            log.trace("Temps d'exécution : ISSN2PPNJSON(" + issn + ")" + Double.valueOf(df.format(duration)) + " secondes");
        return result;
    }

    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String isbnToPpn(String isbn) throws SQLRecoverableException, UncategorizedSQLException {
            long startTime = System.nanoTime();
        StringBuilder request = new StringBuilder("SELECT AUTORITES.ISBN2PPNJSON('");
        request.append(isbn);
        request.append("') as data_xml from DUAL");
        String result =  baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
            long endTime = System.nanoTime();
            // Temps d'exécution en secondes
            double duration = (endTime - startTime) / 1_000_000_000.0;
            DecimalFormat df = new DecimalFormat("#.##");
            log.trace("Temps d'exécution : ISSN2PPNJSON(" + isbn + ")" + Double.valueOf(df.format(duration)) + " secondes");
        return result;
    }

    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String baconProvider035(Integer provider) throws SQLRecoverableException, UncategorizedSQLException {
            long startTime = System.nanoTime();
        StringBuilder request = new StringBuilder("SELECT AUTORITES.BACON_PROVIDER_035_JSON(");
        request.append(provider);
        request.append(") as data_xml from DUAL");
        String result = baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
            long endTime = System.nanoTime();
            // Temps d'exécution en secondes
            double duration = (endTime - startTime) / 1_000_000_000.0;
            DecimalFormat df = new DecimalFormat("#.##");
            log.trace("Temps d'exécution : BACON_PROVIDER_035_JSON(" + provider + ")" + Double.valueOf(df.format(duration)) + " secondes");
        return result;
    }

    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    public String doiToPpn(String doi) throws SQLRecoverableException, UncategorizedSQLException {
            long startTime = System.nanoTime();
        StringBuilder request = new StringBuilder("select XMLTRANSFORM(XMLROOT(XMLElement(\"sudoc\",AUTORITES.DOI2PNN('");
        request.append(doi);
        request.append("')),version '1.0\" encoding=\"UTF-8'),lexsl) from xsl_html  where idxsl='JSON'");
        String result = baseXmlJdbcTemplate.queryForObject(request.toString(), String.class);
            long endTime = System.nanoTime();
            // Temps d'exécution en secondes
            double duration = (endTime - startTime) / 1_000_000_000.0;
            DecimalFormat df = new DecimalFormat("#.##");
            log.trace("Temps d'exécution : DOI2PNN(" + doi + ")" + Double.valueOf(df.format(duration)) + " secondes");
        return result;
    }
}